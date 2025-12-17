import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { fetchClient } from "@/shared/api/instance";
import { components } from "@/shared/api/schema/generated";

type UserAnalyticsResponse = components["schemas"]["UserAnalyticsResponse"];

interface DateRangeParams {
  startDate?: string;
  endDate?: string;
}

export function useUserAnalytics(userId: string, params?: DateRangeParams) {
  return useQuery({
    queryKey: ["analytics", "users", userId, params],
    queryFn: async () => {
      const response = await fetchClient.GET("/analytics/users", {
        params: {
          query: {
            userId,
            ...params,
          },
        },
      });

      if (response.error) {
        throw new Error(response.error.message || "Failed to fetch user analytics");
      }
      return response.data as UserAnalyticsResponse;
    },
    enabled: Boolean(userId),
    placeholderData: keepPreviousData,
  });
}

