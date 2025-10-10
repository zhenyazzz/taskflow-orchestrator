import { useQuery } from "@tanstack/react-query";
import { UserResponse } from "@/shared/api/types";

export function useCurrentUser() {
    const fetchCurrentUser = async (): Promise<UserResponse> => {
        const response = await fetch("/api/users/me", {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (!response.ok) throw new Error("Failed to fetch current user");
        return response.json();
    };

    const { data: user, isLoading, error } = useQuery({
        queryKey: ["currentUser"],
        queryFn: fetchCurrentUser,
    });

    return { user, isLoading, error };
}