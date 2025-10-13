import { useQueryClient } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";
import { components } from "@/shared/api/schema/generated";

type CreateUserRequest = components["schemas"]["CreateUserRequest"];

export function useCreateUser() {
    const queryClient = useQueryClient();

    return rqClient.useMutation("post", "/api/users", {
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["users"] });
        },
    });
}