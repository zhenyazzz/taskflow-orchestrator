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
} from "./ui/users-list-layout";
import { ViewMode, ViewModeToggle } from "./ui/view-mode-toggle";
import { UsersSortSelect } from "./ui/users-sort-select";
import { UsersSearchInput } from "./ui/users-search-input";
import { UsersStatusFilterSelect } from "./ui/users-status-filter-select";
import { UserItem } from "./compose/user-item";
import { UserCard } from "./compose/user-card";
//import { UsersSidebar } from "./ui/users-sidebar";
import {
    UsersTemplatesGallery,
    UsersTemplatesModal,
    useUsersTemplatesModal,
} from "@/features/boards-list/user-templates.tsx";
import {BoardsSidebar} from "@/features/boards-list/ui/boards-sidebar.tsx";

function UsersListPage() {
    const usersFilters = useUsersFilters();
    const usersQuery = useUsersList({
        sort: usersFilters.sort,
        search: useDebouncedValue(usersFilters.search, 300),
        status: usersFilters.status,
    });

    const templatesModal = useUsersTemplatesModal();
    const createUser = useCreateUser();
    const [viewMode, setViewMode] = useState<ViewMode>("list");

    return (
        <>
            <UsersTemplatesModal />
            <UsersListLayout
                templates={<UsersTemplatesGallery />}
                sidebar={<BoardsSidebar />}
                header={
                    <UsersListLayoutHeader
                        title="Пользователи"
                        description="Здесь вы можете просматривать и управлять пользователями системы"
                        actions={
                            <>
                                <Button variant="outline" onClick={() => templatesModal.open()}>
                                    Выбрать шаблон
                                </Button>
                                <Button
                                    disabled={createUser.isPending}
                                    onClick={() => createUser.createUser()}
                                >
                                    <PlusIcon />
                                    Создать пользователя
                                </Button>
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
                    cursorRef={usersQuery.cursorRef}
                    hasCursor={usersQuery.hasNextPage}
                    mode={viewMode}
                    renderList={() =>
                        usersQuery.users.map((user) => (
                            <UserItem key={user.id} user={user} />
                        ))
                    }
                    renderGrid={() =>
                        usersQuery.users.map((user) => (
                            <UserCard key={user.id} user={user} />
                        ))
                    }
                />
            </UsersListLayout>
        </>
    );
}

export const Component = UsersListPage;