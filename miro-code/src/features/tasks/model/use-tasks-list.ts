import { useState, useRef, useEffect } from "react";
import { useInfiniteQuery } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";
import { TaskResponse, TaskPageResponse } from "@/shared/api/schema/generated";

interface UseTasksListParams {
  page?: number;
  size?: number;
  status?: string;
  assigneeId?: string;
  creatorId?: string;
  department?: string;
}

export function useTasksList(params: UseTasksListParams = {}) {
  const cursorRef = useRef<HTMLDivElement | null>(null);
  
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isPending,
    isError,
    error,
  } = useInfiniteQuery({
    queryKey: ["get", "/tasks", params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await rqClient.GET("/tasks", {
        params: {
          query: {
            page: pageParam,
            size: params.size || 10,
            status: params.status,
            assigneeId: params.assigneeId,
            creatorId: params.creatorId,
            department: params.department,
          },
        },
      });
      
      if (response.error) {
        throw new Error(response.error.message || "Failed to fetch tasks");
      }
      
      return response.data as TaskPageResponse;
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.last || !lastPage.content.length) {
        return undefined;
      }
      return lastPage.number + 1;
    },
    initialPageParam: 0,
  });

  const tasks: TaskResponse[] = data?.pages.flatMap(page => page.content) || [];

  // Intersection Observer for infinite scroll
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasNextPage && !isFetchingNextPage) {
          fetchNextPage();
        }
      },
      { threshold: 0.1 }
    );

    if (cursorRef.current) {
      observer.observe(cursorRef.current);
    }

    return () => observer.disconnect();
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  return {
    tasks,
    isPending,
    isLoading,
    isError,
    error,
    hasNextPage,
    isFetchingNextPage,
    cursorRef,
  };
}
