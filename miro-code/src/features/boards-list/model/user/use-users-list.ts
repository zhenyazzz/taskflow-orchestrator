import { RefCallback, useCallback } from "react";
import { keepPreviousData } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";
import { useDebouncedValue } from "@/shared/lib/react";

interface UseUsersListProps {
  size?: number;
  sort?: string;
  username?: string;
  role?: "USER" | "ADMIN" | null;
}

export function useUsersList({
  size = 20,
  sort,
  username: rawUsername,
  role,
}: UseUsersListProps) {
  const username = useDebouncedValue(rawUsername, 300);

  const { 
    fetchNextPage, 
    data, 
    isFetchingNextPage, 
    isPending, 
    hasNextPage 
  } = rqClient.useInfiniteQuery({
    queryKey: [
      "/users", 
      { 
        size, 
        sort, 
        username, 
        role: role || undefined 
      }
    ],
    queryFn: ({ pageParam = 0 }) => 
      rqClient.request({
        method: "get",
        url: "/users",
        params: {
          query: {
            page: pageParam,
            size,
            sort,
            username,
            role: role || undefined,
          },
        },
      }),
    initialPageParam: 0,
    getNextPageParam: (lastPage, _, lastPageParam) => {
      const currentPage = lastPageParam as number;
      return currentPage < lastPage.totalPages - 1 
        ? currentPage + 1 
        : null;
    },
    placeholderData: keepPreviousData,
  });

  const cursorRef: RefCallback<HTMLDivElement> = useCallback(
    (el) => {
      if (!el || !hasNextPage) return;

      const observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && hasNextPage && !isFetchingNextPage) {
            fetchNextPage();
          }
        },
        { threshold: 0.5 },
      );

      observer.observe(el);

      return () => {
        observer.disconnect();
      };
    },
    [fetchNextPage, hasNextPage, isFetchingNextPage],
  );

  const users = data?.pages.flatMap((page) => page.content) ?? [];

  return {
    users,
    isFetchingNextPage,
    isPending,
    hasNextPage,
    cursorRef,
  };
}