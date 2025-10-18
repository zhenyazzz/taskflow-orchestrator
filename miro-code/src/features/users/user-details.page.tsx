import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { PathParams, ROUTES } from "@/shared/model/routes";
import { ArrowLeft, Loader2, AlertCircle, Edit3, Trash2 } from "lucide-react";
import { Button } from "@/shared/ui/kit/button";
import { Alert, AlertDescription, AlertTitle } from "@/shared/ui/kit/alert";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui/kit/card";
import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
    UserPageLayout,
    UserPageLayoutContent,
    UserPageLayoutHeader,
} from "@/features/users/ui/user-page-layout";
import { useUser } from "@/features/users/model/use-user";
import { useDeleteUser } from "@/features/users/model/use-delete-user"; // Предполагаемый хук для удаления
import { EditUserForm } from "@/features/users/ui/edit-user-form"; // Предполагаемый компонент формы редактирования

function UserDetailsPage() {
    const params = useParams<PathParams[typeof ROUTES.USER_DETAILS]>();
    const navigate = useNavigate();
    const userId = params.id!;

    const { data: user, isLoading, isError } = useUser(userId);
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

    const renderLoading = () => (
        <Alert variant="default" className="max-w-md mx-auto">
            <Loader2 className="w-4 h-4 animate-spin" />
            <AlertTitle>Загрузка пользователя</AlertTitle>
            <AlertDescription>Пожалуйста, подождите, пока загружаются данные пользователя.</AlertDescription>
        </Alert>
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
            <Alert variant="destructive" className="max-w-md mx-auto">
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
                        <div className="flex items-center">
                            <Button variant="ghost" size="icon" asChild>
                                <Link to="/users">
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
                <div className="max-w-2xl mx-auto py-6 space-y-6">
                    {isLoading ? (
                        renderLoading()
                    ) : isError || !user ? (
                        renderError()
                    ) : (
                        <>
                            {deleteUserMutation.isError && (
                                <Alert variant="destructive" className="max-w-md mx-auto">
                                    <AlertCircle className="w-4 h-4" />
                                    <AlertTitle>Ошибка</AlertTitle>
                                    <AlertDescription>Ошибка при удалении пользователя</AlertDescription>
                                </Alert>
                            )}
                            {isEditing ? (
                                <Card className="border-none shadow-sm">
                                    <CardHeader>
                                        <CardTitle className="text-lg font-medium">Редактирование пользователя</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        <EditUserForm
                                            user={user}
                                            onCancel={() => setIsEditing(false)}
                                            onSuccess={handleEditSuccess}
                                        />
                                    </CardContent>
                                </Card>
                            ) : (
                                <Card className="border-none shadow-sm transition-shadow hover:shadow-md">
                                    <CardHeader>
                                        <CardTitle className="text-lg font-medium">
                                            {user.firstName} {user.lastName}
                                        </CardTitle>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        <div className="flex items-center justify-between">
                                            <span className="text-sm text-gray-500">Имя пользователя</span>
                                            <span className="text-sm font-medium">{user.username}</span>
                                        </div>
                                        <div className="flex items-center justify-between">
                                            <span className="text-sm text-gray-500">Электронная почта</span>
                                            <span className="text-sm font-medium">{user.email}</span>
                                        </div>
                                        <div className="flex items-center justify-between">
                                            <span className="text-sm text-gray-500">Роль</span>
                                            <span className="text-sm font-medium capitalize">
                        {user.roles.join(", ").toLowerCase()}
                      </span>
                                        </div>
                                        <div className="flex items-center justify-between">
                                            <span className="text-sm text-gray-500">Дата создания</span>
                                            <span className="text-sm font-medium">
                        {new Date(user.createdAt).toLocaleDateString()}
                      </span>
                                        </div>
                                    </CardContent>
                                </Card>
                            )}
                        </>
                    )}
                </div>
            </UserPageLayoutContent>
        </UserPageLayout>
    );
}

export const Component = UserDetailsPage;