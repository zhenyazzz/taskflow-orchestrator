import { useQueryClient } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";

export function useDeleteUser() {
    const queryClient = useQueryClient();

    const deleteMutation = rqClient.useMutation("delete", "/users/{id}", {
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["get", "/users"] });
        },
    });

    const deleteUser = (userId: string) => {
        // ✅ ПРАВИЛЬНО - используем params вместо path
        deleteMutation.mutate({
            params: {
                path: { id: userId }
            }
        });
    };

    return {
        deleteUser,
        isPending: deleteMutation.isPending,
        errorMessage: deleteMutation.error?.message,
    };
}