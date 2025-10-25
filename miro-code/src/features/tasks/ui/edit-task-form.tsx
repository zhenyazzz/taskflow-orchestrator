import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { Button } from "@/shared/ui/kit/button";
import { Input } from "@/shared/ui/kit/input";
import { Textarea } from "@/shared/ui/kit/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/ui/kit/select";
import { Label } from "@/shared/ui/kit/label";
import { Loader2 } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/shared/ui/kit/alert";
import { AlertCircle } from "lucide-react";
import { useUpdateTask } from "../model/use-update-task";
import { useNavigate } from "react-router-dom";
import { TaskResponse, UpdateTaskRequest } from "@/shared/api/schema/generated";
import { TaskPriority, Department } from "../lib/types";

interface EditTaskFormProps {
  task: TaskResponse;
  onCancel: () => void;
}

const priorityLabels: Record<TaskPriority, string> = {
  LOW: "Низкий",
  MEDIUM: "Средний",
  HIGH: "Высокий",
};

const departmentLabels: Record<Department, string> = {
  IT: "IT",
  HR: "HR",
  FINANCE: "Финансы",
  MARKETING: "Маркетинг",
  SALES: "Продажи",
  CUSTOMER_SERVICE: "Служба поддержки",
  PRODUCTION: "Производство",
  LOGISTICS: "Логистика",
  RESEARCH_AND_DEVELOPMENT: "R&D",
  OTHER: "Другое",
};

export function EditTaskForm({ task, onCancel }: EditTaskFormProps) {
  const { register, handleSubmit, formState: { errors }, reset } = useForm<UpdateTaskRequest>({
    defaultValues: {
      title: task.title,
      description: task.description,
      priority: task.priority,
      department: task.department,
      dueDate: task.dueDate,
      tags: task.tags,
    },
  });

  const navigate = useNavigate();

  const { updateTask, isPending, error } = useUpdateTask(task.id, () => {
    navigate("/tasks"); // Navigate back to tasks list
  });

  const onSubmit = (data: UpdateTaskRequest) => {
    updateTask(data);
  };

  useEffect(() => {
    reset({
      title: task.title,
      description: task.description,
      priority: task.priority,
      department: task.department,
      dueDate: task.dueDate,
      tags: task.tags,
    });
  }, [task, reset]);

  return (
    <form key={task.id} onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {error && (
        <Alert variant="destructive">
          <AlertCircle className="w-4 h-4" />
          <AlertTitle>Ошибка</AlertTitle>
          <AlertDescription>
            {error.message || "Не удалось обновить задачу"}
          </AlertDescription>
        </Alert>
      )}

      <div>
        <Label htmlFor="title" className="block mb-2">Название задачи</Label>
        <Input
          id="title"
          className="mt-1"
          {...register("title", { required: "Название задачи обязательно" })}
        />
        {errors.title && <p className="text-red-500 text-sm">{errors.title.message}</p>}
      </div>

      <div>
        <Label htmlFor="description" className="block mb-2">Описание</Label>
        <Textarea
          id="description"
          className="mt-1"
          rows={3}
          {...register("description")}
        />
      </div>

      <div>
        <Label htmlFor="priority" className="block mb-2">Приоритет</Label>
        <Select
          value={task.priority}
          onValueChange={(value: TaskPriority) => {
            reset({ ...reset(), priority: value });
          }}
        >
          <SelectTrigger className="mt-1">
            <SelectValue placeholder="Выберите приоритет" />
          </SelectTrigger>
          <SelectContent>
            {Object.entries(priorityLabels).map(([priority, label]) => (
              <SelectItem key={priority} value={priority}>
                {label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div>
        <Label htmlFor="department" className="block mb-2">Отдел</Label>
        <Select
          value={task.department}
          onValueChange={(value: Department) => {
            reset({ ...reset(), department: value });
          }}
        >
          <SelectTrigger className="mt-1">
            <SelectValue placeholder="Выберите отдел" />
          </SelectTrigger>
          <SelectContent>
            {Object.entries(departmentLabels).map(([department, label]) => (
              <SelectItem key={department} value={department}>
                {label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div>
        <Label htmlFor="dueDate" className="block mb-2">Срок выполнения</Label>
        <Input
          id="dueDate"
          type="datetime-local"
          className="mt-1"
          {...register("dueDate")}
        />
      </div>

      <div>
        <Label htmlFor="tags" className="block mb-2">Теги (через запятую)</Label>
        <Input
          id="tags"
          placeholder="urgent, backend, frontend"
          className="mt-1"
          value={task.tags.join(", ")}
          onChange={(e) => {
            const tags = e.target.value ? e.target.value.split(",").map(tag => tag.trim()).filter(tag => tag) : [];
            reset({ ...reset(), tags });
          }}
        />
      </div>

      <div className="flex gap-2">
        <Button type="submit" disabled={isPending}>
          {isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : "Сохранить"}
        </Button>
        <Button type="button" variant="outline" onClick={onCancel}>
          Отмена
        </Button>
      </div>
    </form>
  );
}
