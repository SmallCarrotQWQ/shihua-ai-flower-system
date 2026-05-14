from app.core.redis_client import get_redis
from app.services.llm_service import get_llm


def chat(session_id: str, message: str) -> dict:
    redis = get_redis()
    if redis:
        redis.lpush(f"chat:{session_id}", message)
        redis.ltrim(f"chat:{session_id}", 0, 20)
        redis.expire(f"chat:{session_id}", 3600)

    llm = get_llm()
    if llm is None:
        return {
            "reply": "AI客服暂未配置 DeepSeek API Key。你可以先查看商品详情里的花语和养护指南。",
            "session_id": session_id,
            "source": "fallback-template",
        }

    result = llm.invoke(f"你是鲜花商城客服，请用100字以内回答：{message}")
    return {
        "reply": result.content,
        "session_id": session_id,
        "source": "deepseek-chat",
    }

