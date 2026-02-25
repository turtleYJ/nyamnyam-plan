"use client";

import type { RecipeCategory, RecipeStage } from "@/lib/types";

interface Filters {
  ageMonth?: number;
  category?: RecipeCategory;
  stage?: RecipeStage;
}

interface RecipeFilterProps {
  filters: Filters;
  onChange: (filters: Filters) => void;
}

const STAGES: { value: RecipeStage; label: string }[] = [
  { value: "EARLY", label: "초기" },
  { value: "MIDDLE", label: "중기" },
  { value: "LATE", label: "후기" },
  { value: "COMPLETE", label: "완료기" },
];

const CATEGORIES: { value: RecipeCategory; label: string }[] = [
  { value: "BABY_FOOD", label: "이유식" },
  { value: "TODDLER_FOOD", label: "유아식" },
];

export default function RecipeFilter({ filters, onChange }: RecipeFilterProps) {
  const toggleStage = (stage: RecipeStage) => {
    onChange({
      ...filters,
      stage: filters.stage === stage ? undefined : stage,
    });
  };

  const toggleCategory = (category: RecipeCategory) => {
    onChange({
      ...filters,
      category: filters.category === category ? undefined : category,
    });
  };

  return (
    <div className="flex flex-col gap-3">
      {/* Stage */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {STAGES.map((s) => (
          <button
            key={s.value}
            onClick={() => toggleStage(s.value)}
            className={`flex-shrink-0 px-3 py-1.5 rounded-full text-sm border transition-colors ${
              filters.stage === s.value
                ? "border-brand bg-brand-50 text-brand-600 font-medium"
                : "border-gray-200 text-gray-600 hover:bg-gray-50"
            }`}
          >
            {s.label}
          </button>
        ))}
      </div>

      {/* Category */}
      <div className="flex gap-2">
        {CATEGORIES.map((c) => (
          <button
            key={c.value}
            onClick={() => toggleCategory(c.value)}
            className={`flex-shrink-0 px-3 py-1.5 rounded-full text-sm border transition-colors ${
              filters.category === c.value
                ? "border-brand bg-brand-50 text-brand-600 font-medium"
                : "border-gray-200 text-gray-600 hover:bg-gray-50"
            }`}
          >
            {c.label}
          </button>
        ))}
      </div>
    </div>
  );
}
