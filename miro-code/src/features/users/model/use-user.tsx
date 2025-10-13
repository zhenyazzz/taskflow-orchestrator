// features/users/model/use-user.ts
import { useQueryClient } from "@tanstack/react-query";
import { keepPreviousData } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";

export function useUser(userId?: string) {
  return rqClient.useQuery("get", "/api/users/{id}", {
    params: {
      path: {
        id: userId!,
      },
    },
  }, {
    enabled: !!userId,
    placeholderData: keepPreviousData,
  });
}

export function useUpdateUser() {
  const queryClient = useQueryClient();

  return rqClient.useMutation("put", "/api/users/{id}", {
    onSuccess: (response, variables) => {
      const updatedUser = response.data;
      
      // Обновляем данные конкретного пользователя
      queryClient.setQueryData(["user", variables.params.path.id], updatedUser);
      
      // Инвалидируем список пользователей
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();

  return rqClient.useMutation("delete", "/api/users/{id}", {
    onSuccess: (_, variables) => {
      const userId = variables.params.path.id;
      // Удаляем пользователя из кеша
      queryClient.removeQueries({ queryKey: ["user", userId] });
      // Инвалидируем список пользователей
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
}