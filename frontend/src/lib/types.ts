// Auth
export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
}

// User
export interface UserResponse {
  id: number;
  email: string;
  nickname: string;
  provider: "KAKAO" | "NAVER" | "DEV";
}

// Child
export interface ChildResponse {
  id: number;
  name: string;
  birth_date: string;
  gender: "MALE" | "FEMALE" | null;
  allergies: string[];
  age_in_months: number;
  created_at: string;
}

export interface ChildCreateRequest {
  name: string;
  birth_date: string;
  gender?: "MALE" | "FEMALE" | null;
  allergies?: string[];
}

export interface ChildUpdateRequest {
  name?: string;
  allergies?: string[];
}

// Recipe
export type RecipeCategory = "BABY_FOOD" | "TODDLER_FOOD";
export type RecipeStage = "EARLY" | "MIDDLE" | "LATE" | "COMPLETE";

export interface RecipeResponse {
  id: number;
  name: string;
  min_month: number;
  max_month: number;
  cook_time: number;
  category: RecipeCategory;
  stage: RecipeStage;
  ingredient_names: string[];
}

export interface RecipeDetailResponse {
  id: number;
  name: string;
  min_month: number;
  max_month: number;
  cook_time: number;
  instructions: string;
  category: RecipeCategory;
  stage: RecipeStage;
  ingredients: IngredientInfo[];
  nutrition: NutritionInfo | null;
}

export interface IngredientInfo {
  name: string;
  amount: number;
  unit: string;
}

export interface NutritionInfo {
  calories: number;
  protein: number;
  iron: number;
  calcium: number;
  vitamin_a: number;
  vitamin_c: number;
  zinc: number;
  source: string | null;
}

// Plan
export type MealTime = "BREAKFAST" | "LUNCH" | "DINNER" | "SNACK";
export type PlanCreator = "USER" | "AI";

export interface MealResponse {
  id: number;
  date: string;
  meal_time: MealTime;
  recipe_id: number;
  recipe_name: string;
}

export interface WeeklyPlanResponse {
  id: number;
  child_id: number;
  child_name: string;
  week_start_date: string;
  created_by: PlanCreator;
  meals: MealResponse[];
  created_at: string;
}

export interface GeneratePlanRequest {
  child_id: number;
  week_start_date: string;
}

// Shopping List
export interface ShoppingListResponse {
  plan_id: number;
  week_start_date: string;
  items: ShoppingItem[];
  total_items: number;
}

export interface ShoppingItem {
  ingredient_name: string;
  total_amount: number;
  unit: string;
  recipe_names: string[];
}
