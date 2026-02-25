"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { fetchApi } from "@/lib/api";
import type { TokenResponse } from "@/lib/types";
import Button from "@/components/ui/Button";
import Input from "@/components/ui/Input";

export default function Home() {
  const router = useRouter();
  const { accessToken, setTokens } = useAuthStore();
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (accessToken) {
      router.replace("/children");
    }
  }, [accessToken, router]);

  const handleDevLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;

    setLoading(true);
    setError("");

    try {
      const res = await fetchApi<TokenResponse>("/api/auth/dev-login", {
        method: "POST",
        body: { email: email.trim() },
      });
      setTokens(res.access_token, res.refresh_token);
      router.push("/children");
    } catch {
      setError("로그인에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setLoading(false);
    }
  };

  const handleOAuth = (provider: "kakao" | "naver") => {
    const redirectUri = `${window.location.origin}/auth/callback/${provider}`;
    const clientId =
      provider === "kakao"
        ? process.env.NEXT_PUBLIC_KAKAO_CLIENT_ID
        : process.env.NEXT_PUBLIC_NAVER_CLIENT_ID;

    if (!clientId) {
      setError(`${provider} OAuth 설정이 필요합니다.`);
      return;
    }

    const authUrl =
      provider === "kakao"
        ? `https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=code`
        : `https://nid.naver.com/oauth2.0/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=code&state=naver`;

    window.location.href = authUrl;
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-b from-orange-50 to-white">
      <main className="flex flex-col items-center gap-8 px-6 text-center w-full max-w-sm">
        <div className="text-6xl">🍽️</div>

        <h1 className="text-4xl font-bold text-gray-900">냠냠플랜</h1>

        <p className="text-lg text-gray-600">
          바쁜 부모를 위한 똑똑한 이유식 플래너
        </p>

        {/* Dev Login */}
        <form onSubmit={handleDevLogin} className="w-full flex flex-col gap-3">
          <Input
            type="email"
            placeholder="이메일로 빠른 로그인"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <Button type="submit" size="lg" loading={loading} className="w-full">
            시작하기
          </Button>
          {error && <p className="text-sm text-red-500">{error}</p>}
        </form>

        {/* OAuth Divider */}
        <div className="flex items-center gap-3 w-full">
          <div className="flex-1 h-px bg-gray-200" />
          <span className="text-xs text-gray-400">소셜 로그인</span>
          <div className="flex-1 h-px bg-gray-200" />
        </div>

        {/* OAuth Buttons */}
        <div className="flex gap-3 w-full">
          <Button
            variant="secondary"
            size="lg"
            className="flex-1"
            onClick={() => handleOAuth("kakao")}
          >
            카카오
          </Button>
          <Button
            variant="secondary"
            size="lg"
            className="flex-1"
            onClick={() => handleOAuth("naver")}
          >
            네이버
          </Button>
        </div>

        {/* Features */}
        <div className="grid grid-cols-3 gap-6 mt-8 text-center">
          <div className="flex flex-col items-center gap-2">
            <span className="text-3xl">📋</span>
            <span className="text-sm text-gray-600">주간 식단</span>
          </div>
          <div className="flex flex-col items-center gap-2">
            <span className="text-3xl">🤖</span>
            <span className="text-sm text-gray-600">AI 추천</span>
          </div>
          <div className="flex flex-col items-center gap-2">
            <span className="text-3xl">📊</span>
            <span className="text-sm text-gray-600">영양소 분석</span>
          </div>
        </div>
      </main>
    </div>
  );
}
