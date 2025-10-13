// features/users/pages/user-details-page.tsx
import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { PathParams, ROUTES } from "@/shared/model/routes";
import { 
  ArrowLeft, 
  Loader2, 
  AlertCircle, 
  Edit3,
  MoreVertical,
  Trash2
} from "lucide-react";
import { Button } from "@/shared/ui/kit/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/shared/ui/kit/dropdown-menu";
import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
  UserPageLayout,
  UserPageLayoutContent,
  UserPageLayoutHeader,
} from "@/features/users/ui/user-page-layout";

function UserDetailsPage() {
  const params = useParams<PathParams[typeof ROUTES.USER_DETAILS]>();
  const navigate = useNavigate();
  const userId = params.id!;
  
  const user = {
    firstName: "John",
    lastName: "Doe",
  };
  
  const [isEditing, setIsEditing] = useState(false);

  const handleEditSuccess = () => {
    setIsEditing(false);
  };

  const handleDeleteUser = () => {
    if (window.confirm("Вы уверены, что хотите удалить этого пользователя?")) {
      // deleteUserMutation.mutate(userId, {
      //   onSuccess: () => {
      //     navigate("/users");
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
              <Link to="/boards/user">
                <ArrowLeft className="w-4 h-4" />
              </Link>
            </Button>
            <span className="ml-2">{user.firstName} {user.lastName}</span>
          </>
        }
          description="Детальная информация о пользователе"
          actions={
            <div className="flex items-center gap-2">
              {!isEditing && (
                <>
                  <Button
                    onClick={() => setIsEditing(true)}
                    variant="outline"
                    className="hover:bg-emerald-500/10"
                  >
                    <Edit3 className="w-4 h-4 mr-2" />
                    Редактировать
                  </Button>
                  <Button
                    onClick={() => setIsEditing(true)}
                    variant="outline"
                    className="hover:bg-destructive/10"
                  >
                    <Trash2 className="w-4 h-4 mr-2" />
                    Удалить пользователя
                  </Button>
                </>
              )}
            </div>
          }
        >
          <span>
            <Button variant="ghost" size="icon" asChild>
              <Link to="/users">
                <ArrowLeft className="w-4 h-4" />
              </Link>
            </Button>
            <span className="ml-2">
              {user.firstName} {user.lastName}
            </span>
          </span>
        </UserPageLayoutHeader>
      }
    >
      <UserPageLayoutContent>
        <div className="max-w-4xl mx-auto space-y-6">
          {/* Уведомления */}
          {/* {deleteUserMutation.isError && (
            <Alert variant="destructive">
              <AlertCircle className="w-4 h-4" />
              <AlertDescription>
                Ошибка при удалении пользователя
              </AlertDescription>
            </Alert>
          )} */}

          {/* Основной контент */}
          {isEditing ? (
            // <EditUserForm
            //   user={user}
            //   onCancel={() => setIsEditing(false)}
            //   onSuccess={handleEditSuccess}
            // />
            <div>Редактирование пользователя</div>
          ) : (
            <div>Профиль пользователя</div>
          )}
        </div>
      </UserPageLayoutContent>
    </UserPageLayout>
  );
}

export const Component = UserDetailsPage;