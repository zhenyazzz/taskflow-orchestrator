import { useState } from "react";
import { useParams, Link } from "react-router-dom";
import { PathParams, ROUTES } from "@/shared/model/routes";
import {
  ArrowLeft, 
  Edit3,
  Trash2
} from "lucide-react";
import { Button } from "@/shared/ui/kit/button";

import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
  UserPageLayout,
  UserPageLayoutContent,
  UserPageLayoutHeader,
} from "@/features/users/ui/user-page-layout";
import { useTask } from "./model/use-task";

function BoardPage() {
  const params = useParams<PathParams[typeof ROUTES.BOARD]>();
  const taskId = params.boardId!;
  
  const { data: task, isLoading, isError } = useTask({ taskId });

  const board = {
    name: task?.title || `Task ${task?.title}`,
    description: task?.description || "This is a description for the task.",
  };
  
  const [isEditing, setIsEditing] = useState(false);

  const handleDeleteBoard = () => {
    if (window.confirm("Вы уверены, что хотите удалить эту задачу?")) {
      // deleteBoardMutation.mutate(boardId, {
      //   onSuccess: () => {
      //     navigate("/boards");
      //   },
      // });
    }
  };


  return (
    <UserPageLayout
      sidebar={<BoardsSidebar />}
      header={
        <UserPageLayoutHeader
        title={
          <>
            <Button variant="ghost" size="icon" asChild>
              <Link to="/boards">
                <ArrowLeft className="w-4 h-4" />
              </Link>
            </Button>
            <span className="ml-2">{board.name}</span>
          </>
        }
          description="Детальная информация о задаче"
          actions={
            <div className="flex items-center gap-2">
              {!isEditing && (
                <>
                  <Button
                    onClick={() => setIsEditing(true)}
                    variant="outline"
                  >
                    <Edit3 className="w-4 h-4 mr-2" />
                    Редактировать
                  </Button>
                  <Button
                    onClick={handleDeleteBoard}
                    variant="outline"
                  >
                    <Trash2 className="w-4 h-4 mr-2" />
                    Удалить задачу
                  </Button>
                </>
              )}
            </div>
          }
        />
      }
    >
      <UserPageLayoutContent>
        <div className="max-w-4xl mx-auto space-y-6">
          {isLoading && <div>Загрузка задачи...</div>}
          {isError && <div>Ошибка загрузки задачи.</div>}
          {!isLoading && !isError && (
            <div>
              <h3>{board.name}</h3>
              <p>{board.description}</p>
            </div>
          )}

          {isEditing ? (
            // <EditBoardForm
            //   board={board}
            //   onCancel={() => setIsEditing(false)}
            //   onSuccess={handleEditSuccess}
            // />
            <div>Редактирование задачи</div>
          ) : (
            <div>Детали задачи</div>
          )}
        </div>
      </UserPageLayoutContent>
    </UserPageLayout>
  );
}

export const Component = BoardPage;
