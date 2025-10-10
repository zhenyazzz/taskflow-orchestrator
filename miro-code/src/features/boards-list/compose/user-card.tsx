import { UserResponse } from "@/shared/api/types";
import { Button } from "@/shared/ui/kit/button.tsx";
import { useDeleteUser } from "../model/user/use-delete-user.ts";

interface UserCardProps {
    user: UserResponse;
    className?: string;
}

export function UserCard({ user, className }: UserCardProps) {
    const deleteUser = useDeleteUser();

    return (
        <div className={`p-4 border rounded-lg shadow-sm ${className}`}>
            <h3 className="font-semibold">{user.username}</h3>
            <p className="text-sm text-gray-600">{user.email}</p>
            <p className="text-sm">Роли: {user.roles.join(", ")}</p>
            <p className="text-sm">Статус: {user.status}</p>
            <Button
                variant="destructive"
                disabled={deleteUser.isPending}
                onClick={(e) => {
                    e.stopPropagation();
                    deleteUser.deleteUser(user.id);
                }}
                className="mt-2 w-full"
            >
                Удалить
            </Button>
        </div>
    );
}