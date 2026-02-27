"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { fetchApi } from "@/lib/api";
import type { RecipeDetailResponse } from "@/lib/types";
import AppShell from "@/components/layout/AppShell";
import Spinner from "@/components/ui/Spinner";

const STAGE_LABEL: Record<string, string> = {
  EARLY: "초기",
  MIDDLE: "중기",
  LATE: "후기",
  COMPLETE: "완료기",
};

const CATEGORY_LABEL: Record<string, string> = {
  BABY_FOOD: "이유식",
  TODDLER_FOOD: "유아식",
};

export default function RecipeDetailPage() {
  const params = useParams();
  const router = useRouter();
  const [recipe, setRecipe] = useState<RecipeDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchApi<RecipeDetailResponse>(`/api/recipes/${params.id}`)
      .then(setRecipe)
      .catch(() => router.replace("/recipes"))
      .finally(() => setLoading(false));
  }, [params.id, router]);

  return (
    <AppShell>
      <div className="px-4 pt-6">
        {loading || !recipe ? (
          <Spinner className="mt-20" />
        ) : (
          <>
            {/* Header */}
            <div className="flex items-center gap-3 mb-4">
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
              <h1 className="text-xl font-bold text-gray-900">
                {recipe.name}
              </h1>
            </div>

            {/* Tags */}
            <div className="flex flex-wrap gap-2 mb-6">
              <span className="px-2.5 py-1 text-xs bg-orange-50 text-orange-600 rounded-full">
                {STAGE_LABEL[recipe.stage]}
              </span>
              <span className="px-2.5 py-1 text-xs bg-blue-50 text-blue-600 rounded-full">
                {CATEGORY_LABEL[recipe.category]}
              </span>
              <span className="px-2.5 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
                ⏱ {recipe.cook_time}분
              </span>
              <span className="px-2.5 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
                {recipe.min_month}~{recipe.max_month}개월
              </span>
            </div>

            {/* Ingredients */}
            <section className="mb-6">
              <h2 className="text-base font-bold text-gray-900 mb-3">
                재료
              </h2>
              <div className="bg-gray-50 rounded-xl p-4">
                {recipe.ingredients.map((ing, i) => (
                  <div
                    key={i}
                    className="flex justify-between py-1.5 border-b border-gray-100 last:border-0"
                  >
                    <span className="text-sm text-gray-700">{ing.name}</span>
                    <span className="text-sm text-gray-500">
                      {ing.amount}
                      {ing.unit}
                    </span>
                  </div>
                ))}
              </div>
            </section>

            {/* Instructions */}
            <section className="mb-6">
              <h2 className="text-base font-bold text-gray-900 mb-3">
                조리법
              </h2>
              <div className="bg-gray-50 rounded-xl p-4">
                {recipe.instructions.split("\n").map((line, i) => (
                  <p key={i} className="text-sm text-gray-700 mb-2 last:mb-0 leading-relaxed">
                    {line}
                  </p>
                ))}
              </div>
            </section>

            {/* Nutrition */}
            {recipe.nutrition && (
              <section className="mb-6">
                <h2 className="text-base font-bold text-gray-900 mb-3">
                  영양소
                </h2>
                <div className="grid grid-cols-2 gap-2">
                  {[
                    { label: "칼로리", value: recipe.nutrition.calories, unit: "kcal" },
                    { label: "단백질", value: recipe.nutrition.protein, unit: "g" },
                    { label: "철분", value: recipe.nutrition.iron, unit: "mg" },
                    { label: "칼슘", value: recipe.nutrition.calcium, unit: "mg" },
                    { label: "비타민A", value: recipe.nutrition.vitamin_a, unit: "μg" },
                    { label: "비타민C", value: recipe.nutrition.vitamin_c, unit: "mg" },
                    { label: "아연", value: recipe.nutrition.zinc, unit: "mg" },
                  ].map((n) => (
                    <div
                      key={n.label}
                      className="bg-gray-50 rounded-xl p-3 text-center"
                    >
                      <div className="text-xs text-gray-400 mb-1">
                        {n.label}
                      </div>
                      <div className="text-sm font-bold text-gray-700">
                        {n.value}
                        <span className="text-xs font-normal text-gray-400 ml-0.5">
                          {n.unit}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
                {recipe.nutrition.source && (
                  <p className="text-xs text-gray-400 mt-3">
                    * 조리 전 생재료 기준 | 출처: {recipe.nutrition.source}
                  </p>
                )}
              </section>
            )}
          </>
        )}
      </div>
    </AppShell>
  );
}
