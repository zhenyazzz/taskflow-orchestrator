import { useEffect, useRef } from "react";
import { Client, IFrame, IMessage } from "@stomp/stompjs";
import { toast } from "sonner";
import { useSession } from "@/shared/model/session";
import { 
  Bell, 
  CheckCircle2, 
  MessageSquare, 
  AlertCircle, 
  Trash2, 
  User, 
  FileText, 
  Rocket,
  ShieldAlert,
  LogIn
} from "lucide-react";
import React from "react";

export type NotificationDto = {
  id: string;
  userId: string;
  message: string;
  type: string;
  metadata?: Record<string, string>;
  read: boolean;
  createdAt: string;
};

const WS_URL = "ws://localhost:8085/notifications/ws";

const NOTIFICATION_THEMES: Record<string, { label: string; variant: "success" | "error" | "info" | "warning" | "default"; icon: React.ReactNode }> = {
  TASK_CREATED: { label: "Новая задача", variant: "success", icon: React.createElement(Rocket, { className: "w-5 h-5 text-green-500" }) },
  TASK_UPDATED: { label: "Задача обновлена", variant: "info", icon: React.createElement(FileText, { className: "w-5 h-5 text-blue-500" }) },
  TASK_SUBSCRIBED: { label: "Подписка на задачу", variant: "success", icon: React.createElement(Bell, { className: "w-5 h-5 text-green-500" }) },
  TASK_UNSUBSCRIBED: { label: "Отписка от задачи", variant: "info", icon: React.createElement(Bell, { className: "w-5 h-5 text-slate-500" }) },
  TASK_COMPLETED: { label: "Задача завершена", variant: "success", icon: React.createElement(CheckCircle2, { className: "w-5 h-5 text-green-500" }) },
  TASK_DELETED: { label: "Задача удалена", variant: "warning", icon: React.createElement(Trash2, { className: "w-5 h-5 text-amber-500" }) },
  TASK_STATUS_UPDATED: { label: "Статус изменен", variant: "info", icon: React.createElement(AlertCircle, { className: "w-5 h-5 text-blue-500" }) },
  TASK_ASSIGNEE_UPDATED: { label: "Исполнитель изменен", variant: "info", icon: React.createElement(User, { className: "w-5 h-5 text-blue-500" }) },
  COMMENT_CREATED: { label: "Новый комментарий", variant: "info", icon: React.createElement(MessageSquare, { className: "w-5 h-5 text-blue-500" }) },
  COMMENT_UPDATED: { label: "Комментарий изменен", variant: "info", icon: React.createElement(MessageSquare, { className: "w-5 h-5 text-blue-500" }) },
  COMMENT_DELETED: { label: "Комментарий удален", variant: "warning", icon: React.createElement(Trash2, { className: "w-5 h-5 text-amber-500" }) },
  ATTACHMENT_ADDED: { label: "Файл добавлен", variant: "info", icon: React.createElement(FileText, { className: "w-5 h-5 text-blue-500" }) },
  ATTACHMENT_DELETED: { label: "Файл удален", variant: "warning", icon: React.createElement(Trash2, { className: "w-5 h-5 text-amber-500" }) },
  USER_LOGIN: { label: "Вход в систему", variant: "success", icon: React.createElement(LogIn, { className: "w-5 h-5 text-green-500" }) },
  LOGIN_FAIL: { label: "Ошибка входа", variant: "error", icon: React.createElement(ShieldAlert, { className: "w-5 h-5 text-red-500" }) },
  USER_PROFILE_UPDATED: { label: "Профиль обновлен", variant: "success", icon: React.createElement(User, { className: "w-5 h-5 text-green-500" }) },
  USER_ROLE_UPDATE: { label: "Роль изменена", variant: "warning", icon: React.createElement(ShieldAlert, { className: "w-5 h-5 text-amber-500" }) },
};

export function useNotifications() {
  const { session } = useSession();
  const stompClientRef = useRef<Client | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!session?.userId || !token) {
      stompClientRef.current?.deactivate();
      stompClientRef.current = null;
      return;
    }

    const client = new Client({
      brokerURL: WS_URL,

      connectHeaders: {
        Authorization: `Bearer ${token}`,
        userId: session.userId,
      },

      debug: (msg) => console.log("WS:", msg),

      reconnectDelay: 5000,

      onConnect: (frame: IFrame) => {
        console.log("WS CONNECTED", frame);

        client.subscribe("/user/queue/notifications", (message: IMessage) => {
          try {
            const notification: NotificationDto = JSON.parse(message.body);
            showNotification(notification);
          } catch (e) {
            console.error("WS parse error", e);
          }
        });
      },

      onStompError: (frame: IFrame) => {
        console.error("STOMP ERROR:", frame.headers["message"]);
        console.error("DETAILS:", frame.body);
      },
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
      stompClientRef.current = null;
    };
  }, [session?.userId]);

  const showNotification = (notification: NotificationDto) => {
    const theme = NOTIFICATION_THEMES[notification.type] || {
      label: "Уведомление",
      variant: "default",
      icon: React.createElement(Bell, { className: "w-5 h-5" })
    };

    toast.custom((t) => (
      React.createElement("div", {
        className: `
          flex items-start gap-4 p-4 min-w-[350px] max-w-[500px] 
          bg-background border rounded-xl shadow-2xl animate-in fade-in slide-in-from-right-8
          ${theme.variant === "error" ? "border-red-200" : "border-border"}
        `
      }, [
        React.createElement("div", { key: "icon", className: "mt-1" }, theme.icon),
        React.createElement("div", { key: "content", className: "flex-1 min-w-0" }, [
          React.createElement("p", { key: "label", className: "text-sm font-semibold text-foreground mb-1" }, theme.label),
          React.createElement("p", { key: "message", className: "text-sm text-muted-foreground leading-relaxed break-words" }, notification.message),
          React.createElement("div", { key: "footer", className: "mt-3 flex items-center justify-between" }, [
            React.createElement("span", { key: "time", className: "text-[10px] text-muted-foreground/60 uppercase tracking-wider font-medium" }, 
              new Date(notification.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
            ),
            React.createElement("button", {
              key: "button",
              onClick: () => toast.dismiss(t),
              className: "text-[11px] font-bold text-primary hover:underline transition-all"
            }, "ПОНЯТНО")
          ])
        ])
      ])
    ), {
      duration: 6000,
    });
  };
}
