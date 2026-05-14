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
POST /sentiment
GET  /vector/status
```

## 下一步开发任务

优先顺序：

1. 完善 `/generate_card`，接入 DeepSeek 生成真实贺卡。
2. 完善 `/chat`，把 Redis 会话记忆和花语知识库接入。
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

