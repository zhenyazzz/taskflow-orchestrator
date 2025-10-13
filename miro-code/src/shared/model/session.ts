import { useState } from "react";
import { jwtDecode } from "jwt-decode";
import { createGStore } from "create-gstore";
import { publicFetchClient } from "../api/instance";
import { components } from "../api/schema/generated";

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

    let currentSession: Session;
    try {
      currentSession = jwtDecode<Session>(token);
    } catch (e) {
      logout();
      return null;
    }

    // Если токен истек, пытаемся обновить его
    if (currentSession.exp < Date.now() / 1000) {
      if (!refreshTokenPromise) {
        refreshTokenPromise = publicFetchClient.POST('/auth/refresh' as const, {
          body: {},
        }).then(({ data, error }: { data?: components['schemas']['JwtResponse'], error?: any }) => {
          refreshTokenPromise = null;
          if (error) {
            logout();
            return null;
          }
          if (data?.accessToken) {
            login(data.accessToken);
            return data.accessToken;
          }
          return null;
        }).catch(() => {
          refreshTokenPromise = null;
          logout();
          return null;
        });
      }
      return refreshTokenPromise;
    }

    return token;
  };

  return { refreshToken, login, logout, session };
});

export const useIsAdmin = () => {
  const { session } = useSession();
  return session?.roles.includes("ADMIN") || false;
};