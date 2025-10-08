import { useState } from "react";
import { jwtDecode } from "jwt-decode";
import { createGStore } from "create-gstore";
import { publicFetchClient } from "../api/instance";

type Session = {
  sub: string; // username
  userId: string; // UUID
  roles: string[]; // array of roles: EMPLOYEE, ADMIN, MANAGER
  exp: number;
  iat: number;
  iss: string; // issuer
};

const TOKEN_KEY = "token";

let refreshTokenPromise: Promise<string | null> | null = null;

export const useSession = createGStore(() => {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));

  const login = (token: string) => {
    localStorage.setItem(TOKEN_KEY, token);
    setToken(token);
  };

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
  };

  const session = token ? jwtDecode<Session>(token) : null;

  const refreshToken = async () => {
    if (!token) {
      return null;
    }

    const session = jwtDecode<Session>(token);

    // Если токен истек, разлогиниваем пользователя
    if (session.exp < Date.now() / 1000) {
      logout();
      return null;
    }

    return token;
  };

  return { refreshToken, login, logout, session };
});
