# nyamnyam-plan 세션 기록

## 현재 상태 (2026-02-25)
- **완료**: 1~6단계 (MVP 전체)

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

### 4단계: 주간 식단 CRUD API
- WeeklyPlan/DailyMeal CRUD (생성/목록/상세/수정/삭제)
- 소유권 검증 (JWT userId → Child → WeeklyPlan)
- fetch join으로 meals + recipe 함께 로드
- 6개 파일 신규, 1개 수정 (ErrorCode)

### 5단계: AI 식단 추천 (348f117)
- `POST /api/plans/generate` — Claude API로 7일 식단 자동 생성
- AI 서버: AsyncAnthropic + Redis 24h 캐싱 + round-robin fallback
- 백엔드: AiMealPlanClient (WebClient) → AI 서버 호출
- 월령/알레르기 기반 레시피 필터링 → AI 서버에 전달
- 14개 파일 변경, 463줄 추가

### 6단계: PWA 프론트엔드 MVP (caeeaff)
- 백엔드: `POST /api/auth/dev-login` (local 프로필 전용, find-or-create)
- 프론트 6개 화면: 랜딩/아이관리/식단목록/식단상세/레시피목록/레시피상세
- `fetchApi<T>()` — Bearer 자동 주입 + 401 토큰 갱신 + 로그아웃
- Zustand persist: authStore (JWT), childStore (selectedChildId)
- UI 컴포넌트: Button, Input, Modal, Spinner, EmptyState, AppShell, BottomNav
- WeeklyGrid: 7일×4끼니 그리드 + 레시피 링크
- AI 생성 UI: 30초 대응 진행 메시지 + 로딩 상태
- 32개 파일 변경, 1,901줄 추가

## 프로젝트 구조 요약
```
backend/src/main/kotlin/com/nyamnyam/
├── auth/          # JWT + OAuth (Kakao, Naver) + DevLogin
├── common/        # ErrorCode, BusinessException, @CurrentUserId
├── config/        # Security, Redis, WebClient, JWT/OAuth properties
├── domain/
│   ├── child/     # Child CRUD (controller, service, repository, dto, entity)
│   ├── recipe/    # Recipe 조회/필터링 (controller, service, repository, dto, entity)
│   ├── plan/      # WeeklyPlan/DailyMeal CRUD (controller, service, repository, dto, entity)
│   └── user/      # User (service, repository, dto, entity)
└── infrastructure/ # HealthController, DataSeeder, AiMealPlanClient, AiMealPlanDto

frontend/src/
├── lib/           # types.ts (API 타입), api.ts (fetchApi + 토큰 갱신)
├── stores/        # authStore (JWT persist), childStore (선택 아이)
├── components/
│   ├── ui/        # Button, Input, Modal, Spinner, EmptyState
│   ├── layout/    # AppShell (인증 가드), BottomNav (3탭)
│   ├── child/     # ChildCard, ChildForm
│   ├── plan/      # GenerateButton, PlanCard, WeeklyGrid
│   └── recipe/    # RecipeCard, RecipeFilter
└── app/
    ├── page.tsx                          # 랜딩 (dev-login + OAuth)
    ├── auth/callback/[provider]/page.tsx # OAuth 콜백
    ├── children/page.tsx                 # 아이 관리
    ├── plans/page.tsx                    # 식단 목록 + AI 생성
    ├── plans/[id]/page.tsx               # 주간 식단 상세
    ├── recipes/page.tsx                  # 레시피 목록 (필터링)
    └── recipes/[id]/page.tsx             # 레시피 상세
```

## API 엔드포인트
| Method | Path | 인증 | 상태 |
|--------|------|------|------|
| POST | /api/auth/dev-login | X | ✅ (local only) |
| POST | /api/auth/login/kakao | X | ✅ |
| POST | /api/auth/login/naver | X | ✅ |
| POST | /api/auth/refresh | X | ✅ |
| POST | /api/auth/logout | O | ✅ |
| GET | /api/users/me | O | ✅ |
| GET/POST/PUT/DELETE | /api/children | O | ✅ |
| GET | /api/recipes | O | ✅ |
| GET | /api/recipes/{id} | O | ✅ |
| POST | /api/plans | O | ✅ |
| GET | /api/plans?childId={id} | O | ✅ |
| GET | /api/plans/{id} | O | ✅ |
| PUT | /api/plans/{id} | O | ✅ |
| DELETE | /api/plans/{id} | O | ✅ |
| POST | /api/plans/generate | O | ✅ |
| GET | /api/health | X | ✅ |

## E2E 테스트 기록 (2026-02-25)

### 테스트 환경
- 백엔드: Spring Boot (localhost:8080, local 프로필)
- AI 서버: FastAPI + uvicorn (localhost:8000)
- 프론트엔드: Next.js dev (localhost:3000)
- DB: 로컬 MariaDB 11.0.2 (Docker 대신 사용)
- Redis: Docker (localhost:6379)
- 테스트 도구: Playwright MCP

### 테스트 시나리오 (전체 통과)
| # | 화면 | 테스트 내용 | 결과 |
|---|------|------------|------|
| 1 | 랜딩 `/` | dev-login (test@nyamnyam.com) → /children 리다이렉트 | ✅ |
| 2 | 아이 관리 `/children` | 하은(8개월, 여아, 계란/우유 알레르기) 등록 | ✅ |
| 3 | 식단 관리 `/plans` | 아이 선택 + "AI 식단 생성" 클릭 | ✅ |
| 4 | 식단 상세 `/plans/1` | 7일×4끼니 WeeklyGrid 표시, 레시피 링크 동작 | ✅ |
| 5 | 레시피 상세 `/recipes/7` | 소고기죽 — 재료/조리법/영양소 표시 | ✅ |
| 6 | 레시피 목록 `/recipes` | 25개 레시피 목록 + 단계/카테고리 필터 | ✅ |

### 발견 및 수정한 버그 (74103de)
| 버그 | 원인 | 수정 |
|------|------|------|
| 시드 데이터 min_month=0 | Jackson SNAKE_CASE가 camelCase JSON 필드 무시 | DataSeeder에 `@JsonProperty` 추가 |
| AI 서버 503 | 백엔드 WebClient가 camelCase로 전송 | WebClientConfig에 ObjectMapper 주입 |
| MariaDB 연결 실패 | MySQL 드라이버가 `transaction_isolation` 전송 | MariaDB JDBC 드라이버로 변경 |
| Python 3.9 타입 에러 | `dict \| None` 문법 3.10+ 전용 | `from __future__ import annotations` 추가 |

## 주요 패턴
- **DTO**: companion `from()` 팩토리
- **Service**: `@Transactional(readOnly = true)` 클래스 레벨, 쓰기 메서드만 `@Transactional`
- **Controller**: `@CurrentUserId` 커스텀 어노테이션으로 인증 유저 주입
- **Error**: `BusinessException(ErrorCode.XXX)` → `GlobalExceptionHandler`
- **JSON 응답**: snake_case (Jackson 글로벌 설정)
- **프론트 API**: `fetchApi<T>()` + 401 자동 토큰 갱신
- **상태 관리**: Zustand persist (authStore, childStore)
- **인증 가드**: AppShell (hydration 대응 + 미인증 리다이렉트)
