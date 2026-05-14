from pydantic import BaseModel
from fastapi import APIRouter

from app.core.response import ok
from app.services.chat_service import chat
from app.services.vector_service import vector_status

router = APIRouter()


class ChatRequest(BaseModel):
    session_id: str
    message: str


@router.post("/chat")
async def chat_endpoint(request: ChatRequest) -> dict:
    return ok(chat(request.session_id, request.message))


@router.get("/vector/status")
async def vector_status_endpoint() -> dict:
    return ok(vector_status())

