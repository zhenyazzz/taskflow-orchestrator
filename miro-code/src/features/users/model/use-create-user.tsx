import { rqClient } from "@/shared/api/instance";
import { useQueryClient } from "@tanstack/react-query";

export function useCreateUser(onSuccessCallback?: () => void) {
    const queryClient = useQueryClient();

    const createUserMutation = rqClient.useMutation("post", "/users", {
        onSuccess: (data) => {
            console.log("✅ User created:", data);
            queryClient.invalidateQueries({ queryKey: ["get", "/users"] });
            if (onSuccessCallback) {
                onSuccessCallback();
            }
        },
        onError: (error: any) => {
            console.error("❌ User creation failed:", error);
        },
    });

    const createUser = (data: any) => {
        console.log("📤 Sending data:", data);

        createUserMutation.mutate({ body: data });
    };

    return {
        createUser,
        isPending: createUserMutation.isPending,
        errorMessage: createUserMutation.error?.message,
        data: createUserMutation.data,
    };
}