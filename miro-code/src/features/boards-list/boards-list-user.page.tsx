import { useState, useEffect, RefObject } from "react";
import { Button } from "@/shared/ui/kit/button";
import { PlusIcon } from "lucide-react";
import { useUsersList } from "./model/user/use-users-list";
import { useUsersFilters } from "./model/task/use-users-filters";
import { useDebouncedValue } from "@/shared/lib/react";
import { useCreateUser } from "./model/user/use-create-user";
import {
    UsersListLayout,
    UsersListLayoutContent,
    UsersListLayoutFilters,
    UsersListLayoutHeader,
} from "../users/ui/users-list-layout.tsx";
import { ViewMode, ViewModeToggle } from "./ui/view-mode-toggle";
import { UsersSortSelect } from "../users/ui/users-sort-select.tsx";
import { UsersSearchInput } from "../users/ui/users-search-input.tsx";
import { UsersStatusFilterSelect } from "../users/ui/users-status-filter-select.tsx";
import { UsersRoleFilterSelect } from "../users/ui/users-role-filter-select.tsx";
import { UserItem } from "./compose/user-item";
import { UserCard } from "./compose/user-card";
import { BoardsSidebar } from "@/features/boards-list/ui/task/boards-sidebar";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/shared/ui/kit/dialog";
import { CreateUserForm } from "../users/ui/create-user-form.tsx";
import { Link } from "react-router-dom";
import { ApiSchemas } from "@/shared/api/schema";

function UsersListPage() {
    const usersFilters = useUsersFilters();
    const usersQuery = useUsersList({
        sort: usersFilters.sort,
        username: useDebouncedValue(usersFilters.search, 300),
        role: usersFilters.role as "ROLE_USER" | "ROLE_ADMIN" | null,
    });

    const createUserMutation = useCreateUser();
    const [viewMode, setViewMode] = useState<ViewMode>("list");
    const [isOpen, setIsOpen] = useState(false);
    const [formData, setFormData] = useState<ApiSchemas["CreateUserRequest"]>({
        username: "",
        password: "",
        email: "",
        firstName: "",
        lastName: "",
        roles: ["ROLE_USER"],
    });

    useEffect(() => {
        if (createUserMutation.isSuccess) {
            setIsOpen(false);
            createUserMutation.reset();
            setFormData({
                username: "",
                password: "",
                email: "",
                firstName: "",
                lastName: "",
                roles: ["ROLE_USER"],
            });
        }
    }, [createUserMutation.isSuccess]);

    if (usersQuery.error as Error | null) {
        return <div>Error loading users: {usersQuery.error?.message}</div>;
    }

    return (

        <UsersListLayout

            sidebar={<BoardsSidebar />}
            header={
                <UsersListLayoutHeader
                    title="Пользователи"
                    description="Здесь вы можете просматривать и управлять пользователями системы"
                    actions={
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
                cursorRef={usersQuery.cursorRef as RefObject<HTMLDivElement>}
                hasCursor={usersQuery.hasNextPage}
                mode={viewMode}
                renderList={() =>
                    usersQuery.users.map((user) => (
                        <Link key={user.id} to={`/users/${user.id}`}>
                            <UserItem
                                user={user}
                                className="mb-4 cursor-pointer hover:bg-gray-100 transition-colors"
                            />
                        </Link>
                    ))
                }
                renderGrid={() =>
                    usersQuery.users.map((user) => (
                        <Link key={user.id} to={`/users/${user.id}`}>
                            <UserCard
                                user={user}
                                className="mb-4 cursor-pointer hover:shadow-md hover:bg-gray-50 transition-all"
                            />
                        </Link>
                    ))
                }
            />
        </UsersListLayout>
    );
}

export const Component = UsersListPage;