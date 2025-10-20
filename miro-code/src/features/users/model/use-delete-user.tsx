// features/users/model/use-delete-user.ts
import { rqClient } from "@/shared/api/instance";
import { useQueryClient } from "@tanstack/react-query";

export function useDeleteUser() {
  const queryClient = useQueryClient();

  const deleteMutation = rqClient.useMutation("delete", "/users/{id}", {
    onSuccess: () => {
      // Обновляем кеш после удаления
      queryClient.invalidateQueries({ queryKey: ["get", "/users"] });
    },
  });

  const deleteUser = (userId: string) => {
    deleteMutation.mutate({ path: { id: userId } });
  };

  return {
    deleteUser,
    isPending: deleteMutation.isPending,
    errorMessage: deleteMutation.error?.message,
  };
}