from __future__ import annotations

import json
from collections.abc import Iterator

from langchain_core.messages import HumanMessage, SystemMessage
from redis import Redis

from app.core.redis_client import get_redis
from app.services.llm_service import get_llm


FLOWER_KNOWLEDGE = [
    {
        "keywords": ["玫瑰", "rose", "爱情", "表白", "纪念日"],
        "text": "玫瑰常用于表白、纪念日和婚礼场景。红玫瑰表达热烈的爱，粉玫瑰表达温柔和感谢。",
    },
    {
        "keywords": ["百合", "lily", "祝福", "婚礼", "探望"],
        "text": "百合寓意纯洁、顺利和美好祝福，适合婚礼、乔迁和正式探望，但送病房前需确认对方是否对花香敏感。",
    },
    {
        "keywords": ["康乃馨", "carnation", "母亲", "妈妈", "长辈"],
        "text": "康乃馨寓意感恩、关怀和亲情，适合母亲节、长辈生日和日常问候。",
    },
    {
        "keywords": ["向日葵", "sunflower", "朋友", "鼓励", "毕业"],
        "text": "向日葵寓意阳光、希望和坚定，适合毕业、开业、朋友鼓励和恢复元气的场景。",
    },
    {
        "keywords": ["养护", "保鲜", "花期", "换水", "修剪"],
        "text": "鲜切花建议斜剪花茎，去掉水线以下叶片，使用干净花瓶，每日换水并避开暴晒和空调直吹。",
    },
]

DEFAULT_FALLBACK_REPLY = (
    "我可以帮你推荐送花场景、解释花语和养护方法。"
    "例如：送妈妈可选康乃馨，表白可选红玫瑰，鼓励朋友可选向日葵。"
)


def chat(session_id: str, message: str) -> dict:
    clean_message = message.strip()
    if not clean_message:
        return {
            "reply": "请告诉我你想送花的对象、场景或预算，我来帮你推荐。",
            "session_id": session_id,
            "source": "validation",
            "context_used": [],
            "suggestions": _suggestions(clean_message, ""),
        }

    redis = get_redis()
    history = _load_history(redis, session_id)
    context = _match_flower_context(clean_message)
    _append_history(redis, session_id, "user", clean_message)

    llm = get_llm()
    if llm is None:
        reply = _fallback_reply(clean_message, context)
        _append_history(redis, session_id, "assistant", reply)
        return {
            "reply": reply,
            "session_id": session_id,
            "source": "fallback-template",
            "context_used": context,
            "suggestions": _suggestions(clean_message, reply),
        }

    try:
        result = llm.invoke(
            [
                SystemMessage(content=_build_system_prompt(context, history)),
                HumanMessage(content=clean_message),
            ]
        )
        reply = str(result.content).strip() or _fallback_reply(clean_message, context)
        _append_history(redis, session_id, "assistant", reply)
        return {
            "reply": reply,
            "session_id": session_id,
            "source": "deepseek-chat",
            "context_used": context,
            "suggestions": _suggestions(clean_message, reply),
        }
    except Exception:
        reply = _fallback_reply(clean_message, context)
        _append_history(redis, session_id, "assistant", reply)
        return {
            "reply": reply,
            "session_id": session_id,
            "source": "fallback-on-error",
            "context_used": context,
            "suggestions": _suggestions(clean_message, reply),
        }


def chat_stream(session_id: str, message: str) -> Iterator[dict]:
    clean_message = message.strip()
    if not clean_message:
        reply = "请告诉我你想送花的对象、场景或预算，我来帮你推荐。"
        yield {"event": "delta", "content": reply}
        yield {
            "event": "done",
            "reply": reply,
            "session_id": session_id,
            "source": "validation",
            "suggestions": _suggestions(clean_message, reply),
        }
        return

    redis = get_redis()
    history = _load_history(redis, session_id)
    context = _match_flower_context(clean_message)
    _append_history(redis, session_id, "user", clean_message)

    llm = get_llm()
    if llm is None:
        reply = _fallback_reply(clean_message, context)
        yield from _stream_text(reply)
        _append_history(redis, session_id, "assistant", reply)
        yield {
            "event": "done",
            "reply": reply,
            "session_id": session_id,
            "source": "fallback-template",
            "context_used": context,
            "suggestions": _suggestions(clean_message, reply),
        }
        return

    reply_parts: list[str] = []
    try:
        messages = [
            SystemMessage(content=_build_system_prompt(context, history)),
            HumanMessage(content=clean_message),
        ]
        for chunk in llm.stream(messages):
            content = str(chunk.content or "")
            if not content:
                continue
            reply_parts.append(content)
            yield {"event": "delta", "content": content}

        reply = "".join(reply_parts).strip() or _fallback_reply(clean_message, context)
        if not reply_parts:
            yield from _stream_text(reply)
        _append_history(redis, session_id, "assistant", reply)
        yield {
            "event": "done",
            "reply": reply,
            "session_id": session_id,
            "source": "deepseek-chat-stream",
            "context_used": context,
            "suggestions": _suggestions(clean_message, reply),
        }
    except Exception:
        reply = _fallback_reply(clean_message, context)
        yield from _stream_text(reply)
        _append_history(redis, session_id, "assistant", reply)
        yield {
            "event": "done",
            "reply": reply,
            "session_id": session_id,
            "source": "fallback-on-error",
            "context_used": context,
            "suggestions": _suggestions(clean_message, reply),
        }


def _match_flower_context(message: str) -> list[str]:
    lowered_message = message.lower()
    matched: list[str] = []
    for item in FLOWER_KNOWLEDGE:
        if any(keyword.lower() in lowered_message for keyword in item["keywords"]):
            matched.append(item["text"])
    return matched[:3]


def _build_system_prompt(context: list[str], history: list[dict[str, str]]) -> str:
    context_text = "\n".join(f"- {item}" for item in context) or "- 当前没有命中特定知识，请基于通用鲜花礼仪回答。"
    history_text = "\n".join(f"{item['role']}: {item['content']}" for item in history[-6:]) or "无"
    return (
        "你是拾花 AI 鲜花售卖系统的智能花语客服。"
        "请使用中文回答，语气自然、简洁、像真实客服。"
        "你只回答鲜花推荐、花语、送花礼仪、鲜花养护、订单购买引导相关问题。"
        "答案控制在120字以内；如果用户问题缺少场景、对象或预算，请主动追问一个关键信息。"
        "不要编造库存、价格、物流状态；涉及具体商品时，引导用户查看鲜花列表或商品详情。\n"
        f"可参考知识：\n{context_text}\n"
        f"最近会话：\n{history_text}"
    )


def _fallback_reply(message: str, context: list[str]) -> str:
    if context:
        return context[0] + " 你也可以告诉我送花对象、场景和预算，我再帮你缩小选择。"
    if any(keyword in message for keyword in ["推荐", "送", "买", "选择"]):
        return "可以的。请告诉我送花对象、使用场景和预算，例如送妈妈、表白或生日，我会帮你推荐合适花束。"
    return DEFAULT_FALLBACK_REPLY


def _stream_text(text: str) -> Iterator[dict]:
    for char in text:
        yield {"event": "delta", "content": char}


def _suggestions(message: str, reply: str) -> list[str]:
    combined = f"{message}{reply}"
    if any(keyword in combined for keyword in ["妈妈", "母亲", "长辈", "康乃馨"]):
        return ["预算200以内怎么选？", "康乃馨怎么养护？"]
    if any(keyword in combined for keyword in ["玫瑰", "表白", "爱情", "纪念日"]):
        return ["红玫瑰和粉玫瑰怎么选？", "表白适合送几朵？"]
    if any(keyword in combined for keyword in ["百合", "婚礼", "乔迁", "祝福"]):
        return ["百合适合送什么场景？", "百合花香太浓怎么办？"]
    if any(keyword in combined for keyword in ["养护", "保鲜", "换水", "花期"]):
        return ["鲜花怎样延长花期？", "收到花后第一步做什么？"]
    return ["按预算帮我推荐", "不同花分别代表什么？"]


def _load_history(redis: Redis | None, session_id: str) -> list[dict[str, str]]:
    if redis is None:
        return []
    raw_items = redis.lrange(f"chat:{session_id}", 0, 11)
    history: list[dict[str, str]] = []
    for raw_item in reversed(raw_items):
        try:
            item = json.loads(raw_item)
            if isinstance(item, dict) and "role" in item and "content" in item:
                history.append({"role": str(item["role"]), "content": str(item["content"])})
        except json.JSONDecodeError:
            continue
    return history


def _append_history(redis: Redis | None, session_id: str, role: str, content: str) -> None:
    if redis is None:
        return
    key = f"chat:{session_id}"
    redis.lpush(key, json.dumps({"role": role, "content": content}, ensure_ascii=False))
    redis.ltrim(key, 0, 19)
    redis.expire(key, 3600)
