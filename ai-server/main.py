from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import meal_plan

app = FastAPI(
    title="NyamNyam AI Server",
    description="AI-powered meal plan generation for babies and toddlers",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(meal_plan.router, prefix="/api")


@app.get("/health")
async def health():
    return {"status": "UP", "service": "nyamnyam-ai"}
