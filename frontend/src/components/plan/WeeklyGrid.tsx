"use client";

import Link from "next/link";
import type { MealResponse, MealTime } from "@/lib/types";

interface WeeklyGridProps {
  meals: MealResponse[];
  weekStartDate: string;
}

const MEAL_TIMES: { key: MealTime; label: string }[] = [
  { key: "BREAKFAST", label: "아침" },
  { key: "LUNCH", label: "점심" },
  { key: "DINNER", label: "저녁" },
  { key: "SNACK", label: "간식" },
];

const DAY_LABELS = ["월", "화", "수", "목", "금", "토", "일"];

export default function WeeklyGrid({ meals, weekStartDate }: WeeklyGridProps) {
  const startDate = new Date(weekStartDate);
  const dates = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(startDate);
    d.setDate(d.getDate() + i);
    return d.toISOString().split("T")[0];
  });

  const getMeal = (date: string, mealTime: MealTime) =>
    meals.find((m) => m.date === date && m.meal_time === mealTime);

  return (
    <div className="overflow-x-auto -mx-4 px-4">
      <table className="w-full min-w-[500px] border-collapse">
        <thead>
          <tr>
            <th className="p-2 text-xs text-gray-400 font-medium text-left w-12"></th>
            {dates.map((date, i) => (
              <th key={date} className="p-2 text-center">
                <div className="text-xs text-gray-400 font-medium">
                  {DAY_LABELS[i]}
                </div>
                <div className="text-sm font-bold text-gray-700">
                  {new Date(date).getDate()}
                </div>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {MEAL_TIMES.map(({ key, label }) => (
            <tr key={key}>
              <td className="p-2 text-xs text-gray-400 font-medium align-top">
                {label}
              </td>
              {dates.map((date) => {
                const meal = getMeal(date, key);
                return (
                  <td key={`${date}-${key}`} className="p-1 align-top">
                    {meal ? (
                      <Link href={`/recipes/${meal.recipe_id}`}>
                        <div className="p-1.5 bg-brand-50 rounded-lg text-xs text-gray-700 hover:bg-brand-100 transition-colors leading-tight">
                          {meal.recipe_name}
                        </div>
                      </Link>
                    ) : (
                      <div className="p-1.5 bg-gray-50 rounded-lg text-xs text-gray-300 text-center">
                        -
                      </div>
                    )}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
