from fastapi import APIRouter, File, Form, UploadFile

from app.core.response import ok
from app.models.flower_recognition import flower_model

router = APIRouter()


@router.post("/recognize")
async def recognize(image: UploadFile = File(...), top_k: int = Form(5)) -> dict:
    image_bytes = await image.read()
    predictions = flower_model.predict(image_bytes, top_k=top_k)
    return ok({"predictions": predictions})

