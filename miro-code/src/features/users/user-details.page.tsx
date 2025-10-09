// features/users/pages/user-details-page.tsx
import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { PathParams, ROUTES } from "@/shared/model/routes";
import { 
  ArrowLeft, 
  Loader2, 
  AlertCircle, 
  Edit3,
  MoreVertical 
} from "lucide-react";
import { Button } from "@/shared/ui/kit/button";
import { Alert, AlertDescription } from "@/shared/ui/kit/alert";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/shared/ui/kit/dropdown-menu";
import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
  PageLayout,
  PageLayoutContent,
  PageLayoutHeader,
} from "@/shared/ui/layouts/page-layout";
import { useUser, useDeleteUser } from "../model/use-user";
import { UserProfileCard } from "../ui/user/user-profile-card";
import { EditUserForm } from "../ui/user/edit-user-form";

function UserDetailsPage() {
  const params = useParams<PathParams[typeof ROUTES.USER_DETAILS]>();
  const navigate = useNavigate();
  const userId = params.id!;
  
  const { data: user, isLoading, error } = useUser(userId);
  const deleteUserMutation = useDeleteUser();
  
  const [isEditing, setIsEditing] = useState(false);

  const handleEditSuccess = () => {
    setIsEditing(false);
  };

  const handleDeleteUser = () => {
    if (window.confirm("Вы уверены, что хотите удалить этого пользователя?")) {
      deleteUserMutation.mutate(userId, {
        onSuccess: () => {
          navigate("/users");
        },
      });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Alert variant="destructive" className="max-w-md">
          <AlertCircle className="w-4 h-4" />
          <AlertDescription>
            Ошибка при загрузке пользователя
          </AlertDescription>
        </Alert>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Alert variant="destructive" className="max-w-md">
          <AlertCircle className="w-4 h-4" />
          <AlertDescription>
            Пользователь не найден
          </AlertDescription>
        </Alert>
      </div>
    );
  }

  return (
    <PageLayout
      sidebar={<BoardsSidebar />}
      header={
        <PageLayoutHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button variant="ghost" size="icon" asChild>
                <Link to="/users">
                  <ArrowLeft className="w-4 h-4" />
                </Link>
              </Button>
              <div>
                <h1 className="text-2xl font-bold">
                  {user.firstName} {user.lastName}
                </h1>
                <p className="text-muted-foreground">
                  Детальная информация о пользователе
                </p>
              </div>
            </div>

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
                  
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button variant="ghost" size="icon">
                        <MoreVertical className="w-4 h-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem
                        className="text-red-600"
                        onClick={handleDeleteUser}
                        disabled={deleteUserMutation.isPending}
                      >
                        Удалить пользователя
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </>
              )}
            </div>
          </div>
        </PageLayoutHeader>
      }
    >
      <PageLayoutContent>
        <div className="max-w-4xl mx-auto space-y-6">
          {/* Уведомления */}
          {deleteUserMutation.isError && (
            <Alert variant="destructive">
              <AlertCircle className="w-4 h-4" />
              <AlertDescription>
                Ошибка при удалении пользователя
              </AlertDescription>
            </Alert>
          )}

          {/* Основной контент */}
          {isEditing ? (
            <EditUserForm
              user={user}
              onCancel={() => setIsEditing(false)}
              onSuccess={handleEditSuccess}
            />
          ) : (
            <UserProfileCard
              user={user}
              onEdit={() => setIsEditing(true)}
            />
          )}
        </div>
      </PageLayoutContent>
    </PageLayout>
  );
}

export const Component = UserDetailsPage;