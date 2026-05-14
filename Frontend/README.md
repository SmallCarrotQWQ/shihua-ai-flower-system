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
- 订单确认：调用 `POST /order`

当前页面路由：

```text
/home
/category
/list
/list?categoryId=1
/detail/:id
/cart
/login
/register
```

本轮只做功能区分和接口对接，没有做最终视觉美化。后续美化时优先替换这些页面的布局和样式，不需要重写 API 封装。

管理员端在用户端闭环完成后再做：

已实现基础功能页：

- `/admin/dashboard`：数据看板，调用 `GET /admin/dashboard/stats`
- `/admin/flower`：鲜花管理，支持新增、修改、上下架、删除
- `/admin/category`：分类管理，支持新增、修改、删除
- `/admin/order`：订单管理，支持发货、取消
- `/admin/user`：用户管理，支持启用、禁用

当前后台页面只做功能对接，不做最终视觉美化。后台接口目前要求已登录用户携带 JWT，后续可以在后端增加 `ROLE_ADMIN` 拦截。

## 编码约定

- API 请求统一写在 `src/api/`，页面不要直接写 axios。
- 登录态统一放在 `src/stores/auth.ts`。
- 用户端组件优先考虑移动端宽度。
- 管理端组件优先使用 Element Plus 表格、表单和弹窗。
