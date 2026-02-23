from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    claude_api_key: str = ""
    redis_host: str = "localhost"
    redis_port: int = 6379

    class Config:
        env_file = ".env"


settings = Settings()
