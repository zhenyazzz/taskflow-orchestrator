import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CreateUserRequest } from "@/shared/api/types";

export function useCreateUser() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (userData: CreateUserRequest) => {
            const response = await fetch("/api/users", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
                body: JSON.stringify(userData),
            });
            if (!response.ok) throw new Error("Failed to create user");
            return response.json();
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["users"] });
        },
    });
}