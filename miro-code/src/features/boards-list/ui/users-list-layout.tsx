import { ReactNode } from "react";

interface UsersListLayoutProps {
    sidebar?: ReactNode;
    header?: ReactNode;
    filters?: ReactNode;
    children: ReactNode;
}

export function UsersListLayout({
                                    sidebar,
                                    header,
                                    filters,
                                    children,
                                }: UsersListLayoutProps) {
    return (
        <div className="flex h-screen">
            {sidebar && <div className="w-64 bg-gray-100 p-4">{sidebar}</div>}
            <div className="flex-1 flex flex-col">
                {header && <div className="p-6 border-b">{header}</div>}
                {filters && <div className="p-4 border-b">{filters}</div>}
                <div className="flex-1 p-6 overflow-auto">{children}</div>
            </div>
        </div>
    );
}

interface UsersListLayoutHeaderProps {
    title: string;
    description: string;
    actions?: ReactNode;
}

export function UsersListLayoutHeader({
                                          title,
                                          description,
                                          actions,
                                      }: UsersListLayoutHeaderProps) {
    return (
        <div className="flex justify-between items-center">
            <div>
                <h1 className="text-2xl font-bold">{title}</h1>
                <p className="text-gray-600">{description}</p>
            </div>
            <div className="flex gap-2">{actions}</div>
        </div>
    );
}

interface UsersListLayoutFiltersProps {
    sort?: ReactNode;
    filters?: ReactNode;
    actions?: ReactNode;
}

export function UsersListLayoutFilters({
                                           sort,
                                           filters,
                                           actions,
                                       }: UsersListLayoutFiltersProps) {
    return (
        <div className="flex justify-between items-center">
            <div className="flex gap-4">
                {sort}
                {filters}
            </div>
            {actions}
        </div>
    );
}

interface UsersListLayoutContentProps {
    isEmpty: boolean;
    isPending: boolean;
    isPendingNext: boolean;
    cursorRef: React.RefObject<HTMLDivElement>;
    hasCursor: boolean;
    mode: "list" | "grid";
    renderList: () => ReactNode;
    renderGrid: () => ReactNode;
}

export function UsersListLayoutContent({
                                           isEmpty,
                                           isPending,
                                           isPendingNext,
                                           cursorRef,
                                           hasCursor,
                                           mode,
                                           renderList,
                                           renderGrid,
                                       }: UsersListLayoutContentProps) {
    if (isPending) return <div>Загрузка...</div>;
    if (isEmpty) return <div>Пользователи не найдены</div>;

    return (
        <div>
            <div
                className={
                    mode === "grid"
                        ? "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4"
                        : "space-y-4"
                }
            >
                {mode === "list" ? renderList() : renderGrid()}
            </div>
            {hasCursor && (
                <div ref={cursorRef} className="h-10">
                    {isPendingNext && <div>Загрузка...</div>}
                </div>
            )}
        </div>
    );
}