import { useMutation, useQueryClient } from "@tanstack/react-query";

export function useDeleteUser() {
    const queryClient = useQueryClient();

    const mutation = useMutation({
        mutationFn: async (userId: string) => {
            const response = await fetch(`/api/users/${userId}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            });
            if (!response.ok) throw new Error("Failed to delete user");
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["users"] });
        },
    });

    return {
        deleteUser: mutation.mutate,
        isPending: mutation.isPending,
    };
}