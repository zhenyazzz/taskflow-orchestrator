import { useState } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, Edit3, Loader2 } from "lucide-react";
import { Button } from "@/shared/ui/kit/button";
import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
    UserPageLayout,
    UserPageLayoutContent,
    UserPageLayoutHeader,
} from "@/features/users/ui/user-page-layout";
import { EditProfileForm } from "@/features/users/edit-profile-form";
import { useUpdateUser } from "@/features/boards-list/model/use-update-user";
import { useUserProfile } from "./model/use-user-profile";

function ProfileEditPage() {
    const { data: user, isLoading: isUserLoading } = useUserProfile();
    const [isEditing, setIsEditing] = useState(false); // Форма редактирования не открыта по умолчанию
    const { mutate: updateUser, isPending: isUpdatePending, error: updateError } = useUpdateUser();

    const handleEditSuccess = () => {
        setIsEditing(false);
    };

    const handleCancel = () => {
        setIsEditing(false);
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
                            <span className="ml-2">
                {isUserLoading ? "Загрузка..." : `${user?.firstName || ""} ${user?.lastName || ""}`}
              </span>
                        </>
                    }
                    description="Редактирование вашего профиля"
                    actions={
                        !isEditing && (
                            <Button onClick={() => setIsEditing(true)} variant="outline">
                                <Edit3 className="w-4 h-4 mr-2" />
                                Редактировать
                            </Button>
                        )
                    }
                />
            }
        >
            <UserPageLayoutContent>
                <div className="max-w-4xl mx-auto space-y-6">
                    {isUserLoading ? (
                        <div className="flex justify-center">
                            <Loader2 className="w-6 h-6 animate-spin" />
                        </div>
                    ) : !user ? (
                        <div className="text-red-500">Ошибка загрузки данных пользователя</div>
                    ) : isEditing ? (
                        <EditProfileForm
                            user={user}
                            onCancel={handleCancel}
                            onSuccess={handleEditSuccess}
                            isPending={isUpdatePending}
                            error={updateError}
                            updateUser={updateUser}
                        />
                    ) : (
                        <div className="space-y-4">
                            <h3 className="text-lg font-semibold">Профиль пользователя</h3>
                            <p><strong>ID:</strong> {user.id}</p>
                            <p><strong>Имя пользователя:</strong> {user.username}</p>
                            <p><strong>Email:</strong> {user.email}</p>
                            <p><strong>Имя:</strong> {user.firstName || "Не указано"}</p>
                            <p><strong>Фамилия:</strong> {user.lastName || "Не указано"}</p>
                            <p><strong>Роли:</strong> {user.roles.join(", ") || "Нет ролей"}</p>
                        </div>
                    )}
                </div>
            </UserPageLayoutContent>
        </UserPageLayout>
    );
}

export const Component = ProfileEditPage;