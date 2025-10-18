import { useForm } from "react-hook-form";
import { UpdateUserRequest } from "@/shared/api/types";
import { Button } from "@/shared/ui/kit/button";
import { Input } from "@/shared/ui/kit/input";
import { Label } from "@/shared/ui/kit/label";
import { Alert, AlertDescription, AlertTitle } from "@/shared/ui/kit/alert";
import {AlertCircle, Loader2} from "lucide-react";
import { ApiSchemas } from "@/shared/api/schema";

interface EditProfileFormProps {
    user: ApiSchemas["ProfileResponse"];
    onCancel: () => void;
    onSuccess: () => void;
    isPending: boolean;
    error: Error | null;
    updateUser: (args: { path: { id: string }; body: UpdateUserRequest }) => void;
}

export function EditProfileForm({ user, onCancel, onSuccess, isPending, error, updateUser }: EditProfileFormProps) {
    const { register, handleSubmit, formState: { errors } } = useForm<UpdateUserRequest>({
        defaultValues: {
            username: user.username,
            email: user.email,
            password: "",
            firstName: user.firstName,
            lastName: user.lastName,
        },
    });

    const onSubmit = (data: UpdateUserRequest) => {
        updateUser({ path: { id: user.id }, body: data });
        onSuccess();
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {error && (
                <Alert variant="destructive">
                    <AlertCircle className="w-4 h-4" />
                    <AlertTitle>Ошибка</AlertTitle>
                    <AlertDescription>
                        {error.message === "USER_EXISTS"
                            ? "Пользователь с таким email уже существует"
                            : error.message || "Не удалось обновить профиль"}
                    </AlertDescription>
                </Alert>
            )}
            <div>
                <Label htmlFor="username" className="block mb-2">Имя пользователя</Label>
                <Input
                    id="username"
                    className="mt-1"
                    {...register("username", { required: "Имя пользователя обязательно" })}
                />
                {errors.username && <p className="text-red-500 text-sm">{errors.username.message}</p>}
            </div>
            <div>
                <Label htmlFor="email" className="block mb-2">Email</Label>
                <Input
                    id="email"
                    type="email"
                    className="mt-1"
                    {...register("email", {
                        required: "Email обязателен",
                        pattern: { value: /^\S+@\S+$/i, message: "Неверный формат email" },
                    })}
                />
                {errors.email && <p className="text-red-500 text-sm">{errors.email.message}</p>}
            </div>
            <div>
                <Label htmlFor="password" className="block mb-2">Новый пароль</Label>
                <Input
                    id="password"
                    type="password"
                    className="mt-1"
                    {...register("password", { required: "Пароль обязателен" })}
                />
                {errors.password && <p className="text-red-500 text-sm">{errors.password.message}</p>}
            </div>
            <div>
                <Label htmlFor="firstName" className="block mb-2">Имя</Label>
                <Input
                    id="firstName"
                    className="mt-1"
                    {...register("firstName")}
                />
            </div>
            <div>
                <Label htmlFor="lastName" className="block mb-2">Фамилия</Label>
                <Input
                    id="lastName"
                    className="mt-1"
                    {...register("lastName")}
                />
            </div>
            <div className="flex gap-2">
                <Button type="submit" disabled={isPending}>
                    {isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : "Сохранить"}
                </Button>
                <Button type="button" variant="outline" onClick={onCancel}>
                    Отмена
                </Button>
            </div>
        </form>
    );
}