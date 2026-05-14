from pydantic import BaseModel, Field
from fastapi import APIRouter

from app.core.response import ok
from app.services.card_service import generate_card

router = APIRouter()


class CardRequest(BaseModel):
    flower_name: str
    relation: str
    occasion: str
    style: str = "浪漫"
    length: int = Field(default=50, ge=10, le=200)


@router.post("/generate_card")
async def generate_card_endpoint(request: CardRequest) -> dict:
    return ok(generate_card(**request.model_dump()))

