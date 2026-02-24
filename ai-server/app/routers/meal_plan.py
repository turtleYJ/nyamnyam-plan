from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from app.services.meal_generator import MealGenerator

router = APIRouter()
meal_generator = MealGenerator()


class RecipeInfo(BaseModel):
    id: int
    name: str
    category: str
    stage: str
    cook_time: int
    ingredient_names: list[str]


class MealPlanRequest(BaseModel):
    child_month: int
    allergies: list[str] = []
    available_recipes: list[RecipeInfo]
    days: int = 7


class MealSlot(BaseModel):
    recipe_id: int
    recipe_name: str


class DayPlan(BaseModel):
    day: int
    breakfast: MealSlot
    lunch: MealSlot
    dinner: MealSlot
    snack: MealSlot


class MealPlanResponse(BaseModel):
    child_month: int
    days: int
    meals: list[DayPlan]
    cached: bool = False


@router.post("/generate-meal-plan", response_model=MealPlanResponse)
async def generate_meal_plan(request: MealPlanRequest):
    if len(request.available_recipes) < 4:
        raise HTTPException(
            status_code=400,
            detail="At least 4 recipes are required to generate a meal plan",
        )

    recipes = [r.model_dump() for r in request.available_recipes]
    result = await meal_generator.generate(
        child_month=request.child_month,
        allergies=request.allergies,
        available_recipes=recipes,
        days=request.days,
    )

    day_plans = []
    for day_data in result["meals"]:
        day_plans.append(
            DayPlan(
                day=day_data["day"],
                breakfast=MealSlot(**day_data["breakfast"]),
                lunch=MealSlot(**day_data["lunch"]),
                dinner=MealSlot(**day_data["dinner"]),
                snack=MealSlot(**day_data["snack"]),
            )
        )

    return MealPlanResponse(
        child_month=request.child_month,
        days=request.days,
        meals=day_plans,
        cached=result.get("cached", False),
    )
