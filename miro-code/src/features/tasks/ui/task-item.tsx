import { TaskResponse } from "@/shared/api/schema/generated";
import { Button } from "@/shared/ui/kit/button";
import { Badge } from "@/shared/ui/kit/badge";
import { Trash2, Edit3 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui/kit/card";

interface TaskItemProps {
  task: TaskResponse;
  onDelete?: (taskId: string) => void;
  onEdit?: (task: TaskResponse) => void;
  isDeleting?: boolean;
  className?: string;
}

const statusLabels: Record<TaskResponse["status"], string> = {
  AVAILABLE: "Доступные",
  IN_PROGRESS: "В работе",
  COMPLETED: "Завершенные",
  BLOCKED: "Заблокированные",
};

const priorityLabels: Record<TaskResponse["priority"], string> = {
  LOW: "Низкий",
  MEDIUM: "Средний",
  HIGH: "Высокий",
};

const statusColors: Record<string, string> = {
  AVAILABLE: "bg-blue-100 text-blue-800",
  IN_PROGRESS: "bg-yellow-100 text-yellow-800",
  COMPLETED: "bg-green-100 text-green-800",
  BLOCKED: "bg-red-100 text-red-800",
};

const priorityColors: Record<string, string> = {
  LOW: "bg-gray-100 text-gray-800",
  MEDIUM: "bg-orange-100 text-orange-800",
  HIGH: "bg-red-100 text-red-800",
};

export function TaskItem({ task, onDelete, onEdit, isDeleting, className }: TaskItemProps) {
  return (
    <Card className={`${className}`}>
      <CardHeader className="pb-3">
        <div className="flex justify-between items-start">
          <CardTitle className="text-lg">{task.title}</CardTitle>
          <div className="flex gap-2">
            {onEdit && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => onEdit(task)}
                className="h-8 w-8 p-0"
              >
                <Edit3 className="h-4 w-4" />
              </Button>
            )}
            {onDelete && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => onDelete(task.id)}
                disabled={isDeleting}
                className="h-8 w-8 p-0 text-red-600 hover:text-red-700"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            )}
          </div>
        </div>
      </CardHeader>
      <CardContent className="pt-0">
        <div className="space-y-3">
          {task.description && (
            <p className="text-sm text-gray-600 line-clamp-2">{task.description}</p>
          )}
          
          <div className="flex gap-2 flex-wrap">
            <Badge className={statusColors[task.status]}>
              {statusLabels[task.status]}
            </Badge>
            <Badge className={priorityColors[task.priority]}>
              {priorityLabels[task.priority]}
            </Badge>
            <Badge variant="outline">
              {task.department}
            </Badge>
          </div>

          <div className="flex justify-between items-center text-sm text-gray-500">
            <span>Создано: {new Date(task.createdAt).toLocaleDateString()}</span>
            {task.dueDate && (
              <span>Срок: {new Date(task.dueDate).toLocaleDateString()}</span>
            )}
          </div>

          {task.assigneeIds && task.assigneeIds.length > 0 && (
            <div className="text-sm text-gray-500">
              Исполнители: {task.assigneeIds.length}
            </div>
          )}

          {task.tags.length > 0 && (
            <div className="flex gap-1 flex-wrap">
              {task.tags.map((tag, index) => (
                <Badge key={index} variant="secondary" className="text-xs">
                  {tag}
                </Badge>
              ))}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
