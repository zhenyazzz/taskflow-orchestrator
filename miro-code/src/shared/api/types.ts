export interface CreateUserRequest {
    username: string;
    password: string;
    email: string;
    firstName?: string;
    lastName?: string;
    phone?: string;
    roles: string[];
}

export interface UpdateUserRequest {
    username: string;
    password: string;
    email: string;
    firstName?: string;
    lastName?: string;
    phone?: string;
}

export interface UserResponse {
    id: string;
    username: string;
    email: string;
    firstName?: string;
    lastName?: string;
    phone?: string;
    roles: string[];
    status: "ACTIVE" | "INACTIVE" | "PENDING";
    createdAt: string;
    updatedAt?: string;
}