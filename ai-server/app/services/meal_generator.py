from app.config import settings


class MealGenerator:
    """Claude API를 이용한 식단 생성 서비스 (4단계에서 구현 예정)"""

    async def generate(self, child_month: int, allergies: list[str], days: int) -> list[dict]:
        # TODO: Implement Claude API call
        # 1. Redis 캐시 확인
        # 2. 캐시 미스 시 Claude API 호출
        # 3. 결과 캐싱 후 반환
        raise NotImplementedError("AI generation will be implemented in Phase 4")
