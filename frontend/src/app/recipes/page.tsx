"use client";

import { useEffect, useState, useCallback } from "react";
import { fetchApi } from "@/lib/api";
import type { RecipeResponse, RecipeCategory, RecipeStage } from "@/lib/types";
import AppShell from "@/components/layout/AppShell";
import RecipeCard from "@/components/recipe/RecipeCard";
import RecipeFilter from "@/components/recipe/RecipeFilter";
import Spinner from "@/components/ui/Spinner";
import EmptyState from "@/components/ui/EmptyState";

interface Filters {
  ageMonth?: number;
  category?: RecipeCategory;
  stage?: RecipeStage;
}

export default function RecipesPage() {
  const [recipes, setRecipes] = useState<RecipeResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<Filters>({});

  const loadRecipes = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.ageMonth) params.set("ageMonth", String(filters.ageMonth));
      if (filters.category) params.set("category", filters.category);
      if (filters.stage) params.set("stage", filters.stage);

      const qs = params.toString();
      const data = await fetchApi<RecipeResponse[]>(
        `/api/recipes${qs ? `?${qs}` : ""}`
      );
      setRecipes(data);
    } catch {
      // handled by fetchApi
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    loadRecipes();
  }, [loadRecipes]);

  return (
    <AppShell>
      <div className="px-4 pt-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-4">레시피</h1>

        <div className="mb-4">
          <RecipeFilter filters={filters} onChange={setFilters} />
        </div>

        {loading ? (
          <Spinner className="mt-10" />
        ) : recipes.length === 0 ? (
          <EmptyState
            icon="🍳"
            title="레시피가 없어요"
            description="필터 조건을 변경해보세요"
          />
        ) : (
          <div className="flex flex-col gap-3">
            {recipes.map((recipe) => (
              <RecipeCard key={recipe.id} recipe={recipe} />
            ))}
          </div>
        )}
      </div>
    </AppShell>
  );
}
