"use client";

import type { ChildResponse } from "@/lib/types";

interface ChildCardProps {
  child: ChildResponse;
  selected: boolean;
  onSelect: () => void;
  onDelete: () => void;
}

export default function ChildCard({
  child,
  selected,
  onSelect,
  onDelete,
}: ChildCardProps) {
  return (
    <div
      onClick={onSelect}
      className={`relative p-4 rounded-2xl border-2 cursor-pointer transition-all ${
        selected
          ? "border-brand bg-brand-50"
          : "border-gray-100 bg-white hover:border-gray-200"
      }`}
    >
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className="text-3xl">
            {child.gender === "MALE" ? "👦" : child.gender === "FEMALE" ? "👧" : "👶"}
          </div>
          <div>
            <h3 className="font-bold text-gray-900">{child.name}</h3>
            <p className="text-sm text-gray-500">{child.age_in_months}개월</p>
          </div>
        </div>
        <button
          onClick={(e) => {
            e.stopPropagation();
            onDelete();
          }}
          className="p-1 text-gray-300 hover:text-red-400 transition-colors"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      {child.allergies.length > 0 && (
        <div className="flex flex-wrap gap-1 mt-3">
          {child.allergies.map((allergy) => (
            <span
              key={allergy}
              className="px-2 py-0.5 text-xs bg-red-50 text-red-600 rounded-full"
            >
              {allergy}
            </span>
          ))}
        </div>
      )}

      {selected && (
        <div className="absolute top-3 right-10 w-5 h-5 bg-brand rounded-full flex items-center justify-center">
          <svg className="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
          </svg>
        </div>
      )}
    </div>
  );
}
