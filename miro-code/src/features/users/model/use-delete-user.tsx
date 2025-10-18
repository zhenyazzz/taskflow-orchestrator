// features/users/model/use-user.ts
import { keepPreviousData } from "@tanstack/react-query";
import { rqClient } from "@/shared/api/instance";

export function useDeleteUser(userId?: string) {
    return rqClient.useQuery("get", "/users/{id}", {
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

