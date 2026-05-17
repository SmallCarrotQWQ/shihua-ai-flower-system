import json
from urllib.parse import parse_qs

from fastapi import APIRouter, Request
from fastapi.responses import StreamingResponse

from app.core.response import ok
from app.services.chat_service import chat, chat_stream
from app.services.vector_service import vector_status

router = APIRouter()


@router.post("/chat")
async def chat_endpoint(request: Request) -> dict:
    payload = await _read_payload(request)
    session_id = payload.get("session_id") or payload.get("sessionId") or "guest"
    message = str(payload.get("message") or "").strip()
    return ok(chat(str(session_id), message))


@router.post("/chat/stream")
async def chat_stream_endpoint(request: Request) -> StreamingResponse:
    payload = await _read_payload(request)
    session_id = payload.get("session_id") or payload.get("sessionId") or "guest"
    message = str(payload.get("message") or "").strip()
    return StreamingResponse(
        _sse_events(chat_stream(str(session_id), message)),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no",
        },
    )


def _sse_events(events):
    for event in events:
        event_name = event.get("event", "message")
        yield f"event: {event_name}\n"
        yield "data: " + json.dumps(event, ensure_ascii=False) + "\n\n"


async def _read_payload(request: Request) -> dict:
    payload = dict(request.query_params)
    body = await request.body()
    if not body:
        return payload

    try:
        body_payload = json.loads(body)
        if isinstance(body_payload, dict):
            payload.update(body_payload)
        return payload
    except json.JSONDecodeError:
        text = body.decode("utf-8", errors="ignore")
        form_payload = parse_qs(text)
        payload.update({key: values[-1] for key, values in form_payload.items() if values})
        return payload


@router.get("/vector/status")
async def vector_status_endpoint() -> dict:
    return ok(vector_status())
