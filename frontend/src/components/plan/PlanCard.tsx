"use client";

import Link from "next/link";
import type { WeeklyPlanResponse } from "@/lib/types";

interface PlanCardProps {
  plan: WeeklyPlanResponse;
}

export default function PlanCard({ plan }: PlanCardProps) {
  const startDate = new Date(plan.week_start_date);
  const endDate = new Date(startDate);
  endDate.setDate(endDate.getDate() + 6);

  const formatDate = (d: Date) =>
    `${d.getMonth() + 1}/${d.getDate()}`;

  return (
    <Link href={`/plans/${plan.id}`}>
      <div className="p-4 bg-white rounded-2xl border border-gray-100 hover:border-brand/30 transition-colors">
        <div className="flex items-center justify-between mb-2">
          <h3 className="font-bold text-gray-900">
            {formatDate(startDate)} ~ {formatDate(endDate)}
          </h3>
          <span
            className={`px-2 py-0.5 text-xs rounded-full ${
              plan.created_by === "AI"
                ? "bg-purple-50 text-purple-600"
                : "bg-gray-100 text-gray-600"
            }`}
          >
            {plan.created_by === "AI" ? "🤖 AI" : "✏️ 직접"}
          </span>
        </div>
        <p className="text-sm text-gray-500">
          {plan.child_name} · {plan.meals.length}끼 식단
        </p>
      </div>
    </Link>
  );
}
