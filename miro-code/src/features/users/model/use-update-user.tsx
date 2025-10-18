// features/users/model/use-user.ts

import { rqClient } from "@/shared/api/instance";
import { useQueryClient } from "@tanstack/react-query";

export function useUpdateUser(userId: string) {
  const queryClient = useQueryClient();

  return rqClient.useMutation("put", "/users/{id}", {
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["get", "/users", { id: userId }] });
      queryClient.invalidateQueries({ queryKey: ["get", "/users"] });
    },
  });
}

