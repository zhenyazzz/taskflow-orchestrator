// features/users/model/use-user.ts
import { rqClient } from "@/shared/api/instance";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export function useDeleteUser() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return rqClient.useMutation("delete", "/users/{id}", {
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["get", "/users"] });
      navigate("/users");
    },
  });
}

