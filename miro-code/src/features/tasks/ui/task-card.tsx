import { TaskResponse } from "@/shared/api/schema/generated";
import { Button } from "@/shared/ui/kit/button";
import { Badge } from "@/shared/ui/kit/badge";
import { Trash2, Edit3 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui/kit/card";

interface TaskCardProps {
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

export function TaskCard({ task, onDelete, onEdit, isDeleting, className }: TaskCardProps) {
  return (
    <Card className={`h-full ${className}`}>
      <CardHeader className="pb-3">
        <div className="flex justify-between items-start">
          <CardTitle className="text-lg line-clamp-2">{task.title}</CardTitle>
          <div className="flex gap-1">
            {onEdit && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => onEdit(task)}
                className="h-7 w-7 p-0"
              >
                <Edit3 className="h-3 w-3" />
              </Button>
            )}
            {onDelete && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => onDelete(task.id)}
                disabled={isDeleting}
                className="h-7 w-7 p-0 text-red-600 hover:text-red-700"
              >
                <Trash2 className="h-3 w-3" />
              </Button>
            )}
          </div>
        </div>
      </CardHeader>
      <CardContent className="pt-0">
        <div className="space-y-3">
          {task.description && (
            <p className="text-sm text-gray-600 line-clamp-3">{task.description}</p>
          )}
          
          <div className="flex gap-1 flex-wrap">
            <Badge className={`text-xs ${statusColors[task.status]}`}>
              {statusLabels[task.status]}
            </Badge>
            <Badge className={`text-xs ${priorityColors[task.priority]}`}>
              {priorityLabels[task.priority]}
            </Badge>
          </div>

          <div className="text-xs text-gray-500 space-y-1">
            <div>Отдел: {task.department}</div>
            <div>Создано: {new Date(task.createdAt).toLocaleDateString()}</div>
            {task.dueDate && (
              <div>Срок: {new Date(task.dueDate).toLocaleDateString()}</div>
            )}
          </div>

          {task.tags.length > 0 && (
            <div className="flex gap-1 flex-wrap">
              {task.tags.slice(0, 3).map((tag, index) => (
                <Badge key={index} variant="secondary" className="text-xs">
                  {tag}
                </Badge>
              ))}
              {task.tags.length > 3 && (
                <Badge variant="secondary" className="text-xs">
                  +{task.tags.length - 3}
                </Badge>
              )}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
