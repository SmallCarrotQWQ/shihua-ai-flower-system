# 拾花 AI 鲜花售卖系统

这是一个三端分离的课程实训项目骨架，包含 Vue3 前端、SpringBoot 业务后端、FastAPI AI 中台、MySQL SQL 脚本和本地部署说明。

## 项目结构

```text
Frontend/            Vue3 + Vite 前端，使用 VSCode 开发
SpringBoot-Backend/  SpringBoot 业务后端，使用 IDEA 开发
FastAPI-Backend/     FastAPI AI 服务，使用 PyCharm 开发
database/            MySQL 建库、建表、初始化数据脚本
deploy/              本地部署和端口说明
docs/                配置、安全、后续开发说明
storage/uploads/     上传文件目录，真实文件不提交 Git
project plan/        原始项目设计文档
```

## 当前状态

- 前端已可运行：`http://localhost:5173`
- SpringBoot 已可运行：`http://localhost:8080/api/v1/health`
- MySQL 已接入：默认库名 `shihua_ai_flower`
- FastAPI AI 服务已接入：`http://localhost:5000/health`
- 用户注册登录、鲜花浏览、购物车、提交订单、管理员基础后台、智能花语客服已完成
- 本轮新增用户中心、地址管理、订单支付/取消/收货/评价、后台角色拦截和 AI 贺卡后端入口
- Redis、AI 识花真实模型、ChromaDB 知识库检索仍作为后续增强项

## 推荐开发顺序

1. 当前主线：
   用户中心、地址管理、订单支付/收货/评价、管理员权限已经进入可联调阶段。
2. 下一阶段：
   完善 AI 贺卡前端入口、AI 识花上传识别、评论情感分析 FastAPI 化。
3. 上线前整理：
   Redis 缓存/限流、ChromaDB 知识库检索、接口测试、前端视觉美化和 Git 分阶段提交。

## 默认测试账号

执行 `database/03_seed_data.sql` 后可使用：

```text
管理员：admin / admin123
普通用户：demo / admin123
```

管理员账号角色为 `ADMIN`，普通用户角色为 `USER`。后台接口 `/api/v1/admin/**` 需要管理员角色。

## 启动顺序

1. 启动 MySQL，并执行 `database/` 下三个 SQL 文件。
2. 可选启动 Redis。
3. PyCharm 启动 `FastAPI-Backend`。
4. IDEA 启动 `SpringBoot-Backend`。
5. VSCode 启动 `Frontend`。

## 本地依赖规则

依赖和缓存都放在项目目录中，不放 C 盘：

- npm cache：`Frontend/.npm-cache`
- Python venv：`FastAPI-Backend/.venv`
- pip cache：`FastAPI-Backend/.pip-cache`
- Maven repo：`SpringBoot-Backend/.m2/repository`

真实 `.env`、模型文件、上传文件、虚拟环境、`node_modules` 不提交 Git。

## GitHub 上传

首次上传推荐流程：

```powershell
git init
git add .
git commit -m "Initial ShiHua project scaffold"
git branch -M main
git remote add origin https://github.com/<your-name>/<your-repo>.git
git push -u origin main
```

如果远程仓库已经存在，直接替换 `<your-name>/<your-repo>` 即可。
