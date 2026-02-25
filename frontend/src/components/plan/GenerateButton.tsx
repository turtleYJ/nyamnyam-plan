"use client";

import { useState } from "react";
import { fetchApi } from "@/lib/api";
import type { WeeklyPlanResponse, ChildResponse } from "@/lib/types";
import Button from "@/components/ui/Button";

interface GenerateButtonProps {
  childList: ChildResponse[];
  selectedChildId: number | null;
  onGenerated: (plan: WeeklyPlanResponse) => void;
}

const PROGRESS_MESSAGES = [
  "레시피를 분석하고 있어요...",
  "영양 균형을 맞추는 중...",
  "AI가 식단을 구성하고 있어요...",
  "거의 다 됐어요!",
];

export default function GenerateButton({
  childList,
  selectedChildId,
  onGenerated,
}: GenerateButtonProps) {
  const [loading, setLoading] = useState(false);
  const [progressIdx, setProgressIdx] = useState(0);
  const [error, setError] = useState("");

  const getNextMonday = () => {
    const d = new Date();
    const day = d.getDay();
    const diff = day === 0 ? 1 : 8 - day;
    d.setDate(d.getDate() + diff);
    return d.toISOString().split("T")[0];
  };

  const handleGenerate = async () => {
    if (!selectedChildId) return;

    setLoading(true);
    setError("");
    setProgressIdx(0);

    const interval = setInterval(() => {
      setProgressIdx((prev) =>
        prev < PROGRESS_MESSAGES.length - 1 ? prev + 1 : prev
      );
    }, 8000);

    try {
      const plan = await fetchApi<WeeklyPlanResponse>("/api/plans/generate", {
        method: "POST",
        body: {
          child_id: selectedChildId,
          week_start_date: getNextMonday(),
        },
      });
      onGenerated(plan);
    } catch {
      setError("식단 생성에 실패했습니다. 다시 시도해주세요.");
    } finally {
      clearInterval(interval);
      setLoading(false);
    }
  };

  const selectedChild = childList.find((c) => c.id === selectedChildId);

  return (
    <div className="flex flex-col gap-3">
      <Button
        size="lg"
        className="w-full"
        loading={loading}
        disabled={!selectedChildId}
        onClick={handleGenerate}
      >
        {loading ? PROGRESS_MESSAGES[progressIdx] : "🤖 AI 식단 생성"}
      </Button>

      {!selectedChildId && (
        <p className="text-xs text-gray-400 text-center">
          아이를 먼저 선택해주세요
        </p>
      )}

      {selectedChild && !loading && (
        <p className="text-xs text-gray-500 text-center">
          {selectedChild.name} ({selectedChild.age_in_months}개월) 맞춤 식단을
          생성합니다
        </p>
      )}

      {error && <p className="text-sm text-red-500 text-center">{error}</p>}
    </div>
  );
}
