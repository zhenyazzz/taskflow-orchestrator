import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { fetchClient } from "@/shared/api/instance";
import { components } from "@/shared/api/schema/generated";

type LoginAnalyticsResponse = components["schemas"]["LoginAnalyticsResponse"];

interface DateRangeParams {
  startDate?: string;
  endDate?: string;
}

export function useLoginAnalytics(params?: DateRangeParams) {
  return useQuery({
    queryKey: ["analytics", "logins", params],
    queryFn: async () => {
      const response = await fetchClient.GET("/analytics/logins", {
        params: {
          query: params || {},
        },
      });

      if (response.error) {
        throw new Error(response.error.message || "Failed to fetch login analytics");
      }
      return response.data as LoginAnalyticsResponse;
    },
    placeholderData: keepPreviousData,
  });
}

