"use client";

import { useCallback, useEffect, useState } from "react";
import type { ShoppingItem } from "@/lib/types";

interface ShoppingListProps {
  planId: number;
  items: ShoppingItem[];
}

const CATEGORY_MAP: Record<string, string> = {
  쌀: "곡류",
  오트밀: "곡류",
  소고기: "육류",
  닭가슴살: "육류",
  연어: "육류",
  두부: "콩/유제품",
  우유: "콩/유제품",
  된장: "콩/유제품",
  단호박: "채소",
  당근: "채소",
  배추: "채소",
  시금치: "채소",
  애호박: "채소",
  양파: "채소",
  브로콜리: "채소",
  감자: "채소",
  고구마: "채소",
  무: "채소",
  미역: "채소",
  표고버섯: "채소",
  바나나: "과일",
  사과: "과일",
  달걀: "기타",
  참기름: "기타",
  카레가루: "기타",
};

const CATEGORY_ORDER = ["곡류", "육류", "채소", "과일", "콩/유제품", "기타"];
const CATEGORY_ICONS: Record<string, string> = {
  곡류: "🌾",
  육류: "🥩",
  채소: "🥬",
  과일: "🍌",
  "콩/유제품": "🫘",
  기타: "📦",
};

function getCategory(name: string): string {
  return CATEGORY_MAP[name] || "기타";
}

function getStorageKey(planId: number) {
  return `shopping-checked-${planId}`;
}

function loadChecked(planId: number): Set<string> {
  if (typeof window === "undefined") return new Set();
  try {
    const raw = localStorage.getItem(getStorageKey(planId));
    if (!raw) return new Set();
    return new Set(JSON.parse(raw));
  } catch {
    return new Set();
  }
}

function saveChecked(planId: number, checked: Set<string>) {
  localStorage.setItem(getStorageKey(planId), JSON.stringify(Array.from(checked)));
}

interface CategoryGroup {
  category: string;
  items: { item: ShoppingItem; key: string }[];
}

function groupByCategory(items: ShoppingItem[]): CategoryGroup[] {
  const groups = new Map<string, { item: ShoppingItem; key: string }[]>();

  items.forEach((item) => {
    const cat = getCategory(item.ingredient_name);
    const key = `${item.ingredient_name}-${item.unit}`;
    if (!groups.has(cat)) groups.set(cat, []);
    groups.get(cat)!.push({ item, key });
  });

  return CATEGORY_ORDER
    .filter((cat) => groups.has(cat))
    .map((cat) => ({ category: cat, items: groups.get(cat)! }));
}

export default function ShoppingList({ planId, items }: ShoppingListProps) {
  const [checked, setChecked] = useState<Set<string>>(new Set());

  useEffect(() => {
    setChecked(loadChecked(planId));
  }, [planId]);

  const toggle = useCallback(
    (key: string) => {
      setChecked((prev) => {
        const next = new Set(prev);
        if (next.has(key)) next.delete(key);
        else next.add(key);
        saveChecked(planId, next);
        return next;
      });
    },
    [planId]
  );

  const checkedCount = checked.size;
  const totalCount = items.length;
  const groups = groupByCategory(items);

  const coupangSearch = (name: string) =>
    `https://www.coupang.com/np/search?q=${encodeURIComponent(name + " 이유식")}`;

  const coupangSearchAll = () => {
    const uncheckedNames = items
      .filter((item) => !checked.has(`${item.ingredient_name}-${item.unit}`))
      .map((item) => item.ingredient_name)
      .join(" ");
    return `https://www.coupang.com/np/search?q=${encodeURIComponent(uncheckedNames)}`;
  };

  return (
    <div>
      {/* Progress */}
      <div className="mb-4">
        <div className="flex items-center justify-between mb-1">
          <span className="text-sm text-gray-600">
            {checkedCount}/{totalCount} 완료
          </span>
          <span className="text-xs text-gray-400">
            {totalCount > 0
              ? Math.round((checkedCount / totalCount) * 100)
              : 0}
            %
          </span>
        </div>
        <div className="w-full bg-gray-100 rounded-full h-2">
          <div
            className="bg-brand h-2 rounded-full transition-all duration-300"
            style={{
              width: `${totalCount > 0 ? (checkedCount / totalCount) * 100 : 0}%`,
            }}
          />
        </div>
      </div>

      {/* Categorized Items */}
      <div className="space-y-5">
        {groups.map(({ category, items: groupItems }) => (
          <div key={category}>
            <div className="flex items-center gap-1.5 mb-2">
              <span className="text-sm">{CATEGORY_ICONS[category]}</span>
              <h3 className="text-sm font-semibold text-gray-700">
                {category}
              </h3>
              <span className="text-xs text-gray-400">
                {groupItems.length}
              </span>
            </div>
            <ul className="space-y-2">
              {groupItems.map(({ item, key }) => {
                const isChecked = checked.has(key);
                return (
                  <li
                    key={key}
                    className={`flex items-center gap-3 p-3 rounded-xl border transition-colors ${
                      isChecked
                        ? "bg-gray-50 border-gray-100"
                        : "bg-white border-gray-200"
                    }`}
                  >
                    <button
                      onClick={() => toggle(key)}
                      className={`w-5 h-5 rounded-md border-2 flex-shrink-0 flex items-center justify-center transition-colors ${
                        isChecked
                          ? "bg-brand border-brand text-white"
                          : "border-gray-300"
                      }`}
                    >
                      {isChecked && (
                        <svg
                          className="w-3 h-3"
                          fill="none"
                          stroke="currentColor"
                          viewBox="0 0 24 24"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={3}
                            d="M5 13l4 4L19 7"
                          />
                        </svg>
                      )}
                    </button>

                    <div className="flex-1 min-w-0">
                      <div className="flex items-baseline gap-2">
                        <span
                          className={`font-medium ${
                            isChecked
                              ? "line-through text-gray-400"
                              : "text-gray-900"
                          }`}
                        >
                          {item.ingredient_name}
                        </span>
                        <span
                          className={`text-sm ${isChecked ? "text-gray-300" : "text-gray-500"}`}
                        >
                          {item.total_amount}
                          {item.unit}
                        </span>
                      </div>
                      <p className="text-xs text-gray-400 mt-0.5 truncate">
                        {item.recipe_names.join(", ")}
                      </p>
                    </div>

                    <a
                      href={coupangSearch(item.ingredient_name)}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="flex-shrink-0 px-2.5 py-1 text-xs font-medium text-red-600 bg-red-50 rounded-lg hover:bg-red-100 transition-colors"
                    >
                      쿠팡
                    </a>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
      </div>

      {/* Search All */}
      {items.length > 0 && (
        <a
          href={coupangSearchAll()}
          target="_blank"
          rel="noopener noreferrer"
          className="block w-full mt-6 py-3 text-center text-sm font-medium text-white bg-red-500 rounded-xl hover:bg-red-600 transition-colors"
        >
          쿠팡에서 한번에 검색
        </a>
      )}
    </div>
  );
}
