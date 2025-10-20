import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { PathParams, ROUTES } from "@/shared/model/routes";
import { ArrowLeft, Loader2, AlertCircle, Edit3, Trash2 } from "lucide-react";
import { Button } from "@/shared/ui/kit/button";
import { Alert, AlertDescription, AlertTitle } from "@/shared/ui/kit/alert";
import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
    UserPageLayout,
    UserPageLayoutContent,
    UserPageLayoutHeader,
} from "@/features/users/ui/user-page-layout";
import { useUser } from "@/features/users/model/use-user";
import { useDeleteUser } from "@/features/users/model/use-delete-user";
import { EditUserForm } from "@/features/users/ui/edit-user-form";
import { InfoItem } from "@/shared/ui/kit/info-item";
import { useUpdateUser } from "@/features/users/model/use-update-user";

function UserDetailsPage() {
  const params = useParams<PathParams[typeof ROUTES.USER_DETAILS]>();
  const navigate = useNavigate();
  const userId = params.id!;

  const { data: user, isLoading, isError } = useUser(userId);
  const deleteUserMutation = useDeleteUser();
  const updateUserMutation = useUpdateUser(userId);
  const [isEditing, setIsEditing] = useState(false);

  const handleEditSuccess = () => {
    setIsEditing(false);
  };

  const handleDeleteUser = () => {
    if (window.confirm("Вы уверены, что хотите удалить этого пользователя?")) {
      deleteUserMutation.mutate({ params: { path: { id: userId } } }, {
        onSuccess: () => {
          navigate("/users");
        },
      });
    }
  };

  const renderLoading = () => (
    <div className="flex justify-center items-center py-12">
      <Alert variant="default" className="max-w-md">
        <Loader2 className="w-4 h-4 animate-spin" />
        <AlertTitle>Загрузка пользователя</AlertTitle>
        <AlertDescription>Пожалуйста, подождите...</AlertDescription>
      </Alert>
    </div>
  );

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
      <div className="flex justify-center items-center py-12">
        <Alert variant="destructive" className="max-w-md">
          <AlertCircle className="w-4 h-4" />
          <AlertTitle>{title}</AlertTitle>
          <AlertDescription>{description}</AlertDescription>
        </Alert>
      </div>
    );
  };

  return (
    <UserPageLayout
      sidebar={<BoardsSidebar />}
      header={
        <UserPageLayoutHeader
          title={
            <div className="flex items-center">
              <Button variant="ghost" size="icon" asChild>
                <Link to="/boards/user">
                  <ArrowLeft className="w-4 h-4" />
                </Link>
              </Button>
              <span className="ml-2 text-xl font-semibold">
                {user ? `${user.firstName} ${user.lastName}` : "Пользователь"}
              </span>
            </div>
          }
          description="Детальная информация о пользователе"
          actions={
            <div className="flex items-center gap-2">
              {!isEditing && user && (
                <>
                  <Button
                    onClick={() => setIsEditing(true)}
                    variant="outline"
                    className="transition-colors hover:bg-emerald-500/10 hover:text-emerald-600"
                  >
                    <Edit3 className="w-4 h-4 mr-2" />
                    Редактировать
                  </Button>
                  <Button
                    onClick={handleDeleteUser}
                    variant="outline"
                    className="transition-colors hover:bg-red-500/10 hover:text-red-600"
                    disabled={deleteUserMutation.isPending}
                  >
                    <Trash2 className="w-4 h-4 mr-2" />
                    {deleteUserMutation.isPending ? "Удаление..." : "Удалить"}
                  </Button>
                </>
              )}
            </div>
          }
        />
      }
    >
      <UserPageLayoutContent>
        <div className="max-w-2xl py-6">
          {isLoading ? (
            renderLoading()
          ) : isError || !user ? (
            renderError()
          ) : (
            <>
              {deleteUserMutation.isError && (
                <Alert variant="destructive" className="mb-6">
                  <AlertCircle className="w-4 h-4" />
                  <AlertTitle>Ошибка</AlertTitle>
                  <AlertDescription>
                    Ошибка при удалении пользователя
                  </AlertDescription>
                </Alert>
              )}

              {isEditing ? (
                <EditUserForm
                  user={user}
                  onCancel={() => setIsEditing(false)}
                  onSuccess={handleEditSuccess}
                  isPending={updateUserMutation.isPending}
                  error={updateUserMutation.error as Error | null}
                  updateUser={(body) => updateUserMutation.mutate({ params: { path: { id: userId } }, body })}
                />
              ) : (
                <div className="space-y-6">
                  <div className="space-y-4">
                    <InfoItem
                      label="Имя пользователя"
                      value={user.username}
                    />
                    <InfoItem
                      label="Электронная почта"
                      value={user.email}
                    />
                    <InfoItem
                      label="Роли"
                      value={user.roles.join(", ")}
                    />
                    <InfoItem
                      label="Дата создания"
                      value={new Date(user.createdAt).toLocaleDateString(
                        "ru-RU",
                        { year: "numeric", month: "long", day: "numeric" }
                      )}
                    />
                    {user.updatedAt && (
                      <InfoItem
                        label="Дата обновления"
                        value={new Date(user.updatedAt).toLocaleDateString(
                          "ru-RU",
                          { year: "numeric", month: "long", day: "numeric" }
                        )}
                      />
                    )}
                  </div>
                </div>
              )}
            </>
          )}  
        </div>
      </UserPageLayoutContent>
    </UserPageLayout>
  );
}

export const Component = UserDetailsPage;