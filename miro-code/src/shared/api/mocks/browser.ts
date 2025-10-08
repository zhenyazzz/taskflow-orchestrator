import { setupWorker } from "msw/browser";
import { tasksHandlers } from "./handlers/tasks";
import { authHandlers } from "./handlers/auth";
import { boardsHandlers } from "./handlers/boards-compat";

export const worker = setupWorker(...authHandlers, ...tasksHandlers, ...boardsHandlers);
