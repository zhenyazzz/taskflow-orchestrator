import { RefCallback, useCallback } from "react";
import { keepPreviousData } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";
import { useDebouncedValue } from "@/shared/lib/react"; // Assuming this path

interface UseUsersListProps {
  limit?: number;
  sort?: string;
  search?: string;
  status?: string | null;
  role?: string | null;
}

export function useUsersList({
  limit = 20,
  sort,
  search: rawSearch,
  status,
  role,
}: UseUsersListProps) {
  const search = useDebouncedValue(rawSearch, 300);

  const { fetchNextPage, data, isFetchingNextPage, isPending, hasNextPage } =
    rqClient.useInfiniteQuery(
      "get",
      
      "/users",
      {
        params: {
          query: {
            page: 1,
            limit,
            sort,
            search,
            status,
            role,
          },
        },
      },
      {
        initialPageParam: 1,
        pageParamName: "page",
        getNextPageParam: (lastPage, _, lastPageParams) =>
          Number(lastPageParams) < lastPage.totalPages
            ? Number(lastPageParams) + 1
            : null,

        placeholderData: keepPreviousData,
      },
    );

  const cursorRef: RefCallback<HTMLDivElement> = useCallback(
    (el) => {
      const observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            fetchNextPage();
          }
        },
        { threshold: 0.5 },
      );

      if (el) {
        observer.observe(el);

        return () => {
          observer.disconnect();
        };
      }
    },
    [fetchNextPage],
  );

  const users = data?.pages.flatMap((page) => page.items) ?? [];

  return {
    users,
    isFetchingNextPage,
    isPending,
    hasNextPage,
    cursorRef,
  };
}