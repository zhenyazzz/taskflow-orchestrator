import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { PathParams, ROUTES } from "@/shared/model/routes";
import { ArrowLeft, Loader2, AlertCircle, Edit3, Trash2, CheckCircle } from "lucide-react";
import { Button } from "@/shared/ui/kit/button";
import { Alert, AlertDescription, AlertTitle } from "@/shared/ui/kit/alert";
import { TasksSidebar } from "./ui/tasks-sidebar";
import {
    TaskPageLayout,
    TaskPageLayoutContent,
    TaskPageLayoutHeader,
} from "./ui/task-page-layout";
import { useTask } from "./model/use-task";
import { useDeleteTask } from "./model/use-delete-task";
import { EditTaskForm } from "./ui/edit-task-form";
import { InfoItem } from "@/shared/ui/kit/info-item";
import { Badge } from "@/shared/ui/kit/badge";
import { useUpdateTaskStatus } from "./model/use-update-task-status";
import {BoardsSidebar} from "@/features/boards-list/ui/task/boards-sidebar.tsx";

function TaskDetailsPage() {
  const params = useParams<PathParams[typeof ROUTES.TASK_DETAILS]>();
  const navigate = useNavigate();
  const taskId = params.id!;

  const { data: task, isLoading, isError } = useTask(taskId);
  const deleteTaskMutation = useDeleteTask(
    () => navigate("/tasks"),
  );
  const [isEditing, setIsEditing] = useState(false);
  const [isUpdatingStatus, setIsUpdatingStatus] = useState(false);

  const statusMutation = useUpdateTaskStatus(taskId, () => {
    setIsUpdatingStatus(false);
  });

  const renderLoading = () => (
    <div className="flex justify-center items-center py-12">
      <Alert variant="default" className="max-w-md">
        <Loader2 className="w-4 h-4 animate-spin" />
        <AlertTitle>Загрузка задачи</AlertTitle>
        <AlertDescription>Пожалуйста, подождите...</AlertDescription>
      </Alert>
    </div>
  );

  const renderError = () => {
    let title = "Произошла ошибка";
    let description = "Не удалось загрузить данные задачи.";

    if (isError) {
      title = "Ошибка загрузки задачи";
      description = "Произошла ошибка при загрузке данных задачи.";
    } else if (!task) {
      title = "Задача не найдена";
      description = "Задача с указанным ID не найдена.";
    }

    return (
      <div className="flex justify-center items-center py-12">
        <Alert variant="destructive" className="max-w-md">
          <AlertCircle className="w-4 h-4" />
          <AlertTitle>{title}</AlertTitle>
          <AlertDescription>{description}</AlertDescription>
        </Alert>
      </div>
    );
  };

  const statusLabels: Record<string, string> = {
    AVAILABLE: "Доступные",
    IN_PROGRESS: "В работе",
    COMPLETED: "Завершенные",
    BLOCKED: "Заблокированные",
  };

  const priorityLabels: Record<string, string> = {
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

  return (
    <TaskPageLayout
      sidebar={<BoardsSidebar />}
      header={
        <TaskPageLayoutHeader
          title={
            <div className="flex items-center">
              <Button variant="ghost" size="icon" asChild>
                <Link to="/tasks">
                  <ArrowLeft className="w-4 h-4" />
                </Link>
              </Button>
              <span className="ml-2 text-xl font-semibold">
                {task ? task.title : "Задача"}
              </span>
            </div>
          }
          description="Детальная информация о задаче"
          actions={
            <div className="flex items-center gap-2">
              {!isEditing && task && (
                <>
                  <Button
                    onClick={() => setIsEditing(true)}
                    variant="outline"
                    className="transition-colors hover:bg-emerald-500/10 hover:text-emerald-600"
                  >
                    <Edit3 className="w-4 h-4 mr-2" />
                    Редактировать
                  </Button>
                  {task.status !== "COMPLETED" && (
                    <Button
                      onClick={() => {
                        setIsUpdatingStatus(true);
                        statusMutation.updateStatus({ status: "COMPLETED" });
                      }}
                      variant="outline"
                      className="transition-colors hover:bg-green-500/10 hover:text-green-600"
                      disabled={statusMutation.isPending}
                    >
                      <CheckCircle className="w-4 h-4 mr-2" />
                      {statusMutation.isPending ? "Завершение..." : "Завершить"}
                    </Button>
                  )}
                  <Button
                    onClick={() => deleteTaskMutation.deleteTask(taskId)}
                    variant="outline"
                    className="transition-colors hover:bg-red-500/10 hover:text-red-600"
                    disabled={deleteTaskMutation.isPending}
                  >
                    <Trash2 className="w-4 h-4 mr-2" />
                    {deleteTaskMutation.isPending ? "Удаление..." : "Удалить"}
                  </Button>
                </>
              )}
            </div>
          }
        />
      }
    >
      <TaskPageLayoutContent>
        <div className="max-w-2xl py-6">
          {isLoading ? (
            renderLoading()
          ) : isError || !task ? (
            renderError()
          ) : (
            <>
              {deleteTaskMutation.errorMessage && (
                <Alert variant="destructive" className="mb-6">
                  <AlertCircle className="w-4 h-4" />
                  <AlertTitle>Ошибка</AlertTitle>
                  <AlertDescription>
                    {deleteTaskMutation.errorMessage || "Ошибка при удалении задачи"}
                  </AlertDescription>
                </Alert>
              )}

              {isEditing ? (
                <EditTaskForm
                  task={task}
                  onCancel={() => setIsEditing(false)}
                />
              ) : (
                <div className="space-y-6">
                  <div className="space-y-4">
                    <InfoItem
                      label="Название"
                      value={task.title}
                    />
                    {task.description && (
                      <InfoItem
                        label="Описание"
                        value={task.description}
                      />
                    )}
                    <InfoItem
                      label="Статус"
                      value={
                        <Badge className={statusColors[task.status]}>
                          {statusLabels[task.status]}
                        </Badge>
                      }
                    />
                    <InfoItem
                      label="Приоритет"
                      value={
                        <Badge className={priorityColors[task.priority]}>
                          {priorityLabels[task.priority]}
                        </Badge>
                      }
                    />
                    <InfoItem
                      label="Отдел"
                      value={task.department}
                    />
                    <InfoItem
                      label="Дата создания"
                      value={new Date(task.createdAt).toLocaleDateString(
                        "ru-RU",
                        { year: "numeric", month: "long", day: "numeric" }
                      )}
                    />
                    {task.dueDate && (
                      <InfoItem
                        label="Срок выполнения"
                        value={new Date(task.dueDate).toLocaleDateString(
                          "ru-RU",
                          { year: "numeric", month: "long", day: "numeric" }
                        )}
                      />
                    )}
                    {task.tags.length > 0 && (
                      <InfoItem
                        label="Теги"
                        value={
                          <div className="flex gap-1 flex-wrap">
                            {task.tags.map((tag, index) => (
                              <Badge key={index} variant="secondary">
                                {tag}
                              </Badge>
                            ))}
                          </div>
                        }
                      />
                    )}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </TaskPageLayoutContent>
    </TaskPageLayout>
  );
}

export const Component = TaskDetailsPage;
