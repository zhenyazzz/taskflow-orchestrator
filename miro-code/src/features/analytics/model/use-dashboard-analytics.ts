import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { fetchClient } from "@/shared/api/instance";
import { components } from "@/shared/api/schema/generated";

type DashboardAnalyticsResponse = components["schemas"]["DashboardAnalyticsResponse"];

interface DateRangeParams {
  startDate?: string;
  endDate?: string;
}

export function useDashboardAnalytics(params?: DateRangeParams) {
  return useQuery({
    queryKey: ["analytics", "dashboard", params],
    queryFn: async () => {
      const response = await fetchClient.GET("/analytics/dashboard", {
        params: {
          query: params || {},
        },
      });

      if (response.error) {
        throw new Error(response.error.message || "Failed to fetch dashboard analytics");
      }
      return response.data as DashboardAnalyticsResponse;
    },
    placeholderData: keepPreviousData,
  });
}

