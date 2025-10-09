// features/users/model/use-user.ts
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { keepPreviousData } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";
import type { paths } from "@/shared/api/schema"; // ваши сгенерированные типы

// Типы из сгенерированной схемы
type User = components["schemas"]["User"];
type UpdateUserRequest = components["schemas"]["UpdateUserRequest"];
type CreateUserRequest = components["schemas"]["CreateUserRequest"];

export function useUser(userId?: string) {
  return useQuery({
    queryKey: ["user", userId],
    queryFn: () => 
      rqClient.get("/users/{id}", {
        params: {
          path: {
            id: userId!,
          },
        },
      }) as Promise<{ data: User }>,
    enabled: !!userId,
    placeholderData: keepPreviousData,
    select: (response) => response.data,
  });
}

export function useUpdateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ userId, data }: { userId: string; data: UpdateUserRequest }) =>
      rqClient.put("/users/{id}", {
        params: {
          path: {
            id: userId,
          },
        },
        body: data,
      }) as Promise<{ data: User }>,
    onSuccess: (response, variables) => {
      const updatedUser = response.data;
      
      // Обновляем данные конкретного пользователя
      queryClient.setQueryData(["user", variables.userId], { data: updatedUser });
      
      // Инвалидируем список пользователей
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userId: string) =>
      rqClient.delete("/users/{id}", {
        params: {
          path: {
            id: userId,
          },
        },
      }),
    onSuccess: (_, userId) => {
      // Удаляем пользователя из кеша
      queryClient.removeQueries({ queryKey: ["user", userId] });
      // Инвалидируем список пользователей
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
}