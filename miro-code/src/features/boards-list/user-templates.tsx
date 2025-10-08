import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/shared/ui/kit/dialog";
import { Button } from "@/shared/ui/kit/button";
import { useCreateUser } from "./model/use-create-user";

export function useUsersTemplatesModal() {
    const [isOpen, setIsOpen] = useState(false);

    return {
        isOpen,
        open: () => setIsOpen(true),
        close: () => setIsOpen(false),
    };
}

export function UsersTemplatesModal() {
    const modal = useUsersTemplatesModal();
    const createUser = useCreateUser();

    const handleTemplateSelect = (template: { name: string; roles: string[] }) => {
        createUser.createUser({
            username: `user${Date.now()}`,
            password: "defaultPassword123",
            email: `user${Date.now()}@example.com`,
            firstName: "New",
            lastName: "User",
            roles: template.roles,
        });
        modal.close();
    };

    return (
        <Dialog open={modal.isOpen} onOpenChange={modal.close}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Выберите шаблон пользователя</DialogTitle>
                </DialogHeader>
                <div className="grid gap-4">
                    <Button
                        onClick={() => handleTemplateSelect({ name: "User", roles: ["USER"] })}
                    >
                        Обычный пользователь
                    </Button>
                    <Button
                        onClick={() => handleTemplateSelect({ name: "Admin", roles: ["ADMIN"] })}
                    >
                        Администратор
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    );
}

export function UsersTemplatesGallery() {
    const modal = useUsersTemplatesModal();

    return (
        <div className="p-4">
            <h2 className="text-lg font-semibold mb-2">Шаблоны</h2>
            <Button variant="outline" onClick={() => modal.open()}>
                Открыть шаблоны
            </Button>
        </div>
    );
}