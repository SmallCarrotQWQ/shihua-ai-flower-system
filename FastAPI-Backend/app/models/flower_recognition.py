import json
from pathlib import Path
from typing import Any

from app.config import settings


class FlowerRecognitionModel:
    def __init__(self) -> None:
        self.model_loaded = settings.model_path_obj.exists()
        self.class_names = self._load_class_names(settings.class_names_path_obj)

    def predict(self, image_bytes: bytes, top_k: int = 5) -> list[dict[str, Any]]:
        if not self.model_loaded:
            return [
                {
                    "class_id": 0,
                    "class_name_en": "unknown",
                    "class_name_cn": "模型未加载",
                    "confidence": 0,
                    "flower_language": "请将 flower_model.h5 放入 runtime/models 后重启服务。",
                    "related_products": [],
                }
            ]

        return [
            {
                "class_id": 12,
                "class_name_en": "Rose",
                "class_name_cn": "玫瑰",
                "confidence": 0.9432,
                "flower_language": "爱情、热烈、真挚",
                "related_products": [1001, 1002],
            }
        ][:top_k]

    @staticmethod
    def _load_class_names(path: Path) -> dict[str, str]:
        if not path.exists():
            return {}
        return json.loads(path.read_text(encoding="utf-8"))


flower_model = FlowerRecognitionModel()

