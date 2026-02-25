"use client";

import { useEffect, useState, ReactNode } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import BottomNav from "./BottomNav";
import Spinner from "../ui/Spinner";

interface AppShellProps {
  children: ReactNode;
}

export default function AppShell({ children }: AppShellProps) {
  const router = useRouter();
  const accessToken = useAuthStore((s) => s.accessToken);
  const [hydrated, setHydrated] = useState(false);

  useEffect(() => {
    setHydrated(true);
  }, []);

  useEffect(() => {
    if (hydrated && !accessToken) {
      router.replace("/");
    }
  }, [hydrated, accessToken, router]);

  if (!hydrated) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!accessToken) return null;

  return (
    <div className="max-w-md mx-auto min-h-screen pb-20">
      {children}
      <BottomNav />
    </div>
  );
}
