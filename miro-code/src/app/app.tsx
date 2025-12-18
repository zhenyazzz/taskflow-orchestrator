import { Outlet } from "react-router-dom";
import { useNotifications } from "@/features/notifications/model/use-notifications";

export function App() {
  useNotifications();
  
  return (
    <div className="min-h-screen flex flex-col">
      <Outlet />
    </div>
  );
}
