"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { fetchApi } from "@/lib/api";
import type { WeeklyPlanResponse } from "@/lib/types";
import AppShell from "@/components/layout/AppShell";
import WeeklyGrid from "@/components/plan/WeeklyGrid";
import Button from "@/components/ui/Button";
import Spinner from "@/components/ui/Spinner";

export default function PlanDetailPage() {
  const params = useParams();
  const router = useRouter();
  const [plan, setPlan] = useState<WeeklyPlanResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchApi<WeeklyPlanResponse>(`/api/plans/${params.id}`)
      .then(setPlan)
      .catch(() => router.replace("/plans"))
      .finally(() => setLoading(false));
  }, [params.id, router]);

  const handleDelete = async () => {
    if (!confirm("이 식단을 삭제하시겠습니까?")) return;
    await fetchApi(`/api/plans/${params.id}`, { method: "DELETE" });
    router.replace("/plans");
  };

  return (
    <AppShell>
      <div className="px-4 pt-6">
        {loading || !plan ? (
          <Spinner className="mt-20" />
        ) : (
          <>
            {/* Header */}
            <div className="flex items-center justify-between mb-4">
              <button
                onClick={() => router.back()}
                className="text-gray-400 hover:text-gray-600"
              >
                <svg
                  className="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M15 19l-7-7 7-7"
                  />
                </svg>
              </button>
              <h1 className="text-lg font-bold text-gray-900">
                주간 식단
              </h1>
              <div className="w-6" />
            </div>

            {/* Meta */}
            <div className="flex items-center gap-3 mb-4">
              <span className="text-sm text-gray-600">
                {plan.child_name}
              </span>
              <span
                className={`px-2 py-0.5 text-xs rounded-full ${
                  plan.created_by === "AI"
                    ? "bg-purple-50 text-purple-600"
                    : "bg-gray-100 text-gray-600"
                }`}
              >
                {plan.created_by === "AI" ? "🤖 AI 생성" : "✏️ 직접 생성"}
              </span>
            </div>

            {/* Weekly Grid */}
            <WeeklyGrid
              meals={plan.meals}
              weekStartDate={plan.week_start_date}
            />

            {/* Shopping List */}
            <Button
              variant="secondary"
              className="w-full mt-6"
              onClick={() => router.push(`/plans/${params.id}/shopping`)}
            >
              장보기 목록
            </Button>

            {/* Delete */}
            <Button
              variant="ghost"
              className="w-full mt-2 text-red-400 hover:text-red-500 hover:bg-red-50"
              onClick={handleDelete}
            >
              식단 삭제
            </Button>
          </>
        )}
      </div>
    </AppShell>
  );
}
