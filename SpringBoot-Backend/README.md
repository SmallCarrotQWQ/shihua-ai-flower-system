# SpringBoot-Backend 开发说明

本模块是拾花系统业务后端，负责用户、权限、鲜花商品、购物车、订单、管理员后台和调用 FastAPI AI 服务。

## 开发工具

推荐使用 IDEA 打开当前目录：

```text
D:\JavaWeb pratical trainning\SpringBoot-Backend
```

建议使用 JDK 17 或 JDK 21。当前项目不建议使用 Java 25 做长期开发。

## 启动入口

主类：

```text
com.shihua.ShiHuaApplication
```

健康检查：

```text
http://localhost:8080/api/v1/health
```

## IDEA 运行配置

关键配置：

```text
JRE: Java 17 或 Java 21
Main class: com.shihua.ShiHuaApplication
Working directory: D:\JavaWeb pratical trainning\SpringBoot-Backend
Active profile: dev
```

`application-dev.yml` 会自动读取当前目录下的 `.env`。

## 环境配置

复制 `.env.example` 为 `.env`，至少配置：

```properties
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=shihua_ai_flower
MYSQL_USERNAME=root
MYSQL_PASSWORD=你的MySQL密码
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=replace-with-at-least-32-characters-secret
FASTAPI_BASE_URL=http://127.0.0.1:5000
```

如果暂时没有 Redis，项目仍可启动；只是 actuator 健康检查会提示 Redis 连接失败。

## Maven 本地仓库

为了避免依赖下载到 C 盘，IDEA 的 Maven local repository 建议设置为：

```text
D:\JavaWeb pratical trainning\SpringBoot-Backend\.m2\repository
```

命令行运行示例：

```powershell
mvn -Dmaven.repo.local=.\.m2\repository spring-boot:run
```

## 目录说明

```text
src/main/java/com/shihua/common/   统一响应、通用异常、基础工具
src/main/java/com/shihua/config/   配置类，如 CORS、RestClient、属性绑定
src/main/java/com/shihua/security/ 安全配置、JWT、RBAC 权限
src/main/java/com/shihua/modules/  业务模块
src/main/resources/mapper/         MyBatis XML
```

业务模块推荐结构：

```text
modules/user/
  controller/
  service/
  mapper/
  entity/
  dto/
  vo/
```

当前骨架为了简单先把 Controller 放在模块根目录，后续实现业务时可以按上面结构拆分。

## 下一步开发任务

已实现的基础电商闭环接口：

- `user`：注册、登录、当前用户信息
- `category`：分类列表
- `flower`：鲜花列表、鲜花详情、热销榜
- `cart`：加入购物车、购物车列表、修改数量、删除、清空
- `order`：提交订单、订单列表、订单详情

当前可测试接口：

```text
POST   /api/v1/user/register
POST   /api/v1/user/login
GET    /api/v1/user/info
GET    /api/v1/category
GET    /api/v1/flower
GET    /api/v1/flower?categoryId=1&keyword=玫瑰
GET    /api/v1/flower/hot?limit=8
GET    /api/v1/flower/{id}
GET    /api/v1/cart
POST   /api/v1/cart
PUT    /api/v1/cart/{id}
DELETE /api/v1/cart/{id}
DELETE /api/v1/cart
POST   /api/v1/order
GET    /api/v1/order
GET    /api/v1/order/{id}
```

购物车和订单接口需要 Header：

```text
Authorization: Bearer <token>
```

本轮已继续补齐：

- `user`：资料修改、密码修改、地址管理
- `order`：模拟支付、确认收货、取消订单、评价入口
- `admin`：基于 `ADMIN` 角色的后台接口拦截
- `ai`：AI 贺卡后端代理与落库

## 智能花语客服接口

已实现 SpringBoot 到 FastAPI 的客服代理接口。当前为了保证真正的逐字流式体验，前端流式客服优先直连 FastAPI `/chat/stream`；SpringBoot 保留非流式 `/ai/chat` 和备用流式代理 `/ai/chat/stream`。

```text
POST /api/v1/ai/chat
POST /api/v1/ai/chat/stream
GET  /api/v1/ai/health
```

`/ai/chat` 当前允许未登录访问；如果用户已登录并携带 JWT，后端会优先使用用户身份作为会话标识。前端传入 `sessionId` 时使用前端会话。

请求示例：

```json
{
  "sessionId": "web_123456",
  "message": "送妈妈生日适合什么花"
}
```

返回数据来自 FastAPI：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "reply": "送妈妈生日可以考虑康乃馨，寓意感恩和关怀。",
    "session_id": "web_123456",
    "source": "deepseek-chat"
  }
}
```

如果 FastAPI 未启动，SpringBoot 会返回 `springboot-fallback` 降级文案。

`/ai/chat/stream` 用于前端流式输出，SpringBoot 会把 FastAPI 的 SSE 事件透传给浏览器：

```text
event: delta
data: {"event":"delta","content":"康"}

event: done
data: {"event":"done","reply":"完整回复","suggestions":["预算200以内怎么选？","康乃馨怎么养护？"]}
```

前端会把 `suggestions` 渲染为 AI 回复下方的 2 个联想问题按钮。

SpringBoot 调用 FastAPI 时会使用 JSON 请求体，并建议 `.env` 中配置：

```properties
FASTAPI_BASE_URL=http://127.0.0.1:5000
```

Windows 本地开发中 `127.0.0.1` 比 `localhost` 更稳定，可避免少数情况下 IPv4/IPv6 解析差异。

## 用户中心与地址接口

用户中心接口都需要携带登录后的 JWT：

```text
GET    /api/v1/user/info
PUT    /api/v1/user/info
PUT    /api/v1/user/password
GET    /api/v1/user/address
POST   /api/v1/user/address
PUT    /api/v1/user/address/{id}
DELETE /api/v1/user/address/{id}
PUT    /api/v1/user/address/{id}/default
```

`PUT /user/info` 支持更新手机号、邮箱、头像和性别。`PUT /user/password` 会校验原密码后再保存 BCrypt 新密码。地址接口会校验地址归属，普通用户只能维护自己的地址。

地址新增/修改请求示例：

```json
{
  "receiver": "张三",
  "phone": "13800000000",
  "province": "广东省",
  "city": "广州市",
  "district": "天河区",
  "detail": "体育西路 100 号",
  "isDefault": 1
}
```

## 订单闭环接口

订单状态统一为：

```text
0 待支付
1 待发货
2 待收货
3 已完成
4 已取消
```

用户端订单接口：

```text
POST /api/v1/order
GET  /api/v1/order
GET  /api/v1/order/{id}
POST /api/v1/order/{id}/pay
POST /api/v1/order/{id}/receive
POST /api/v1/order/{id}/cancel
```

提交订单时支持传入 `addressId`，后端会校验该地址属于当前用户，并把地址快照写入订单。提交订单继续保持扣减库存；取消待支付/待发货订单会恢复库存。

评价接口：

```text
POST /api/v1/review/order/{orderId}
GET  /api/v1/review/flower/{flowerId}
```

当前评价需要订单处于“已完成”状态，并且只能评价订单中包含的鲜花。情感分析已预留字段，本轮先使用后端关键词规则生成 `positive/neutral/negative`，后续可替换为 FastAPI `/sentiment`。

## 管理员后台接口

后台接口现在要求 JWT 中用户角色为 `ADMIN`，普通 `USER` 不能访问 `/api/v1/admin/**`。

```text
GET    /api/v1/admin/dashboard/stats
GET    /api/v1/admin/flower
POST   /api/v1/admin/flower
PUT    /api/v1/admin/flower/{id}
PUT    /api/v1/admin/flower/{id}/status
DELETE /api/v1/admin/flower/{id}
GET    /api/v1/admin/category
POST   /api/v1/admin/category
PUT    /api/v1/admin/category/{id}
DELETE /api/v1/admin/category/{id}
GET    /api/v1/admin/order
PUT    /api/v1/admin/order/{id}/status
GET    /api/v1/admin/user
PUT    /api/v1/admin/user/{id}/status
GET    /api/v1/admin/review
```

鲜花新增/修改请求示例：

```json
{
  "flowerName": "红玫瑰礼盒",
  "categoryId": 1,
  "price": 99.00,
  "stock": 100,
  "coverImage": "",
  "description": "经典红玫瑰礼盒",
  "flowerLanguage": "热烈的爱",
  "careGuide": "斜剪花茎，保持清水。",
  "status": 1
}
```

状态更新统一使用：

```json
{
  "status": 1
}
```

订单管理中 `status=2` 表示管理员发货，`status=4` 表示管理员取消订单并恢复库存。初始化 SQL 已提供默认管理员账号 `admin / admin123`，角色为 `ADMIN`。

## AI 贺卡接口

SpringBoot 已增加 AI 贺卡业务入口，负责调用 FastAPI 并把生成结果保存到 `ai_card` 表：

```text
POST /api/v1/ai/card
GET  /api/v1/ai/card
```

前端已接入 `/ai-card` 页面，用户登录后可填写鲜花名称、收礼关系、场景、风格和字数生成贺卡，并查看历史记录。

请求示例：

```json
{
  "flowerId": 1001,
  "flowerName": "康乃馨",
  "relation": "妈妈",
  "occasion": "生日",
  "style": "温暖",
  "length": 50
}
```

如果 FastAPI 暂时不可用，后端会生成一段降级祝福文案，保证业务流程不断。

## 基础下单流程

1. 注册或登录获取 token。
2. 调用 `GET /flower` 或 `GET /flower/{id}` 选择商品。
3. 调用 `POST /cart` 加入购物车：

```json
{
  "flowerId": 1001,
  "quantity": 1
}
```

4. 调用 `GET /cart` 查看购物车。
5. 调用 `POST /order` 提交订单：

```json
{
  "cartIds": [1, 2],
  "addressId": 1,
  "remark": "前端提交订单"
}
```

如果 `cartIds` 为空，后端会默认提交当前用户购物车内全部商品。

## 编码约定

- Controller 只处理 HTTP 入参和响应，不写复杂业务。
- Service 负责事务、库存扣减、权限判断和业务流程。
- Mapper 使用 MyBatis-Plus，复杂 SQL 再写 XML。
- 所有响应使用 `ApiResponse<T>` 包装。
- 密码必须 BCrypt 加密。
- 后续 JWT 黑名单、验证码、热点商品缓存放 Redis。
