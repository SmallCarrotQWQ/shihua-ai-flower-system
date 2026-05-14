# Configuration 配置说明

本文件说明项目中账号、密钥、数据库、模型和缓存配置应该放在哪里。

## 原则

- 真实账号、密码、API Key 不提交 Git。
- 只提交 `.env.example`。
- 本地使用 `.env`。
- 模型文件、上传文件、ChromaDB 数据不提交 Git。

## SpringBoot 配置

位置：

```text
SpringBoot-Backend/.env
```

示例：

```properties
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=shihua_ai_flower
MYSQL_USERNAME=root
MYSQL_PASSWORD=你的MySQL密码
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=replace-with-at-least-32-characters-secret
FASTAPI_BASE_URL=http://localhost:5000
```

IDEA 运行时要确保 Working directory 是：

```text
D:\JavaWeb pratical trainning\SpringBoot-Backend
```

否则 `.env` 可能读不到。

## FastAPI 配置

位置：

```text
FastAPI-Backend/.env
```

示例：

```properties
DEEPSEEK_API_KEY=
DEEPSEEK_BASE_URL=https://api.deepseek.com/v1
DEEPSEEK_MODEL=deepseek-chat
MODEL_PATH=./runtime/models/flower_model.h5
CLASS_NAMES_PATH=./runtime/models/class_names.json
REDIS_URL=redis://localhost:6379/0
CHROMA_PATH=./runtime/chroma
```

## Frontend 配置

位置：

```text
Frontend/.env
```

示例：

```properties
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_AI_BASE_URL=http://localhost:5000
```

## 模型文件

识花模型放在：

```text
FastAPI-Backend/runtime/models/flower_model.h5
```

类别映射放在：

```text
FastAPI-Backend/runtime/models/class_names.json
```

这些文件不提交 Git。

## Redis 用途

后续建议用于：

- 验证码缓存
- JWT 黑名单
- 热点鲜花缓存
- 首页推荐缓存
- AI 客服会话短期记忆
- 简单接口限流

## ChromaDB 用途

ChromaDB 用于 AI 客服增强检索：

- 花语知识
- 鲜花养护
- 送花礼仪
- 节日推荐

数据目录：

```text
FastAPI-Backend/runtime/chroma
```

