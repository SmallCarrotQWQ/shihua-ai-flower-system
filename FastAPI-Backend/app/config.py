from pathlib import Path

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    deepseek_api_key: str = ""
    deepseek_base_url: str = "https://api.deepseek.com/v1"
    deepseek_model: str = "deepseek-chat"
    model_path: str = "./runtime/models/flower_model.h5"
    class_names_path: str = "./runtime/models/class_names.json"
    redis_url: str = "redis://localhost:6379/0"
    chroma_path: str = "./runtime/chroma"

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    @property
    def model_path_obj(self) -> Path:
        return Path(self.model_path)

    @property
    def class_names_path_obj(self) -> Path:
        return Path(self.class_names_path)

    @property
    def chroma_path_obj(self) -> Path:
        return Path(self.chroma_path)


settings = Settings()

