import { queryClient } from "@/shared/api/query-client";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "@/shared/ui/kit/sonner";

export function Providers({ children }: { children: React.ReactNode }) {
  return (
    <QueryClientProvider client={queryClient}>
      {children}
      <Toaster />
    </QueryClientProvider>
  );
}
