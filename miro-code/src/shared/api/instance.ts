import createFetchClient from "openapi-fetch";
import createClient from "openapi-react-query";
import { CONFIG } from "@/shared/model/config";
import { paths } from "./schema/generated";
import { useSession } from "../model/session";

export const fetchClient = createFetchClient<paths>({
  baseUrl: CONFIG.API_BASE_URL,
  credentials: 'include', // Для отправки cookies (refresh token)
});
export const rqClient = createClient(fetchClient);

export const publicFetchClient = createFetchClient<paths>({
  baseUrl: CONFIG.API_BASE_URL,
  credentials: 'include', // Для отправки cookies (refresh token)
});
export const publicRqClient = createClient(publicFetchClient);


fetchClient.use({
  async onRequest({ request }) {
    const token = await useSession.getState().refreshToken();
    if (token) {
      request.headers.set("Authorization", `Bearer ${token}`);
    }
  },
});
