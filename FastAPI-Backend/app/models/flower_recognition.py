import json
import os
import pathlib
from io import BytesIO
from pathlib import Path
from typing import Any

from PIL import Image

from app.config import settings


DEFAULT_FASTAI_MODEL_PATH = Path("./runtime/models/final_flowers_dataset_v_1.pkl")

FLOWER_LANGUAGE = {
    "rose": "爱情、热烈、真挚",
    "sunflower": "阳光、希望、坚定",
    "carnation": "感恩、关怀、亲情",
    "lily": "纯洁、祝福、顺利",
    "tiger lily": "热情、活力、勇敢",
    "water lily": "纯净、宁静、高洁",
    "daisy": "天真、快乐、希望",
    "camellia": "谦逊、美德、理想之爱",
    "hibiscus": "温柔、美丽、珍惜当下",
    "lotus": "纯洁、高雅、坚韧",
}

FLOWER_NAME_CN = {
    "rose": "玫瑰",
    "sunflower": "向日葵",
    "carnation": "康乃馨",
    "tiger lily": "虎百合",
    "water lily": "睡莲",
    "camellia": "山茶花",
    "hibiscus": "木槿",
    "lotus": "莲花",
    "daffodil": "水仙",
    "anthurium": "红掌",
    "bird of paradise": "天堂鸟",
    "snapdragon": "金鱼草",
    "poinsettia": "一品红",
    "morning glory": "牵牛花",
    "magnolia": "木兰",
    "azalea": "杜鹃",
}


class FlowerRecognitionModel:
    def __init__(self) -> None:
        self.model_path = self._resolve_model_path()
        self.model_loaded = self.model_path.exists()
        self.class_names = self._load_class_names(settings.class_names_path_obj)
        self.learner = None
        self.load_error = ""
        if self.model_loaded:
            self._load_model()

    def predict(self, image_bytes: bytes, top_k: int = 5) -> list[dict[str, Any]]:
        top_k = max(1, min(top_k, 10))
        if not self.learner:
            return [
                {
                    "class_id": 0,
                    "class_name_en": "unknown",
                    "class_name_cn": "模型未加载",
                    "confidence": 0,
                    "flower_language": self.load_error or "请将 final_flowers_dataset_v_1.pkl 放入 runtime/models 后重启服务。",
                    "related_products": [],
                }
            ]

        try:
            image = Image.open(BytesIO(image_bytes)).convert("RGB")
        except Exception:
            return [
                {
                    "class_id": 0,
                    "class_name_en": "invalid-image",
                    "class_name_cn": "图片读取失败",
                    "confidence": 0,
                    "flower_language": "请上传 JPG、PNG 等常见图片格式，并尽量保证花朵主体清晰。",
                    "related_products": [],
                }
            ]
        with self.learner.no_bar():
            _, _, probs = self.learner.predict(image)
        vocab = list(self.learner.dls.vocab)
        values = probs.detach().cpu().tolist()
        ranked = sorted(enumerate(values), key=lambda item: item[1], reverse=True)[:top_k]
        return [self._to_prediction(vocab[index], score) for index, score in ranked]

    def status(self) -> dict[str, Any]:
        return {
            "model_path": str(self.model_path),
            "model_exists": self.model_path.exists(),
            "model_loaded": bool(self.learner),
            "load_error": self.load_error,
        }

    def _load_model(self) -> None:
        try:
            # The upstream fastai export was created on Linux and contains PosixPath objects.
            if os.name == "nt":
                pathlib.PosixPath = pathlib.WindowsPath
            from fastai.vision.all import load_learner

            self.learner = load_learner(self.model_path, cpu=True)
            self.model_loaded = True
        except Exception as exc:
            self.learner = None
            self.model_loaded = False
            self.load_error = f"模型加载失败：{exc}"

    def _to_prediction(self, class_name: str, confidence: float) -> dict[str, Any]:
        normalized = class_name.strip().lower()
        class_id = self._class_id(normalized)
        return {
            "class_id": class_id,
            "class_name_en": class_name,
            "class_name_cn": FLOWER_NAME_CN.get(normalized, class_name),
            "confidence": round(float(confidence), 4),
            "flower_language": self._flower_language(normalized),
            "related_products": self._related_products(normalized),
        }

    def _class_id(self, class_name: str) -> int:
        value = self.class_names.get(class_name)
        if isinstance(value, int):
            return value
        if isinstance(value, str) and value.isdigit():
            return int(value)
        if self.learner:
            try:
                return list(self.learner.dls.vocab).index(class_name) + 1
            except ValueError:
                return 0
        return 0

    def _flower_language(self, class_name: str) -> str:
        for key, value in FLOWER_LANGUAGE.items():
            if key in class_name:
                return value
        return "此花属于 Oxford Flowers 102 识别类别，可结合商品详情查看具体花语和养护建议。"

    def _related_products(self, class_name: str) -> list[int]:
        if "rose" in class_name:
            return [1001, 1002]
        if "carnation" in class_name:
            return [1003]
        if "sunflower" in class_name:
            return [1004]
        return []

    @staticmethod
    def _resolve_model_path() -> Path:
        configured = settings.model_path_obj
        if configured.exists() and configured.suffix.lower() == ".pkl":
            return configured
        return DEFAULT_FASTAI_MODEL_PATH

    @staticmethod
    def _load_class_names(path: Path) -> dict[str, str]:
        if not path.exists():
            return {}
        return json.loads(path.read_text(encoding="utf-8"))


flower_model = FlowerRecognitionModel()

