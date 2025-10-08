import { ApiSchemas } from "../../schema";
import { http } from "../http";
import { delay, HttpResponse } from "msw";
import {
  createRefreshTokenCookie,
  generateTokens,
  //verifyToken,
} from "../session";

const mockUsers: ApiSchemas["User"][] = [
  {
    id: "550e8400-e29b-41d4-a716-446655440001",
    username: "admin",
    email: "admin@company.com",
    roles: ["ADMIN"],
  },
  {
    id: "550e8400-e29b-41d4-a716-446655440002", 
    username: "employee",
    email: "employee@company.com",
    roles: ["EMPLOYEE"],
  },
  {
    id: "550e8400-e29b-41d4-a716-446655440003",
    username: "manager", 
    email: "manager@company.com",
    roles: ["MANAGER"],
  },
];

const userPasswords = new Map<string, string>();
userPasswords.set("admin@company.com", "123456");
userPasswords.set("employee@company.com", "123456");
userPasswords.set("manager@company.com", "123456");

export const authHandlers = [
  http.post("/auth/login", async ({ request }) => {
    const body = await request.json();

    const user = mockUsers.find((u) => u.email === body.email);
    const storedPassword = userPasswords.get(user?.email || "");

    await delay();

    if (!user || !storedPassword || storedPassword !== body.password) {
      return HttpResponse.json(
        {
          message: "Неверный email или пароль",
          code: "INVALID_CREDENTIALS",
        },
        { status: 401 },
      );
    }

    const { accessToken, refreshToken } = await generateTokens({
      userId: user.id,
      username: user.username,
      email: user.email,
      roles: user.roles,
    });

    return HttpResponse.json(
      {
        accessToken: accessToken,
        user,
      },
      {
        status: 200,
        headers: {
          "Set-Cookie": createRefreshTokenCookie(refreshToken),
        },
      },
    );
  }),

  http.post("/auth/register", async ({ request }) => {
    const body = await request.json();

    await delay();

    if (mockUsers.some((u) => u.email === body.email)) {
      return HttpResponse.json(
        {
          message: "Пользователь с таким email уже существует",
          code: "USER_EXISTS",
        },
        { status: 409 },
      );
    }

    const newUser: ApiSchemas["User"] = {
      id: crypto.randomUUID(),
      username: body.username,
      email: body.email,
      roles: ["EMPLOYEE"], // по умолчанию новые пользователи - сотрудники
    };

    const { accessToken, refreshToken } = await generateTokens({
      userId: newUser.id,
      username: newUser.username, 
      email: newUser.email,
      roles: newUser.roles,
    });

    mockUsers.push(newUser);
    userPasswords.set(body.email, body.password);

    return HttpResponse.json(
      {
        accessToken: accessToken,
        user: newUser,
      },
      {
        status: 201,
        headers: {
          "Set-Cookie": createRefreshTokenCookie(refreshToken),
        },
      },
    );
  }),
];
