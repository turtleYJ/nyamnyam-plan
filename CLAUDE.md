# nyamnyam-plan 세션 기록

## 현재 상태 (2026-02-23)
- **완료**: 1~3단계
- **다음**: 4단계 (주간 식단 API) 또는 프론트엔드 먼저 붙이기

## 완료된 단계

### 1단계: 프로젝트 기반 세팅 (51ea707)
- Spring Boot 3.2 + Kotlin 1.9 프로젝트 구조
- Docker Compose (MySQL, Redis)
- Entity: User, Child, Recipe, Ingredient, RecipeIngredient, RecipeNutrition, WeeklyPlan, DailyMeal

### 2단계: 인증 + 아이 프로필 CRUD (175ce0f)
- JWT 인증 + OAuth 2.0 (Kakao, Naver)
- Child CRUD API (최대 5명, 알레르기 JSON 저장)
- SecurityConfig, GlobalExceptionHandler

### 3단계: 레시피 DB + 룰 기반 필터링 (5e6d185)
- `GET /api/recipes` — 월령/카테고리/스테이지/알레르기 필터링
- `GET /api/recipes/{id}` — 상세 (재료 + 영양소)
- DataSeeder: 25개 이유식 레시피 JSON 시드 로더
- 9개 파일, 724줄 추가

## 남은 단계
- **4단계**: 주간 식단 API (WeeklyPlan/DailyMeal CRUD + 레시피 배정)
- **5단계**: AI 식단 추천 (ai-server 연동)
- **6단계**: 프론트엔드 모바일 앱

## 프로젝트 구조 요약
```
backend/src/main/kotlin/com/nyamnyam/
├── auth/          # JWT + OAuth (Kakao, Naver)
├── common/        # ErrorCode, BusinessException, @CurrentUserId
├── config/        # Security, Redis, WebClient, JWT/OAuth properties
├── domain/
│   ├── child/     # Child CRUD (controller, service, repository, dto, entity)
│   ├── recipe/    # Recipe 조회/필터링 (controller, service, repository, dto, entity)
│   ├── plan/      # WeeklyPlan, DailyMeal (entity only — 아직 미구현)
│   └── user/      # User (service, repository, dto, entity)
└── infrastructure/ # HealthController, DataSeeder
```

## API 엔드포인트
| Method | Path | 인증 | 상태 |
|--------|------|------|------|
| POST | /api/auth/login/kakao | X | ✅ |
| POST | /api/auth/login/naver | X | ✅ |
| POST | /api/auth/refresh | X | ✅ |
| POST | /api/auth/logout | O | ✅ |
| GET | /api/users/me | O | ✅ |
| GET/POST/PUT/DELETE | /api/children | O | ✅ |
| GET | /api/recipes | O | ✅ |
| GET | /api/recipes/{id} | O | ✅ |
| GET | /api/health | X | ✅ |

## 주요 패턴
- **DTO**: companion `from()` 팩토리
- **Service**: `@Transactional(readOnly = true)` 클래스 레벨, 쓰기 메서드만 `@Transactional`
- **Controller**: `@CurrentUserId` 커스텀 어노테이션으로 인증 유저 주입
- **Error**: `BusinessException(ErrorCode.XXX)` → `GlobalExceptionHandler`
- **JSON 응답**: snake_case (Jackson 글로벌 설정)
