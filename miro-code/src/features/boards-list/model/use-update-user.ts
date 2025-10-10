import { useMutation, useQueryClient } from "@tanstack/react-query";
import { UpdateUserRequest, UserResponse } from "@/shared/api/types";

export function useUpdateUser() {
    const queryClient = useQueryClient();

    const mutation = useMutation({
        mutationFn: async (data: { id: string; userData: UpdateUserRequest }) => {
            const response = await fetch(`/api/users/${data.id}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
                body: JSON.stringify(data.userData),
            });
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || "Failed to update user");
            }
            return response.json() as Promise<UserResponse>;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["currentUser"] });
            queryClient.invalidateQueries({ queryKey: ["users"] });
        },
    });

    return {
        updateUser: mutation.mutate,
        isPending: mutation.isPending,
        error: mutation.error,
    };
}