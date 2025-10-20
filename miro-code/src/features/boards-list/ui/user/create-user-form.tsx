import { Button } from "@/shared/ui/kit/button";
import { Input } from "@/shared/ui/kit/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/ui/kit/select";
import { components } from "@/shared/api/schema/generated";
import { UseMutationResult } from "@tanstack/react-query";
import { FormEvent } from "react";

interface CreateUserFormProps {
  formData: components["schemas"]["CreateUserRequest"];
  setFormData: (data: components["schemas"]["CreateUserRequest"]) => void;
  mutation: UseMutationResult<any, unknown, components["schemas"]["CreateUserRequest"]>;
  onClose: () => void;
}

export function CreateUserForm({ formData, setFormData, mutation, onClose }: CreateUserFormProps) {
  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    mutation.mutate(formData);
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="space-y-4">
        <Input
          placeholder="Username"
          value={formData.username}
          onChange={(e) => setFormData({ ...formData, username: e.target.value })}
        />
        <Input
          type="password"
          placeholder="Password"
          value={formData.password}
          onChange={(e) => setFormData({ ...formData, password: e.target.value })}
        />
        <Input
          placeholder="Email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        />
        <Input
          placeholder="First Name"
          value={formData.firstName}
          onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
        />
        <Input
          placeholder="Last Name"
          value={formData.lastName}
          onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
        />
        <Select
          value={formData.roles?.[0] || ""}
          onValueChange={(value: "ROLE_USER" | "ROLE_ADMIN") =>
            setFormData({
              ...formData,
              roles: value === "ROLE_ADMIN" ? ["ROLE_ADMIN", "ROLE_USER"] : [value],
            })
          }
        >
          <SelectTrigger>
            <SelectValue placeholder="Select role" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ROLE_USER">User</SelectItem>
            <SelectItem value="ROLE_ADMIN">Admin</SelectItem>
            {/* Добавьте другие роли по необходимости */}
          </SelectContent>
        </Select>
      </div>
      <div className="mt-6 flex justify-end gap-4">
        <Button type="button" variant="outline" onClick={onClose}>
          Назад
        </Button>
        <Button type="submit" disabled={mutation.isPending}>
          Создать
        </Button>
      </div>
    </form>
  );
}