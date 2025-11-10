import { useState } from "react";
import { components } from "@/shared/api/schema/generated";
import { Button } from "@/shared/ui/kit/button";
import { Badge } from "@/shared/ui/kit/badge";
import { Trash2, Edit3, Star } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui/kit/card";
import { useSubscribeTask } from "../model/use-subscribe-task";
import { useSession } from "@/shared/model/session";

interface TaskItemProps {
  task: components["schemas"]["TaskResponse"];
  onDelete?: (taskId: string) => void;
  onEdit?: (task: components["schemas"]["TaskResponse"]) => void;
  isDeleting?: boolean;
  className?: string;
}

const statusLabels: Record<NonNullable<components["schemas"]["TaskResponse"]["status"]>, string> = {
  AVAILABLE: "Доступные",
  IN_PROGRESS: "В работе",
  COMPLETED: "Завершенные",
  BLOCKED: "Заблокированные",
};

const priorityLabels: Record<NonNullable<components["schemas"]["TaskResponse"]["priority"]>, string> = {
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
  const { session } = useSession();
  const isAdmin = session?.roles?.includes("ROLE_ADMIN") ?? false;
  const [isSubscribed, setIsSubscribed] = useState(false);
  const { subscribe, unsubscribe, isPending: isSubscribing } = useSubscribeTask(
    undefined,
    () => {
      // Откатываем состояние при ошибке
      setIsSubscribed((prev) => !prev);
    }
  );

  const handleSubscribeToggle = () => {
    if (!task.id) return;
    
    const newState = !isSubscribed;
    setIsSubscribed(newState);
    
    if (newState) {
      subscribe(task.id);
    } else {
      unsubscribe(task.id);
    }
  };

  return (
    <Card className={`${className}`}>
      <CardHeader className="pb-3">
        <div className="flex justify-between items-start gap-2">
          <CardTitle className="text-lg flex-1 min-w-0">{task.title}</CardTitle>
          <div className="flex gap-1 flex-shrink-0">
            {task.id && (
              <Button
                variant={isSubscribed ? "default" : "outline"}
                size="sm"
                onClick={handleSubscribeToggle}
                disabled={isSubscribing}
                className="h-8 px-3 text-xs"
                title={isSubscribed ? "Убрать из избранного" : "Добавить в избранное"}
              >
                <Star className={`h-3 w-3 mr-1.5 ${isSubscribed ? "fill-yellow-400 text-yellow-400" : ""}`} />
                {isSubscribed ? "В избранном" : "В избранное"}
              </Button>
            )}
            {onEdit && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => onEdit(task)}
                className="h-8 w-8 p-0"
                title="Редактировать"
              >
                <Edit3 className="h-4 w-4" />
              </Button>
            )}
            {onDelete && task.id && isAdmin && (
              <Button
                variant="destructive"
                size="sm"
                onClick={() => onDelete(task.id!)}
                disabled={isDeleting}
                className="h-8 w-8 p-0"
                title="Удалить задачу"
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
            {task.status && (
              <Badge className={statusColors[task.status]}>
                {statusLabels[task.status]}
              </Badge>
            )}
            {task.priority && (
              <Badge className={priorityColors[task.priority]}>
                {priorityLabels[task.priority]}
              </Badge>
            )}
            {task.department && (
              <Badge variant="outline">
                {task.department}
              </Badge>
            )}
          </div>

          <div className="flex justify-between items-center text-sm text-gray-500">
            {task.createdAt && (
              <span>Создано: {new Date(task.createdAt).toLocaleDateString()}</span>
            )}
            {task.dueDate && (
              <span>Срок: {new Date(task.dueDate).toLocaleDateString()}</span>
            )}
          </div>

          {task.assigneeIds && task.assigneeIds.length > 0 && (
            <div className="text-sm text-gray-500">
              Исполнители: {task.assigneeIds.length}
            </div>
          )}

          {task.tags && task.tags.length > 0 && (
            <div className="flex gap-1 flex-wrap">
              {task.tags.map((tag: string, index: number) => (
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
