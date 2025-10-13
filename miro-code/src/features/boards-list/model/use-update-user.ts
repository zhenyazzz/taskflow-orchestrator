import { useQueryClient } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";
import { components } from "@/shared/api/schema/generated";

type UpdateUserRequest = components["schemas"]["UpdateUserRequest"];

export function useUpdateUser() {
    const queryClient = useQueryClient();

    return rqClient.useMutation("put", "/api/users/{id}", {
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["currentUser"] });
            queryClient.invalidateQueries({ queryKey: ["users"] });
        },
    });
}