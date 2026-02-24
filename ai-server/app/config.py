from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    claude_api_key: str = ""
    claude_model: str = "claude-sonnet-4-20250514"
    redis_host: str = "localhost"
    redis_port: int = 6379
    cache_ttl_seconds: int = 86400

    class Config:
        env_file = ".env"


settings = Settings()
