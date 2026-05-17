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
MODEL_PATH=./runtime/models/final_flowers_dataset_v_1.pkl
CLASS_NAMES_PATH=./runtime/models/class_names.json
REDIS_URL=redis://localhost:6379/0
CHROMA_PATH=./runtime/chroma
```

没有 DeepSeek API Key 时，接口会返回降级文案，不会崩溃。

## 模型和运行数据

```text
runtime/models/final_flowers_dataset_v_1.pkl  Oxford-102 fastai 识花模型
runtime/models/class_names.json                类别映射
runtime/chroma/                                ChromaDB 向量库
logs/                                          运行日志
```

这些运行数据不提交 Git。

当前 AI 识花已接入指定 Hugging Face Space 的 fastai 模型，实际模型文件为：

```text
runtime/models/final_flowers_dataset_v_1.pkl
```

模型来源：

- Hugging Face Space：https://huggingface.co/spaces/tdnathmlenthusiast/flower_classifier
- GitHub 说明与 MIT License：https://github.com/darkangrycoder/102_oxford_flower_prediction

注意：该模型是 fastai/PyTorch pickle 文件，Hugging Face 标记为 `Unsafe pickle`。本项目只从上述指定来源下载到 `runtime/models/`，模型文件不提交 Git。

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

## AI 图片识花

识花接口：

```text
POST /recognize
Content-Type: multipart/form-data
```

请求字段：

```text
image  图片文件，必填
top_k  返回候选数量，可选，默认 5
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "predictions": [
      {
        "class_id": 7,
        "class_name_en": "moon orchid",
        "class_name_cn": "moon orchid",
        "confidence": 0.9977,
        "flower_language": "此花属于 Oxford Flowers 102 识别类别，可结合商品详情查看具体花语和养护建议。",
        "related_products": []
      }
    ]
  }
}
```

依赖安装仍然放在项目目录内：

```powershell
.\.venv\Scripts\python -m pip install --cache-dir .\.pip-cache -r requirements.txt
```

如果模型文件缺失或加载失败，接口会返回“模型未加载”的降级结果，不会导致 FastAPI 启动失败。

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
