import { UserResponse } from "@/shared/api/types";
import { Button } from "@/shared/ui/kit/button.tsx";
import { useDeleteUser } from "../model/user/use-delete-user.ts";

interface UserItemProps {
    user: UserResponse;
    className?: string;
}

export function UserItem({ user, className }: UserItemProps) {
    const deleteUser = useDeleteUser();

    return (
        <div className={`flex justify-between items-center p-4 border rounded-lg ${className}`}>
            <div>
                <h3 className="font-semibold">{user.username}</h3>
                <p className="text-sm text-gray-600">{user.email}</p>
                <p className="text-sm">Роли: {user.roles.join(", ")}</p>
                <p className="text-sm">Статус: {user.status}</p>
            </div>
            <Button
                variant="destructive"
                disabled={deleteUser.isPending}
                onClick={(e) => {
                    e.stopPropagation();
                    deleteUser.deleteUser(user.id);
                }}
            >
                Удалить
            </Button>
        </div>
    );
}