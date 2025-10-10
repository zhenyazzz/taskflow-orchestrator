import { useQuery } from "@tanstack/react-query";
import { fetchClient } from "@/shared/api/instance";
import { UserResponse } from "@/shared/api/types";

interface UseUserOptions {
    userId: string;
}

export function useUser({ userId }: UseUserOptions) {
    return useQuery<UserResponse>({
        queryKey: ["user", userId],
        queryFn: async () => {
            const response = await fetch(`/api/users/${userId}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            });
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || "Failed to fetch user");
            }
            return response.json();
        },
        enabled: !!userId,
    });
}