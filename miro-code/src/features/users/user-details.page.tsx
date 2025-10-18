// features/users/pages/user-details-page.tsx
import { useState } from "react";
import { useParams, Link } from "react-router-dom";
import { PathParams, ROUTES } from "@/shared/model/routes";
import { 
  ArrowLeft, 
  Loader2, 
  AlertCircle, 
  Edit3,
  Trash2
} from "lucide-react";
import { Button } from "@/shared/ui/kit/button";
// import {
//   DropdownMenu,
//   DropdownMenuContent,
//   DropdownMenuItem,
//   DropdownMenuTrigger,
// } from "@/shared/ui/kit/dropdown-menu";
import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
  UserPageLayout,
  UserPageLayoutContent,
  UserPageLayoutHeader,
} from "@/features/users/ui/user-page-layout";
import { useUser } from "@/features/users/model/use-user";
import { Alert, AlertDescription, AlertTitle } from "@/shared/ui/kit/alert";

function UserDetailsPage() {
  const params = useParams<PathParams[typeof ROUTES.USER_DETAILS]>();
  // const navigate = useNavigate(); // Не используется
  const userId = params.id!;
  
  const { data: user, isLoading, isError } = useUser(userId);

  const [isEditing, setIsEditing] = useState(false);

  // const handleEditSuccess = () => { // Не используется
  //   setIsEditing(false);
  // };

  // const handleDeleteUser = () => { // Не используется
  //   if (window.confirm("Вы уверены, что хотите удалить этого пользователя?")) {
  //     // deleteUserMutation.mutate(userId, {
  //     //   onSuccess: () => {
  //     //     navigate("/users");
  //     //   },
  //     // });
  //   }
  // };

  const renderLoading = () => {
    return (
      <Alert variant="default">
        <Loader2 className="w-4 h-4" />
        <AlertTitle>Загрузка пользователя</AlertTitle>
        <AlertDescription>Пожалуйста, подождите, пока загружаются данные пользователя.</AlertDescription>
      </Alert>
    );
  };

  const renderError = () => {
    let title = "Произошла ошибка";
    let description = "Не удалось загрузить данные пользователя.";

    if (isError) {
      title = "Ошибка загрузки пользователя";
      description = "Произошла ошибка при загрузке данных пользователя.";
    } else if (!user) {
      title = "Пользователь не найден";
      description = "Пользователь с указанным ID не найден.";
    }

    return (
      <Alert variant="destructive">
        <AlertCircle className="w-4 h-4" />
        <AlertTitle>{title}</AlertTitle>
        <AlertDescription>{description}</AlertDescription>
      </Alert>
    );
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
            <span className="ml-2">
              {user?.firstName} {user?.lastName}
            </span>
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
          {/* Удален повторяющийся span с Link */}
        </UserPageLayoutHeader>
      }
    >
      <UserPageLayoutContent>
        <div className="max-w-4xl mx-auto space-y-6">
          {isLoading && renderLoading() || (isError || !user) && renderError() || (
            <>
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
                <div>Профиль пользователя: {user?.username} ({user?.email})</div>
              )}
            </>
          )}
        </div>
      </UserPageLayoutContent>
    </UserPageLayout>
  );
}

export const Component = UserDetailsPage;