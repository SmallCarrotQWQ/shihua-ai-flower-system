from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.routers import chat, generate, recognize, sentiment
from app.services.vector_service import ensure_chroma

app = FastAPI(title="ShiHua FastAPI AI Service", version="0.1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://127.0.0.1:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(recognize.router)
app.include_router(generate.router)
app.include_router(chat.router)
app.include_router(sentiment.router)


@app.on_event("startup")
async def startup() -> None:
    ensure_chroma()


@app.get("/health")
async def health() -> dict:
    return {
        "code": 200,
        "message": "success",
        "data": {
            "service": "FastAPI-Backend",
            "status": "UP",
            "model_path": settings.model_path,
            "model_exists": settings.model_path_obj.exists(),
            "chroma_path": settings.chroma_path,
            "deepseek_configured": bool(settings.deepseek_api_key),
        },
    }

