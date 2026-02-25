from __future__ import annotations

import hashlib
import json
import logging
from typing import Optional

import redis.asyncio as redis
from anthropic import AsyncAnthropic

from app.config import settings

logger = logging.getLogger(__name__)


class MealGenerator:
    def __init__(self):
        self._client: AsyncAnthropic | None = None
        self._redis: redis.Redis | None = None

    def _get_client(self) -> AsyncAnthropic:
        if self._client is None:
            self._client = AsyncAnthropic(api_key=settings.claude_api_key)
        return self._client

    def _get_redis(self) -> redis.Redis:
        if self._redis is None:
            self._redis = redis.Redis(
                host=settings.redis_host,
                port=settings.redis_port,
                decode_responses=True,
            )
        return self._redis

    async def generate(
        self,
        child_month: int,
        allergies: list[str],
        available_recipes: list[dict],
        days: int = 7,
    ) -> dict:
        cache_key = self._build_cache_key(child_month, allergies, available_recipes)

        cached = await self._get_cache(cache_key)
        if cached is not None:
            cached["cached"] = True
            return cached

        meals = await self._call_claude(child_month, allergies, available_recipes, days)

        result = {"meals": meals, "cached": False}
        await self._set_cache(cache_key, result)
        return result

    def _build_cache_key(
        self, child_month: int, allergies: list[str], recipes: list[dict]
    ) -> str:
        recipe_ids = sorted([r["id"] for r in recipes])
        raw = f"{child_month}:{','.join(sorted(allergies))}:{','.join(map(str, recipe_ids))}"
        digest = hashlib.sha256(raw.encode()).hexdigest()[:16]
        return f"meal_plan:{digest}"

    async def _get_cache(self, key: str) -> dict | None:
        try:
            r = self._get_redis()
            data = await r.get(key)
            if data:
                return json.loads(data)
        except Exception as e:
            logger.warning(f"Redis cache read failed: {e}")
        return None

    async def _set_cache(self, key: str, value: dict) -> None:
        try:
            r = self._get_redis()
            await r.set(key, json.dumps(value, ensure_ascii=False), ex=settings.cache_ttl_seconds)
        except Exception as e:
            logger.warning(f"Redis cache write failed: {e}")

    async def _call_claude(
        self,
        child_month: int,
        allergies: list[str],
        recipes: list[dict],
        days: int,
    ) -> list[dict]:
        recipe_lines = []
        for r in recipes:
            ingredients = ", ".join(r.get("ingredient_names", []))
            recipe_lines.append(
                f"ID:{r['id']}|{r['name']}|{r['category']}|재료:{ingredients}|조리시간:{r['cook_time']}분"
            )
        recipe_text = "\n".join(recipe_lines)

        allergy_text = ", ".join(allergies) if allergies else "없음"

        prompt = f"""당신은 영유아 이유식 전문 영양사입니다.
아래 조건에 맞는 {days}일 식단을 JSON으로 작성하세요.

[아이 정보]
- 월령: {child_month}개월
- 알레르기: {allergy_text}

[사용 가능한 레시피 목록]
{recipe_text}

[규칙]
1. 반드시 위 레시피 목록의 ID만 사용하세요.
2. 같은 날에 같은 레시피를 2번 이상 사용하지 마세요.
3. 연속된 끼니(예: Day1 저녁 → Day2 아침)에 같은 레시피를 피하세요.
4. 간식(snack)은 조리시간이 짧은 레시피를 우선 배정하세요.
5. 다양한 카테고리(죽, 퓌레, 무른밥 등)가 골고루 포함되도록 하세요.

[출력 형식]
JSON 배열만 출력하세요. 다른 텍스트 없이 순수 JSON만 응답하세요.
```json
[
  {{"day": 1, "breakfast": {{"recipe_id": ID, "recipe_name": "이름"}}, "lunch": {{"recipe_id": ID, "recipe_name": "이름"}}, "dinner": {{"recipe_id": ID, "recipe_name": "이름"}}, "snack": {{"recipe_id": ID, "recipe_name": "이름"}}}},
  ...
]
```"""

        try:
            client = self._get_client()
            response = await client.messages.create(
                model=settings.claude_model,
                max_tokens=2048,
                messages=[{"role": "user", "content": prompt}],
            )

            text = response.content[0].text.strip()
            # Extract JSON from possible markdown code block
            if "```json" in text:
                text = text.split("```json")[1].split("```")[0].strip()
            elif "```" in text:
                text = text.split("```")[1].split("```")[0].strip()

            meals = json.loads(text)
            if self._validate_meals(meals, recipes, days):
                return meals

            logger.warning("Claude response validation failed, using round-robin fallback")
            return self._round_robin_fallback(recipes, days)

        except Exception as e:
            logger.error(f"Claude API call failed: {e}")
            return self._round_robin_fallback(recipes, days)

    def _validate_meals(self, meals: list[dict], recipes: list[dict], days: int) -> bool:
        if len(meals) != days:
            return False

        valid_ids = {r["id"] for r in recipes}
        recipe_names = {r["id"]: r["name"] for r in recipes}

        for day in meals:
            for slot in ["breakfast", "lunch", "dinner", "snack"]:
                if slot not in day:
                    return False
                rid = day[slot].get("recipe_id")
                if rid not in valid_ids:
                    return False
        return True

    def _round_robin_fallback(self, recipes: list[dict], days: int) -> list[dict]:
        sorted_recipes = sorted(recipes, key=lambda r: r.get("cook_time", 999))
        snack_pool = sorted_recipes[:max(1, len(sorted_recipes) // 3)]
        main_pool = recipes

        meals = []
        main_idx = 0
        snack_idx = 0

        for day_num in range(1, days + 1):
            day: dict = {"day": day_num}
            for slot in ["breakfast", "lunch", "dinner"]:
                r = main_pool[main_idx % len(main_pool)]
                day[slot] = {"recipe_id": r["id"], "recipe_name": r["name"]}
                main_idx += 1

            s = snack_pool[snack_idx % len(snack_pool)]
            day["snack"] = {"recipe_id": s["id"], "recipe_name": s["name"]}
            snack_idx += 1

            meals.append(day)

        return meals
