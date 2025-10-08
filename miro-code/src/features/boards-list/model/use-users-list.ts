import { useEffect, useRef } from "react";
import { useInfiniteQuery } from "@tanstack/react-query";
//import { UserResponse } from "@/shared/api/types";

interface UseUsersListProps {
    sort: string;
    search: string;
    status: string | null;
}

export function useUsersList({ sort, search, status }: UseUsersListProps) {
    const cursorRef = useRef<HTMLDivElement>(null);

    const fetchUsers = async ({ pageParam = null }) => {
        const params = new URLSearchParams({ sort, search });
        if (status) {
            params.append("status", status);
        }
        if (pageParam) {
            params.append("cursor", pageParam);
        }
        const response = await fetch(`/api/users?${params.toString()}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (!response.ok) throw new Error("Failed to fetch users");
        return response.json();
    };

    const { data, isPending, isFetchingNextPage, hasNextPage, fetchNextPage } =
        useInfiniteQuery({
            queryKey: ["users", sort, search, status],
            queryFn: fetchUsers,
            getNextPageParam: (lastPage) => lastPage.nextCursor || null,
            initialPageParam: null,
        });

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && hasNextPage) {
                    fetchNextPage();
                }
            },
            { threshold: 1.0 }
        );

        if (cursorRef.current) observer.observe(cursorRef.current);
        return () => observer.disconnect();
    }, [hasNextPage, fetchNextPage]);

    return {
        users: data?.pages.flatMap((page) => page.items) || [],
        isPending,
        isFetchingNextPage,
        hasNextPage,
        cursorRef,
    };
}