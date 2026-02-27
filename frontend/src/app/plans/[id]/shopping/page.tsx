"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { fetchApi } from "@/lib/api";
import type { ShoppingListResponse } from "@/lib/types";
import AppShell from "@/components/layout/AppShell";
import ShoppingList from "@/components/plan/ShoppingList";
import Spinner from "@/components/ui/Spinner";
import EmptyState from "@/components/ui/EmptyState";

export default function ShoppingPage() {
  const params = useParams();
  const router = useRouter();
  const [data, setData] = useState<ShoppingListResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchApi<ShoppingListResponse>(`/api/plans/${params.id}/shopping-list`)
      .then(setData)
      .catch(() => router.replace(`/plans/${params.id}`))
      .finally(() => setLoading(false));
  }, [params.id, router]);

  return (
    <AppShell>
      <div className="px-4 pt-6 pb-8">
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
          <h1 className="text-lg font-bold text-gray-900">장보기 목록</h1>
          <div className="w-6" />
        </div>

        {loading ? (
          <Spinner className="mt-20" />
        ) : !data || data.items.length === 0 ? (
          <EmptyState
            icon="🛒"
            title="재료가 없습니다"
            description="식단에 등록된 레시피가 없거나 재료 정보가 없습니다."
          />
        ) : (
          <>
            <p className="text-sm text-gray-500 mb-4">
              {data.week_start_date} 주간 식단 기준
            </p>
            <ShoppingList planId={data.plan_id} items={data.items} />
          </>
        )}
      </div>
    </AppShell>
  );
}
