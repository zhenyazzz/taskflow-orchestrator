import { useState } from "react";
import { Button } from "@/shared/ui/kit/button";
import { PlusIcon } from "lucide-react";
import { useUsersList } from "./model/use-users-list";
import { useUsersFilters } from "./model/use-users-filters";
import { useDebouncedValue } from "@/shared/lib/react";
import { useCreateUser } from "./model/use-create-user";
import {
    UsersListLayout,
    UsersListLayoutContent,
    UsersListLayoutFilters,
    UsersListLayoutHeader,
} from "./ui/user/users-list-layout";
import { ViewMode, ViewModeToggle } from "./ui/view-mode-toggle";
import { UsersSortSelect } from "./ui/user/users-sort-select";
import { UsersSearchInput } from "./ui/user/users-search-input";
import { UsersStatusFilterSelect } from "./ui/user/users-status-filter-select";
import { UsersRoleFilterSelect } from "./ui/user/users-role-filter-select";
import { UserItem } from "./compose/user-item";
import { UserCard } from "./compose/user-card";
import { BoardsSidebar} from "@/features/boards-list/ui/task/boards-sidebar";
import { CreateUserRequest } from "@/shared/api/types";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "@/shared/ui/kit/dialog";
import { CreateUserForm } from "./ui/user/create-user-form";
import { useEffect } from "react";
import { Link } from "react-router-dom"; // Импортируем Link для навигации

function UsersListPage() {
    const usersFilters = useUsersFilters();
    const usersQuery = useUsersList({
        sort: usersFilters.sort,
        search: useDebouncedValue(usersFilters.search, 300),
        status: usersFilters.status,
        role: usersFilters.role,
    });

    const createUserMutation = useCreateUser();
    const [viewMode, setViewMode] = useState<ViewMode>("list");
    const [isOpen, setIsOpen] = useState(false);
    const [formData, setFormData] = useState<CreateUserRequest>({
        username: "",
        password: "",
        email: "",
        firstName: "",
        lastName: "",
        roles: ["USER"],
    });

    useEffect(() => {
        if (createUserMutation.isSuccess) {
            setIsOpen(false);
            createUserMutation.reset();
            // Сброс формы после успешного создания
            setFormData({
                username: "",
                password: "",
                email: "",
                firstName: "",
                lastName: "",
                roles: ["USER"],
            });
        }
    }, [createUserMutation.isSuccess]);

    return (
        <>
            <UsersListLayout
                sidebar={<BoardsSidebar />}
                header={
                    <UsersListLayoutHeader
                        title="Пользователи"
                        description="Здесь вы можете просматривать и управлять пользователями системы"
                        actions={
                            <>
                                <Dialog open={isOpen} onOpenChange={setIsOpen}>
                                    <DialogTrigger asChild>
                                        <Button>
                                            <PlusIcon />
                                            Создать пользователя
                                        </Button>
                                    </DialogTrigger>
                                    <DialogContent>
                                        <DialogHeader>
                                            <DialogTitle>Создать нового пользователя</DialogTitle>
                                        </DialogHeader>
                                        <CreateUserForm
                                            formData={formData}
                                            setFormData={setFormData}
                                            mutation={createUserMutation}
                                            onClose={() => setIsOpen(false)}
                                        />
                                    </DialogContent>
                                </Dialog>
                            </>
                        }
                    />
                }
                filters={
                    <UsersListLayoutFilters
                        sort={
                            <UsersSortSelect
                                value={usersFilters.sort}
                                onValueChange={usersFilters.setSort}
                            />
                        }
                        filters={
                            <div className="flex gap-4">
                                <UsersSearchInput
                                    value={usersFilters.search}
                                    onChange={usersFilters.setSearch}
                                />
                                <UsersStatusFilterSelect
                                    value={usersFilters.status}
                                    onValueChange={usersFilters.setStatus}
                                />
                                <UsersRoleFilterSelect
                                    value={usersFilters.role}
                                    onValueChange={usersFilters.setRole}
                                />
                            </div>
                        }
                        actions={
                            <ViewModeToggle
                                value={viewMode}
                                onChange={(value) => setViewMode(value)}
                            />
                        }
                    />
                }
            >
                <UsersListLayoutContent
                    isEmpty={usersQuery.users.length === 0}
                    isPending={usersQuery.isPending}
                    isPendingNext={usersQuery.isFetchingNextPage}
                    cursorRef={usersQuery.cursorRef as React.RefObject<HTMLDivElement | null>}
                    hasCursor={usersQuery.hasNextPage}
                    mode={viewMode}
                    renderList={() =>
                        usersQuery.users.map((user) => (
                            <Link key={user.id} to={`/users/${user.id}`}>
                                <UserItem user={user} className="mb-4 cursor-pointer hover:bg-gray-100 transition-colors" />
                            </Link>
                        ))
                    }
                    renderGrid={() =>
                        usersQuery.users.map((user) => (
                            <Link key={user.id} to={`/users/${user.id}`}>
                                <UserCard user={user} className="mb-4 cursor-pointer hover:shadow-md hover:bg-gray-50 transition-all" />
                            </Link>
                        ))
                    }
                />
            </UsersListLayout>
        </>
    );
}

export const Component = UsersListPage;