import { rqClient } from "@/shared/api/instance";

interface UseUserOptions {
    userId: string;
}

export function useUser({ userId }: UseUserOptions) {
    return rqClient.useQuery("get", "/api/users/{id}", {
        params: {
            path: {
                id: userId,
            },
        },
    }, {
        enabled: !!userId,
    });
}