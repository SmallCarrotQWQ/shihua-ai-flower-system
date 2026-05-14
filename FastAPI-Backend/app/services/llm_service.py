from langchain_openai import ChatOpenAI

from app.config import settings


def get_llm() -> ChatOpenAI | None:
    if not settings.deepseek_api_key:
        return None

    return ChatOpenAI(
        model=settings.deepseek_model,
        api_key=settings.deepseek_api_key,
        base_url=settings.deepseek_base_url,
        temperature=0.7,
        max_tokens=512,
        timeout=10,
    )

