import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { fetchClient } from "@/shared/api/instance";
import { components } from "@/shared/api/schema/generated";

type TaskAnalyticsResponse = components["schemas"]["TaskAnalyticsResponse"];

interface DateRangeParams {
  startDate?: string;
  endDate?: string;
}

export function useTaskAnalytics(params?: DateRangeParams) {
  return useQuery({
    queryKey: ["analytics", "tasks", params],
    queryFn: async () => {
      const response = await fetchClient.GET("/analytics/tasks", {
        params: {
          query: params || {},
        },
      });

      if (response.error) {
        throw new Error(response.error.message || "Failed to fetch task analytics");
      }
      return response.data as TaskAnalyticsResponse;
    },
    placeholderData: keepPreviousData,
  });
}

