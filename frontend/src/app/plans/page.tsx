"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { fetchApi } from "@/lib/api";
import { useChildStore } from "@/stores/childStore";
import type { WeeklyPlanResponse, ChildResponse } from "@/lib/types";
import AppShell from "@/components/layout/AppShell";
import PlanCard from "@/components/plan/PlanCard";
import GenerateButton from "@/components/plan/GenerateButton";
import Spinner from "@/components/ui/Spinner";
import EmptyState from "@/components/ui/EmptyState";

export default function PlansPage() {
  const router = useRouter();
  const { selectedChildId, setSelectedChildId } = useChildStore();
  const [plans, setPlans] = useState<WeeklyPlanResponse[]>([]);
  const [children, setChildren] = useState<ChildResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const loadData = useCallback(async () => {
    try {
      const childData = await fetchApi<ChildResponse[]>("/api/children");
      setChildren(childData);

      if (childData.length === 0) {
        router.replace("/children");
        return;
      }

      const childId = selectedChildId || childData[0].id;
      if (!selectedChildId) setSelectedChildId(childData[0].id);

      const planData = await fetchApi<WeeklyPlanResponse[]>(
        `/api/plans?childId=${childId}`
      );
      setPlans(planData);
    } catch {
      // handled by fetchApi
    } finally {
      setLoading(false);
    }
  }, [selectedChildId, setSelectedChildId, router]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleChildSelect = async (childId: number) => {
    setSelectedChildId(childId);
    setLoading(true);
    try {
      const planData = await fetchApi<WeeklyPlanResponse[]>(
        `/api/plans?childId=${childId}`
      );
      setPlans(planData);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerated = (plan: WeeklyPlanResponse) => {
    router.push(`/plans/${plan.id}`);
  };

  return (
    <AppShell>
      <div className="px-4 pt-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-4">식단 관리</h1>

        {/* Child Selector */}
        {children.length > 1 && (
          <div className="flex gap-2 mb-4 overflow-x-auto pb-1">
            {children.map((child) => (
              <button
                key={child.id}
                onClick={() => handleChildSelect(child.id)}
                className={`flex-shrink-0 px-3 py-1.5 rounded-full text-sm transition-colors ${
                  selectedChildId === child.id
                    ? "bg-brand text-white"
                    : "bg-gray-100 text-gray-600 hover:bg-gray-200"
                }`}
              >
                {child.name}
              </button>
            ))}
          </div>
        )}

        {/* AI Generate */}
        <div className="mb-6">
          <GenerateButton
            childList={children}
            selectedChildId={selectedChildId}
            onGenerated={handleGenerated}
          />
        </div>

        {/* Plan List */}
        {loading ? (
          <Spinner className="mt-10" />
        ) : plans.length === 0 ? (
          <EmptyState
            icon="📋"
            title="아직 식단이 없어요"
            description="AI로 맞춤 식단을 생성해보세요"
          />
        ) : (
          <div className="flex flex-col gap-3">
            {plans.map((plan) => (
              <PlanCard key={plan.id} plan={plan} />
            ))}
          </div>
        )}
      </div>
    </AppShell>
  );
}
