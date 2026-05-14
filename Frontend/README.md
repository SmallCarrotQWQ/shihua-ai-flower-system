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

优先实现用户端页面并接入后端接口：

- 登录页：调用 `POST /user/login`
- 注册页：调用 `POST /user/register`
- 首页：调用 `GET /flower/hot`、`GET /flower/recommend`
- 分类页：调用 `GET /category`
- 商品列表：调用 `GET /flower`
- 商品详情：调用 `GET /flower/{id}`
- 购物车：调用 `GET /cart`、`POST /cart`、`PUT /cart/{id}`、`DELETE /cart/{id}`
- 订单确认：调用 `POST /order`

管理员端在用户端闭环完成后再做：

- 数据看板
- 鲜花管理
- 分类管理
- 订单管理
- 用户管理

## 编码约定

- API 请求统一写在 `src/api/`，页面不要直接写 axios。
- 登录态统一放在 `src/stores/auth.ts`。
- 用户端组件优先考虑移动端宽度。
- 管理端组件优先使用 Element Plus 表格、表单和弹窗。

