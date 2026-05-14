from pydantic import BaseModel
from fastapi import APIRouter

from app.core.response import ok
from app.services.sentiment_service import analyze_sentiment

router = APIRouter()


class SentimentRequest(BaseModel):
    text: str


@router.post("/sentiment")
async def sentiment_endpoint(request: SentimentRequest) -> dict:
    return ok(analyze_sentiment(request.text))

