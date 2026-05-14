from time import time
from typing import Any


def ok(data: Any) -> dict:
    return {
        "code": 200,
        "message": "success",
        "data": data,
        "timestamp": int(time() * 1000),
    }


def degraded(data: Any, message: str = "degraded") -> dict:
    return {
        "code": 200,
        "message": message,
        "data": data,
        "timestamp": int(time() * 1000),
    }

