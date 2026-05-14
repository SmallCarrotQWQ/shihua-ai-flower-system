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
FASTAPI_BASE_URL=http://localhost:5000
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

后续继续实现：

- `user`：地址管理、修改资料、修改密码
- `order`：模拟支付、确认收货、评价
- `admin`：后台登录、鲜花管理、分类管理、订单管理、用户管理
- `ai`：代理调用 FastAPI 的 AI 贺卡、AI 客服、AI 识花接口

## 管理员后台接口

已实现基础管理员接口，当前要求携带登录后的 JWT；角色级拦截可在后续接入管理员登录后再收紧。

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
