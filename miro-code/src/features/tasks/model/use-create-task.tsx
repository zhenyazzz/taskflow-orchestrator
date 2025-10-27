import { rqClient } from "@/shared/api/instance";
import { useQueryClient } from "@tanstack/react-query";
import type { components } from "@/shared/api/schema/generated";

type CreateTaskRequest = components["schemas"]["CreateTaskRequest"];

export function useCreateTask(onSuccessCallback?: () => void) {
  const queryClient = useQueryClient();

  const createTaskMutation = rqClient.useMutation("post", "/tasks", {
    onSuccess: (data) => {
      console.log("✅ Task created:", data);
      queryClient.invalidateQueries({ queryKey: ["get", "/tasks"] });
      if (onSuccessCallback) {
        onSuccessCallback();
      }
    },
    onError: (error: any) => {
      console.error("❌ Task creation failed:", error);
    },
  });

  const createTask = (data: CreateTaskRequest) => {
    console.log("📤 Sending task data:", data);

    createTaskMutation.mutate({ body: data });
  };

  return {
    createTask,
    isPending: createTaskMutation.isPending,
    isSuccess: createTaskMutation.isSuccess,
    errorMessage: createTaskMutation.error?.message,
    data: createTaskMutation.data,
    reset: createTaskMutation.reset,
  };
}
