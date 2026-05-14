from app.services.llm_service import get_llm


def analyze_sentiment(text: str) -> dict:
    llm = get_llm()
    if llm is None:
        positive_words = ("好", "新鲜", "喜欢", "满意", "快")
        negative_words = ("差", "慢", "坏", "枯", "失望")
        sentiment = "neutral"
        if any(word in text for word in positive_words):
            sentiment = "positive"
        if any(word in text for word in negative_words):
            sentiment = "negative"
        return {"sentiment": sentiment, "keywords": [], "score": 0.5, "source": "fallback-rules"}

    prompt = (
        "Analyze this flower shop review. Return compact JSON with sentiment "
        f"positive/neutral/negative, keywords and score only: {text}"
    )
    result = llm.invoke(prompt)
    return {"raw": result.content, "source": "deepseek-chat"}

