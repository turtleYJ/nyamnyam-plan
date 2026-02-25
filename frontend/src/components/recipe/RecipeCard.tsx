"use client";

import Link from "next/link";
import type { RecipeResponse } from "@/lib/types";

interface RecipeCardProps {
  recipe: RecipeResponse;
}

const STAGE_LABEL: Record<string, string> = {
  EARLY: "초기",
  MIDDLE: "중기",
  LATE: "후기",
  COMPLETE: "완료기",
};

export default function RecipeCard({ recipe }: RecipeCardProps) {
  return (
    <Link href={`/recipes/${recipe.id}`}>
      <div className="p-4 bg-white rounded-2xl border border-gray-100 hover:border-brand/30 transition-colors">
        <div className="flex items-start justify-between mb-2">
          <h3 className="font-bold text-gray-900 leading-tight">
            {recipe.name}
          </h3>
          <span className="flex-shrink-0 ml-2 px-2 py-0.5 text-xs bg-orange-50 text-orange-600 rounded-full">
            {STAGE_LABEL[recipe.stage] || recipe.stage}
          </span>
        </div>

        <div className="flex items-center gap-3 text-xs text-gray-500 mb-2">
          <span>⏱ {recipe.cook_time}분</span>
          <span>
            {recipe.min_month}~{recipe.max_month}개월
          </span>
        </div>

        {recipe.ingredient_names.length > 0 && (
          <p className="text-xs text-gray-400 truncate">
            {recipe.ingredient_names.slice(0, 5).join(", ")}
            {recipe.ingredient_names.length > 5 && " ..."}
          </p>
        )}
      </div>
    </Link>
  );
}
