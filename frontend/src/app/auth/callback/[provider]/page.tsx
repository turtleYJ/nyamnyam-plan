"use client";

import { useEffect, useRef } from "react";
import { useRouter, useSearchParams, useParams } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { fetchApi } from "@/lib/api";
import type { TokenResponse } from "@/lib/types";
import Spinner from "@/components/ui/Spinner";

export default function OAuthCallback() {
  const router = useRouter();
  const params = useParams();
  const searchParams = useSearchParams();
  const setTokens = useAuthStore((s) => s.setTokens);
  const called = useRef(false);

  useEffect(() => {
    if (called.current) return;
    called.current = true;

    const provider = params.provider as string;
    const code = searchParams.get("code");
    const state = searchParams.get("state");

    if (!code) {
      router.replace("/");
      return;
    }

    const redirectUri = `${window.location.origin}/auth/callback/${provider}`;

    fetchApi<TokenResponse>(`/api/auth/login/${provider}`, {
      method: "POST",
      body: { code, redirect_uri: redirectUri, state },
    })
      .then((res) => {
        setTokens(res.access_token, res.refresh_token);
        router.replace("/children");
      })
      .catch(() => {
        router.replace("/");
      });
  }, [params, searchParams, router, setTokens]);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <Spinner size="lg" />
      <p className="text-gray-500">로그인 처리 중...</p>
    </div>
  );
}
