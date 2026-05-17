# FastAPI-Backend 开发说明

本模块是拾花系统 AI 中台，负责 AI 识花、AI 贺卡生成、智能花语客服、评论情感分析，以及后续 ChromaDB 知识库检索。

## 开发工具

推荐使用 PyCharm 打开当前目录：

```text
D:\JavaWeb pratical trainning\FastAPI-Backend
```

虚拟环境必须放在项目内：

```text
D:\JavaWeb pratical trainning\FastAPI-Backend\.venv
```

## 安装依赖

```powershell
python -m venv .venv
.\.venv\Scripts\python -m pip install --cache-dir .\.pip-cache -r requirements.txt
```

## PyCharm 运行配置

使用 Python 运行配置，选择 `module`：

```text
Module name: uvicorn
Parameters: main:app --host 0.0.0.0 --port 5000 --reload
Working directory: D:\JavaWeb pratical trainning\FastAPI-Backend
Environment file: D:\JavaWeb pratical trainning\FastAPI-Backend\.env
```

访问：

```text
http://localhost:5000/health
```

接口文档：

```text
http://localhost:5000/docs
```

## 环境配置

复制 `.env.example` 为 `.env`：

```properties
DEEPSEEK_API_KEY=
DEEPSEEK_BASE_URL=https://api.deepseek.com/v1
DEEPSEEK_MODEL=deepseek-chat
MODEL_PATH=./runtime/models/flower_model.h5
CLASS_NAMES_PATH=./runtime/models/class_names.json
REDIS_URL=redis://localhost:6379/0
CHROMA_PATH=./runtime/chroma
```

没有 DeepSeek API Key 时，接口会返回降级文案，不会崩溃。

## 模型和运行数据

```text
runtime/models/flower_model.h5      Oxford-102 识花模型
runtime/models/class_names.json     类别映射
runtime/chroma/                     ChromaDB 向量库
logs/                               运行日志
```

这些运行数据不提交 Git。

## 当前接口

```text
GET  /health
POST /recognize
POST /generate_card
POST /chat
POST /chat/stream
POST /sentiment
GET  /vector/status
```

## 智能花语客服

已实现 `/chat` 接口，用于花语问答、送花建议、鲜花养护和购买引导。

请求示例：

```json
{
  "session_id": "web_123456",
  "message": "送妈妈生日适合什么花"
}
```

接口同时兼容前端常用的驼峰字段：

```json
{
  "sessionId": "web_123456",
  "message": "送妈妈生日适合什么花"
}
```

返回数据中的 `data` 示例：

```json
{
  "reply": "送妈妈生日可以优先考虑康乃馨，寓意感恩和关怀。也可以搭配百合，表达祝福和顺利。",
  "session_id": "web_123456",
  "source": "deepseek-chat",
  "context_used": []
}
```

流式接口：

```text
POST /chat/stream
```

该接口使用 SSE 返回事件：

```text
event: delta
data: {"event":"delta","content":"康"}

event: done
data: {"event":"done","reply":"完整回复","suggestions":["预算200以内怎么选？","康乃馨怎么养护？"]}
```

前端收到 `delta` 后逐步追加文本，收到 `done` 后显示 2 个联想问题。

实现说明：

- DeepSeek 配置读取当前目录 `.env` 中的 `DEEPSEEK_API_KEY`、`DEEPSEEK_BASE_URL`、`DEEPSEEK_MODEL`。
- Redis 可用时会把最近会话缓存到 `chat:{session_id}`，有效期 1 小时。
- Redis 不可用、DeepSeek Key 缺失或外部 API 调用失败时，接口返回降级文案，不让前端崩溃。
- `/chat` 会兼容 JSON、表单格式和空请求体，避免调试工具或代理传参异常时直接返回 500。
- `/chat/stream` 会流式返回 DeepSeek 内容，并在结束事件中返回 2 个联想问题。
- 当前先使用内置花语和养护知识做上下文，后续可替换为 ChromaDB 花语知识库检索。

## 下一步开发任务

优先顺序：

1. 完善 `/generate_card`，接入 DeepSeek 生成真实贺卡。
2. 将 `/chat` 的内置知识替换为 ChromaDB 花语知识库检索。
3. 完善 `/sentiment`，规范 LLM 输出 JSON。
4. 下载并放置 `flower_model.h5`，实现真实图片识别。
5. 将 ChromaDB 用于花语、养护、送花礼仪知识检索。

## 编码约定

- 路由只放在 `app/routers/`。
- 业务逻辑放在 `app/services/`。
- 模型推理封装放在 `app/models/`。
- Prompt 模板放在 `app/prompts/`。
- 所有接口返回统一结构：`code/message/data/timestamp`。
- 外部 API 超时必须返回降级结果。
