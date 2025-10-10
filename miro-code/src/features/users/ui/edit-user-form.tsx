// features/users/ui/user/edit-user-form.tsx
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/shared/ui/kit/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/shared/ui/kit/form";
import { Input } from "@/shared/ui/kit/input";
import { Checkbox } from "@/shared/ui/kit/checkbox";
import { Badge } from "@/shared/ui/layouts/badge";
import { Loader2, Save, X, Shield, Mail, User as UserIcon } from "lucide-react";
import { editUserSchema, EditUserFormData } from "../../lib/validations";
import { useUpdateUser } from "../../model/use-user";
import type { components } from "@/shared/api/schema";

type User = components["schemas"]["User"];

interface EditUserFormProps {
  user: User;
  onCancel: () => void;
  onSuccess?: () => void;
}

const ROLE_OPTIONS = [
  { value: "USER", label: "Пользователь" },
  { value: "MODERATOR", label: "Модератор" },
  { value: "ADMIN", label: "Администратор" },
];

export function EditUserForm({ user, onCancel, onSuccess }: EditUserFormProps) {
  const updateUserMutation = useUpdateUser();

  const form = useForm<EditUserFormData>({
    resolver: zodResolver(editUserSchema),
    defaultValues: {
      email: user.email || "",
      firstName: user.firstName || "",
      lastName: user.lastName || "",
      roles: user.roles || ["USER"],
    },
  });

  const onSubmit = (data: EditUserFormData) => {
    updateUserMutation.mutate(
      { userId: user.id, data },
      {
        onSuccess: () => {
          onSuccess?.();
        },
      }
    );
  };

  useEffect(() => {
    form.reset({
      email: user.email || "",
      firstName: user.firstName || "",
      lastName: user.lastName || "",
      roles: user.roles || ["USER"],
    });
  }, [user, form]);

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="firstName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Имя</FormLabel>
                <FormControl>
                  <Input placeholder="Введите имя" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="lastName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Фамилия</FormLabel>
                <FormControl>
                  <Input placeholder="Введите фамилию" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name="email"
          render={({ field }) => (
            <FormItem>
              <FormLabel className="flex items-center gap-2">
                <Mail className="w-4 h-4" />
                Email
              </FormLabel>
              <FormControl>
                <Input type="email" placeholder="user@example.com" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="roles"
          render={() => (
            <FormItem>
              <FormLabel className="flex items-center gap-2">
                <Shield className="w-4 h-4" />
                Роли пользователя
              </FormLabel>
              <div className="space-y-2">
                {ROLE_OPTIONS.map((role) => (
                  <FormField
                    key={role.value}
                    control={form.control}
                    name="roles"
                    render={({ field }) => {
                      return (
                        <FormItem
                          key={role.value}
                          className="flex flex-row items-start space-x-3 space-y-0"
                        >
                          <FormControl>
                            <Checkbox
                              checked={field.value?.includes(role.value)}
                              onCheckedChange={(checked) => {
                                return checked
                                  ? field.onChange([...field.value, role.value])
                                  : field.onChange(
                                      field.value?.filter(
                                        (value) => value !== role.value
                                      )
                                    );
                              }}
                            />
                          </FormControl>
                          <FormLabel className="font-normal">
                            {role.label}
                          </FormLabel>
                        </FormItem>
                      );
                    }}
                  />
                ))}
              </div>
              <FormDescription>
                Выберите роли, которые будут назначены пользователю
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Статическая информация */}
        <div className="pt-4 border-t space-y-4">
          <h3 className="font-semibold flex items-center gap-2">
            <UserIcon className="w-4 h-4" />
            Системная информация
          </h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <FormLabel>Имя пользователя</FormLabel>
              <Input value={user.username} disabled className="mt-1 bg-muted" />
              <FormDescription>Имя пользователя нельзя изменить</FormDescription>
            </div>
            
            <div>
              <FormLabel>ID пользователя</FormLabel>
              <Input value={user.id} disabled className="mt-1 bg-muted font-mono text-sm" />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <FormLabel>Статус</FormLabel>
              <div className="flex items-center gap-2 mt-1">
                <div
                  className={`w-2 h-2 rounded-full ${
                    user.status === "ACTIVE" ? "bg-green-500" : "bg-red-500"
                  }`}
                />
                <span className="capitalize">
                  {user.status === "ACTIVE" ? "Активный" : "Неактивный"}
                </span>
              </div>
            </div>
            
            <div>
              <FormLabel>Дата регистрации</FormLabel>
              <Input 
                value={user.createdAt ? new Date(user.createdAt).toLocaleDateString("ru-RU") : "Не указано"} 
                disabled 
                className="mt-1 bg-muted" 
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-4">
          <Button
            type="button"
            variant="outline"
            onClick={onCancel}
            disabled={updateUserMutation.isPending}
          >
            <X className="w-4 h-4 mr-2" />
            Отмена
          </Button>
          <Button 
            type="submit" 
            disabled={updateUserMutation.isPending || !form.formState.isDirty}
          >
            {updateUserMutation.isPending ? (
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
            ) : (
              <Save className="w-4 h-4 mr-2" />
            )}
            Сохранить изменения
          </Button>
        </div>
      </form>
    </Form>
  );
}