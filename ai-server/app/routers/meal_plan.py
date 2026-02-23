from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter()


class MealPlanRequest(BaseModel):
    child_month: int
    allergies: list[str] = []
    days: int = 7


class MealPlanResponse(BaseModel):
    child_month: int
    days: int
    meals: list[dict]
    cached: bool = False


@router.post("/generate-meal-plan", response_model=MealPlanResponse)
async def generate_meal_plan(request: MealPlanRequest):
    # TODO: Implement actual AI generation with Claude API
    # For now, return a stub response
    stub_meals = [
        {
            "day": day + 1,
            "breakfast": {"name": f"아침 이유식 (Day {day + 1})", "recipe_id": None},
            "lunch": {"name": f"점심 이유식 (Day {day + 1})", "recipe_id": None},
            "dinner": {"name": f"저녁 이유식 (Day {day + 1})", "recipe_id": None},
            "snack": {"name": f"간식 (Day {day + 1})", "recipe_id": None},
        }
        for day in range(request.days)
    ]

    return MealPlanResponse(
        child_month=request.child_month,
        days=request.days,
        meals=stub_meals,
        cached=False,
    )
