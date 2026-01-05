export interface paths {
    "/auth/signIn": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["LoginRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        "Set-Cookie"?: string;
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["JwtResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/auth/signUp": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["RegisterRequest"];
                };
            };
            responses: {
                201: {
                    headers: {
                        "Set-Cookie"?: string;
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["JwtResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                409: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/auth/assign-role": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["AssignRoleRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["JwtResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/auth/remove-role": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["RemoveRoleRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["JwtResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/auth/validate": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: never;
                header: {
                    Authorization: string;
                };
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["JwtResponse"];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/auth/refresh": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        "Set-Cookie"?: string;
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["JwtResponse"];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/auth/logout": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                204: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
                401: components["responses"]["UnauthorizedError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/auth/logout-all": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                204: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/users": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                    sort?: string;
                    username?: string;
                    role?: "ROLE_USER" | "ROLE_ADMIN";
                    status?: "ACTIVE" | "INACTIVE" | "PENDING";
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["UserPageResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["CreateUserRequest"];
                };
            };
            responses: {
                201: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["UserResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
                409: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/users/{id}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["UserResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        put: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["UpdateUserRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["UserResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        post?: never;
        delete: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                204: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/users/all": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["UserResponse"][];
                    };
                };
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/me/profile": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["ProfileResponse"];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
            };
        };
        put: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["UpdateUserRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["ProfileResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
            };
        };
        post?: never;
        delete: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                204: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
                401: components["responses"]["UnauthorizedError"];
            };
        };
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/me/tasks": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                    status?: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED" | "BLOCKED";
                    department?: "IT" | "HR" | "FINANCE" | "MARKETING" | "SALES" | "CUSTOMER_SERVICE" | "PRODUCTION" | "LOGISTICS" | "RESEARCH_AND_DEVELOPMENT" | "OTHER";
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskPageResponse"];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/me/tasks/{id}/subscribe": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        trace?: never;
    };
    "/me/tasks/{id}/unsubscribe": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        trace?: never;
    };
    "/me/tasks/{id}/complete": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        trace?: never;
    };
    "/me/tasks/history": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskPageResponse"];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/me/tasks/available-tasks": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                    department?: "IT" | "HR" | "FINANCE" | "MARKETING" | "SALES" | "CUSTOMER_SERVICE" | "PRODUCTION" | "LOGISTICS" | "RESEARCH_AND_DEVELOPMENT" | "OTHER";
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskPageResponse"];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/comments/task/{taskId}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                };
                header?: never;
                path: {
                    taskId: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["CommentPageResponse"];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    taskId: string;
                };
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["CreateCommentRequest"];
                };
            };
            responses: {
                201: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["CommentResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/comments/task/{taskId}/{commentId}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    taskId: string;
                    commentId: string;
                };
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["UpdateCommentRequest"];
                };
            };
            responses: {
                201: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["CommentResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        post?: never;
        delete: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    taskId: string;
                    commentId: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                204: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/tasks": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                    sort?: string;
                    search?: string;
                    status?: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED" | "BLOCKED";
                    priority?: "LOW" | "MEDIUM" | "HIGH";
                    assigneeId?: string;
                    creatorId?: string;
                    department?: "IT" | "HR" | "FINANCE" | "MARKETING" | "SALES" | "CUSTOMER_SERVICE" | "PRODUCTION" | "LOGISTICS" | "RESEARCH_AND_DEVELOPMENT" | "OTHER";
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskPageResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "multipart/form-data": {
                        task: components["schemas"]["CreateTaskRequest"];
                        files?: string[];
                    };
                };
            };
            responses: {
                201: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/tasks/all": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"][];
                    };
                };
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/tasks/bulk": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["CreateTaskRequest"][];
                };
            };
            responses: {
                201: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"][];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/tasks/due-soon": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    hours?: number;
                    page?: number;
                    size?: number;
                    status?: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED" | "BLOCKED";
                    assigneeId?: string;
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskPageResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/tasks/{id}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        put: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["UpdateTaskRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        post?: never;
        delete: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                204: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/tasks/{id}/status": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["UpdateStatusRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        trace?: never;
    };
    "/tasks/{id}/complete": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        trace?: never;
    };
    "/tasks/{id}/assignees": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody: {
                content: {
                    "application/json": components["schemas"]["UpdateAssigneesRequest"];
                };
            };
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        trace?: never;
    };
    "/tasks/{id}/history": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                };
                header?: never;
                path: {
                    id: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskPageResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/tasks/assignee/{userId}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    page?: number;
                    size?: number;
                    status?: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED" | "BLOCKED";
                    creatorId?: string;
                    department?: "IT" | "HR" | "FINANCE" | "MARKETING" | "SALES" | "CUSTOMER_SERVICE" | "PRODUCTION" | "LOGISTICS" | "RESEARCH_AND_DEVELOPMENT" | "OTHER";
                };
                header?: never;
                path: {
                    userId: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskPageResponse"];
                    };
                };
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/attachments/task/{taskId}/batch": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    taskId: string;
                };
                cookie?: never;
            };
            requestBody: {
                content: {
                    "multipart/form-data": {
                        files?: string[];
                    };
                };
            };
            responses: {
                201: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["AttachmentResponse"][];
                    };
                };
                400: components["responses"]["BadRequestError"];
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/attachments/task/{taskId}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    taskId: string;
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["AttachmentResponse"][];
                    };
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/attachments/task/{taskId}/{attachmentIds}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get?: never;
        put?: never;
        post?: never;
        delete: {
            parameters: {
                query?: never;
                header?: never;
                path: {
                    taskId: string;
                    attachmentIds: string[];
                };
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                204: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content?: never;
                };
                401: components["responses"]["UnauthorizedError"];
                403: components["responses"]["ForbiddenError"];
                404: components["responses"]["NotFoundError"];
            };
        };
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/analytics/tasks": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    startDate?: string;
                    endDate?: string;
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["TaskAnalyticsResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/analytics/users": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    startDate?: string;
                    endDate?: string;
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["UserAnalyticsResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/analytics/logins": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    startDate?: string;
                    endDate?: string;
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["LoginAnalyticsResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/analytics/dashboard": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: {
            parameters: {
                query?: {
                    startDate?: string;
                    endDate?: string;
                };
                header?: never;
                path?: never;
                cookie?: never;
            };
            requestBody?: never;
            responses: {
                200: {
                    headers: {
                        [name: string]: unknown;
                    };
                    content: {
                        "application/json": components["schemas"]["DashboardAnalyticsResponse"];
                    };
                };
                400: components["responses"]["BadRequestError"];
                403: components["responses"]["ForbiddenError"];
            };
        };
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
}
export type webhooks = Record<string, never>;
export interface components {
    schemas: {
        LoginRequest: {
            username: string;
            password: string;
        };
        JwtResponse: {
            token: string;
            id: string;
            username: string;
            roles: ("ROLE_ADMIN" | "ROLE_USER")[];
        };
        Error: {
            message: string;
            code: string;
        };
        RegisterRequest: {
            username: string;
            email: string;
            password: string;
            firstName: string;
            lastName: string;
        };
        AssignRoleRequest: {
            id: string;
            username: string;
            role: "ROLE_USER" | "ROLE_ADMIN";
        };
        RemoveRoleRequest: {
            id: string;
            username: string;
            role: "ROLE_USER" | "ROLE_ADMIN";
        };
        UserResponse: {
            id: string;
            username: string;
            email: string;
            firstName?: string;
            lastName?: string;
            roles: ("ROLE_USER" | "ROLE_ADMIN")[];
            status: "ACTIVE" | "INACTIVE" | "PENDING";
            createdAt: string;
            updatedAt?: string;
        };
        UserPageResponse: {
            content: components["schemas"]["UserResponse"][];
            totalElements: number;
            totalPages: number;
            size: number;
            number: number;
            first?: boolean;
            last?: boolean;
            numberOfElements?: number;
            empty?: boolean;
        };
        CreateUserRequest: {
            username: string;
            password: string;
            email: string;
            firstName?: string;
            lastName?: string;
            roles?: ("ROLE_USER" | "ROLE_ADMIN")[];
        };
        UpdateUserRequest: {
            username?: string;
            password?: string;
            email?: string;
            firstName?: string;
            lastName?: string;
        };
        ProfileResponse: {
            id: string;
            username: string;
            email: string;
            firstName?: string;
            lastName?: string;
            roles: ("ROLE_USER" | "ROLE_ADMIN")[];
        };
        CommentResponse: {
            id?: string;
            content?: string;
            authorId?: string;
            createdAt?: string;
            updatedAt?: string;
        };
        TaskResponse: {
            id?: string;
            title?: string;
            description?: string;
            status?: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED" | "BLOCKED";
            priority?: "LOW" | "MEDIUM" | "HIGH";
            assigneeIds?: string[];
            creatorId?: string;
            department?: "IT" | "HR" | "FINANCE" | "MARKETING" | "SALES" | "CUSTOMER_SERVICE" | "PRODUCTION" | "LOGISTICS" | "RESEARCH_AND_DEVELOPMENT" | "OTHER";
            createdAt?: string;
            dueDate?: string;
            tags?: string[];
            comments?: components["schemas"]["CommentResponse"][];
        };
        TaskPageResponse: {
            content: components["schemas"]["TaskResponse"][];
            totalElements: number;
            totalPages: number;
            size: number;
            number: number;
            first?: boolean;
            last?: boolean;
            numberOfElements?: number;
            empty?: boolean;
        };
        CommentPageResponse: {
            content: components["schemas"]["CommentResponse"][];
            totalElements: number;
            totalPages: number;
            size: number;
            number: number;
            first?: boolean;
            last?: boolean;
            numberOfElements?: number;
            empty?: boolean;
        };
        CreateCommentRequest: {
            content: string;
        };
        UpdateCommentRequest: {
            content: string;
        };
        CreateTaskRequest: {
            title: string;
            description?: string;
            priority: "LOW" | "MEDIUM" | "HIGH";
            department: "IT" | "HR" | "FINANCE" | "MARKETING" | "SALES" | "CUSTOMER_SERVICE" | "PRODUCTION" | "LOGISTICS" | "RESEARCH_AND_DEVELOPMENT" | "OTHER";
            dueDate?: string;
            tags?: string[];
            assigneeIds?: string[];
        };
        UpdateTaskRequest: {
            title?: string;
            description?: string;
            status?: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED" | "BLOCKED";
            priority?: "LOW" | "MEDIUM" | "HIGH";
            dueDate?: string;
            tags?: string[];
            assigneeIds?: string[];
            department?: "IT" | "HR" | "FINANCE" | "MARKETING" | "SALES" | "CUSTOMER_SERVICE" | "PRODUCTION" | "LOGISTICS" | "RESEARCH_AND_DEVELOPMENT" | "OTHER";
        };
        UpdateStatusRequest: {
            status: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED" | "BLOCKED";
            comment?: string;
        };
        UpdateAssigneesRequest: {
            assigneeIds?: string[];
        };
        AttachmentResponse: {
            id?: string;
            taskId?: string;
            fileName?: string;
            objectName?: string;
            size?: number;
            url?: string;
            fileType?: string;
            createdAt?: string;
        };
        TaskAnalyticsResponse: {
            totalTasks: number;
            createdTasks: number;
            completedTasks: number;
            deletedTasks: number;
            averageCompletionTimeHours?: number | null;
            tasksByPriority: {
                [key: string]: number;
            };
            tasksByStatus: {
                [key: string]: number;
            };
            dailyCreatedTasks?: {
                [key: string]: number;
            };
            dailyCompletedTasks?: {
                [key: string]: number;
            };
        };
        UserAnalyticsResponse: {
            totalUsers: number;
            registeredUsers: number;
            updatedUsers: number;
            usersByDepartment: {
                [key: string]: number;
            };
            usersByRole: {
                [key: string]: number;
            };
        };
        LoginAnalyticsResponse: {
            totalLogins: number;
            successfulLogins: number;
            failedLogins: number;
            successRate: number;
            failureReasons: {
                [key: string]: number;
            };
            dailySuccessfulLogins: {
                [key: string]: number;
            };
            dailyFailedLogins: {
                [key: string]: number;
            };
        };
        DashboardAnalyticsResponse: {
            taskAnalytics: components["schemas"]["TaskAnalyticsResponse"];
            userAnalytics: components["schemas"]["UserAnalyticsResponse"];
            loginAnalytics: components["schemas"]["LoginAnalyticsResponse"];
            periodStart: string;
            periodEnd: string;
        };
    };
    responses: {
        BadRequestError: {
            headers: {
                [name: string]: unknown;
            };
            content: {
                "application/json": components["schemas"]["Error"];
            };
        };
        UnauthorizedError: {
            headers: {
                [name: string]: unknown;
            };
            content: {
                "application/json": components["schemas"]["Error"];
            };
        };
        ForbiddenError: {
            headers: {
                [name: string]: unknown;
            };
            content: {
                "application/json": components["schemas"]["Error"];
            };
        };
        NotFoundError: {
            headers: {
                [name: string]: unknown;
            };
            content: {
                "application/json": components["schemas"]["Error"];
            };
        };
    };
    parameters: never;
    requestBodies: never;
    headers: never;
    pathItems: never;
}
export type $defs = Record<string, never>;
export type operations = Record<string, never>;
