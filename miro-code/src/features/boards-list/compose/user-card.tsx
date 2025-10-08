import { UserResponse } from "@/shared/api/types";
import { Button } from "@/shared/ui/kit/button.tsx";
import { useDeleteUser } from "../model/use-delete-user.ts";

interface UserCardProps {
    user: UserResponse;
}

export function UserCard({ user }: UserCardProps) {
    const deleteUser = useDeleteUser();

    return (
        <div className="p-4 border rounded-lg shadow-sm">
            <h3 className="font-semibold">{user.username}</h3>
            <p className="text-sm text-gray-600">{user.email}</p>
            <p className="text-sm">Роли: {user.roles.join(", ")}</p>
            <p className="text-sm">Статус: {user.status}</p>
            <Button
                variant="destructive"
                disabled={deleteUser.isPending}
                onClick={() => deleteUser.deleteUser(user.id)}
                className="mt-2 w-full"
            >
                Удалить
            </Button>
        </div>
    );
}