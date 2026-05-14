from redis import Redis
from redis.exceptions import RedisError

from app.config import settings


def get_redis() -> Redis | None:
    try:
        client = Redis.from_url(settings.redis_url, decode_responses=True, socket_timeout=1)
        client.ping()
        return client
    except RedisError:
        return None

