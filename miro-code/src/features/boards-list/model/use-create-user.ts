import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CreateUserRequest } from "@/shared/api/types";

export function useCreateUser() {
    const queryClient = useQueryClient();

    const mutation = useMutation({
        mutationFn: async (userData?: CreateUserRequest) => {
            const defaultUser: CreateUserRequest = userData || {
                username: `user${Date.now()}`,
                password: "defaultPassword123",
                email: `user${Date.now()}@example.com`,
                firstName: "New",
                lastName: "User",
                roles: ["USER"],
            };
            const response = await fetch("/api/users", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
                body: JSON.stringify(defaultUser),
            });
            if (!response.ok) throw new Error("Failed to create user");
            return response.json();
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["users"] });
        },
    });

    return {
        createUser: mutation.mutate,
        isPending: mutation.isPending,
    };
}