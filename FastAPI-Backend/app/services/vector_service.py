from app.config import settings


def ensure_chroma() -> None:
    settings.chroma_path_obj.mkdir(parents=True, exist_ok=True)


def vector_status() -> dict:
    ensure_chroma()
    return {
        "chroma_path": settings.chroma_path,
        "ready": settings.chroma_path_obj.exists(),
        "purpose": "flower knowledge retrieval for AI customer service",
    }

