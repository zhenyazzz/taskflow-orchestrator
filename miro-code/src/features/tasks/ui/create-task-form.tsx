import { Button } from "@/shared/ui/kit/button";
import { Input } from "@/shared/ui/kit/input";
import { Textarea } from "@/shared/ui/kit/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/ui/kit/select";
import { Label } from "@/shared/ui/kit/label";
import type { components } from "@/shared/api/schema/generated";

type CreateTaskRequest = components["schemas"]["CreateTaskRequest"];
import { TaskPriority, Department } from "../lib/types";
import { FormEvent } from "react";
import { useCreateTask } from "../model/use-create-task";

interface CreateTaskFormProps {
  formData: CreateTaskRequest;
  setFormData: (data: CreateTaskRequest) => void;
  onClose: () => void;
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

export function CreateTaskForm({ formData, setFormData, onClose }: CreateTaskFormProps) {
  const { createTask, isPending, errorMessage } = useCreateTask(onClose);

  const handleSubmit = (e: FormEvent) => {
    console.log("📝 Task form data:", JSON.stringify(formData, null, 2));
    e.preventDefault();
    createTask(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <Label htmlFor="title">Название задачи *</Label>
        <Input
          id="title"
          placeholder="Название задачи"
          value={formData.title}
          onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          required
        />
      </div>

      <div>
        <Label htmlFor="description">Описание</Label>
        <Textarea
          id="description"
          placeholder="Описание задачи"
          value={formData.description || ""}
          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          rows={3}
        />
      </div>

      <div>
        <Label htmlFor="priority">Приоритет *</Label>
        <Select
          value={formData.priority}
          onValueChange={(value: TaskPriority) =>
            setFormData({ ...formData, priority: value })
          }
        >
          <SelectTrigger>
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
        <Label htmlFor="department">Отдел *</Label>
        <Select
          value={formData.department}
          onValueChange={(value: Department) =>
            setFormData({ ...formData, department: value })
          }
        >
          <SelectTrigger>
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
        <Label htmlFor="dueDate">Срок выполнения</Label>
        <Input
          id="dueDate"
          type="datetime-local"
          value={formData.dueDate ? new Date(formData.dueDate).toISOString().slice(0, 16) : ""}
          onChange={(e) => setFormData({ 
            ...formData, 
            dueDate: e.target.value ? new Date(e.target.value).toISOString() : undefined 
          })}
        />
      </div>

      <div>
        <Label htmlFor="tags">Теги (через запятую)</Label>
        <Input
          id="tags"
          placeholder="urgent, backend, frontend"
          value={formData.tags?.join(", ") || ""}
          onChange={(e) => setFormData({ 
            ...formData, 
            tags: e.target.value ? e.target.value.split(",").map(tag => tag.trim()).filter(tag => tag) : []
          })}
        />
      </div>

      {errorMessage && (
        <p className="text-destructive text-sm">{errorMessage}</p>
      )}

      <div className="flex justify-end gap-4">
        <Button type="button" variant="outline" onClick={onClose}>
          Назад
        </Button>
        <Button type="submit" disabled={isPending}>
          {isPending ? 'Создание...' : 'Создать'}
        </Button>
      </div>
    </form>
  );
}
