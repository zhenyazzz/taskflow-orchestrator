// types/User.ts
// @ts-ignore
export enum Role {
    ADMIN = 'ADMIN',
    USER = 'USER',
    MANAGER = 'MANAGER',
    // Добавь другие роли, если они есть на бэкенде
}

// @ts-ignore
export enum UserStatus {
    ACTIVE = 'ACTIVE',
    INACTIVE = 'INACTIVE',
    // Добавь другие статусы, если они есть на бэкенде
}

export interface User {
    id: string;              // UUID преобразуется в строку на фронтенде
    username: string;        // Уникальный логин, обязательное поле
    email: string;           // Уникальный email, обязательное поле
    firstName?: string;      // Имя, опционально (nullable на бэкенде)
    lastName?: string;       // Фамилия, опционально
    phone?: string;          // Телефон, опционально
    roles: Set<Role>;        // Множество ролей, соответствует @ElementCollection
    status: UserStatus;      // Статус, обязательное поле с дефолтом ACTIVE
    createdAt: Date;         // Дата создания
    updatedAt?: Date;        // Дата последнего обновления, опционально
}