# Frontend 开发说明

本模块是拾花系统前端，使用 Vue3 + Vite + TypeScript + Pinia + Axios，用户端偏移动端体验，管理员端使用 Element Plus。

## 开发工具

推荐使用 VSCode 打开当前目录：

```text
D:\JavaWeb pratical trainning\Frontend
```

建议安装插件：

- Vue - Official
- TypeScript Vue Plugin
- ESLint
- Prettier
- Markdown Preview Enhanced

## 启动命令

```powershell
npm install
npm run dev
```

访问：

```text
http://localhost:5173
```

构建检查：

```powershell
npm run build
```

## 环境配置

复制 `.env.example` 为 `.env`：

```properties
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_AI_BASE_URL=http://localhost:5000
```

如果 SpringBoot 改端口，必须同步修改 `VITE_API_BASE_URL`。

## 目录说明

```text
src/api/          Axios 请求封装和模块 API
src/components/   可复用组件，如 AI 客服、图片上传
src/layouts/      用户端、管理端布局
src/router/       Vue Router 路由表
src/stores/       Pinia 状态管理
src/views/        用户端页面
src/views/admin/  管理端页面
```

## 下一步开发任务

已实现并接入后端的用户端页面：

- 登录页：调用 `POST /user/login`
- 注册页：调用 `POST /user/register`
- 首页：调用 `GET /flower/hot`
- 分类页：调用 `GET /category`
- 商品列表：调用 `GET /flower`
- 商品详情：调用 `GET /flower/{id}`
- 购物车：调用 `GET /cart`、`POST /cart`、`PUT /cart/{id}`、`DELETE /cart/{id}`
- 订单确认：调用 `POST /order`，支持选择收货地址
- 个人资料：调用 `GET /user/info`、`PUT /user/info`、`PUT /user/password`
- 地址管理：调用 `GET/POST/PUT/DELETE /user/address`
- 我的订单：调用 `GET /order`、`POST /order/{id}/pay`、`POST /order/{id}/cancel`、`POST /order/{id}/receive`
- 订单评价：调用 `POST /review/order/{orderId}`
- 智能花语客服：流式对话直连 FastAPI `POST /chat/stream`，保留 SpringBoot `POST /ai/chat` 作为非流式备用接口

当前页面路由：

```text
/home
/category
/list
/list?categoryId=1
/detail/:id
/cart
/profile
/address
/orders
/ai-scan
/ai-card
/login
/register
```

本轮只做功能区分和接口对接，没有做最终视觉美化。后续美化时优先替换这些页面的布局和样式，不需要重写 API 封装。

管理员端在用户端闭环完成后再做：

已实现基础功能页：

- `/admin/dashboard`：数据看板，调用 `GET /admin/dashboard/stats`
- `/admin/flower`：鲜花管理，支持新增、修改、上下架、删除
- `/admin/category`：分类管理，支持新增、修改、删除
- `/admin/order`：订单管理，支持发货、取消，订单状态为待支付、待发货、待收货、已完成、已取消
- `/admin/user`：用户管理，支持启用、禁用

当前后台页面只做功能对接，不做最终视觉美化。后台接口已经收紧为 `ADMIN` 角色访问，前端路由也会根据登录用户的 `role` 控制后台入口和访问跳转。

## 用户中心与订单页面

本轮新增三个用户侧页面：

```text
/profile  个人资料与修改密码
/address  收货地址管理
/orders   我的订单、模拟支付、取消、确认收货、评价
```

购物车提交订单前会读取用户地址列表，优先选择默认地址。订单创建请求会携带 `addressId`，后端会保存地址快照。

## AI 识花与贺卡页面

当前新增两个用户侧 AI 页面：

```text
/ai-scan  图片文件上传识花
/ai-card  AI 贺卡生成与历史记录
```

`/ai-scan` 只支持选择本地图片文件上传，暂不接入拍照。页面会调用 FastAPI：

```text
POST ${VITE_AI_BASE_URL}/recognize
```

请求格式为 `multipart/form-data`，字段为 `image` 和可选 `top_k`。页面会展示图片预览、Top 5 识别结果、置信度、花语和关联商品 ID。

`/ai-card` 需要登录后访问，页面会调用 SpringBoot：

```text
POST /api/v1/ai/card
GET  /api/v1/ai/card
```

生成表单字段包括鲜花名称、可选鲜花 ID、收礼关系、场景、风格和字数。生成结果会保存到后端 `ai_card` 表，并在历史记录中展示。

## 智能花语客服组件

`src/components/AiChatWidget.vue` 已接入真实流式接口：

```text
POST http://localhost:5000/chat/stream
```

前端使用 `fetch + ReadableStream` 读取 SSE：

- `delta`：逐步追加 AI 回复文本
- `done`：结束回复并渲染 2 个联想问题按钮

前端会在 `localStorage` 中保存 `shihua_ai_chat_session`，用于保持同一浏览器的短期会话。接口封装在：

```text
src/api/ai.ts
```

运行时需要同时启动：

```text
FastAPI-Backend  http://localhost:5000
SpringBoot       http://localhost:8080/api/v1
Frontend         http://localhost:5173
```

如果 FastAPI 或 DeepSeek 暂时不可用，页面会显示后端返回的降级回复。

注意：为了获得真正的逐字流式效果，前端流式接口直接请求 `VITE_AI_BASE_URL`，默认值为 `http://localhost:5000`。SpringBoot 仍保留 `/api/v1/ai/chat/stream` 代理接口，但部分本地 Java HTTP 代理会缓冲 SSE，不建议前端流式场景优先使用。

## 编码约定

- API 请求统一写在 `src/api/`，页面不要直接写 axios。
- 登录态统一放在 `src/stores/auth.ts`。
- 用户端组件优先考虑移动端宽度。
- 管理端组件优先使用 Element Plus 表格、表单和弹窗。
