# Database 开发说明

本目录保存 MySQL 建库、建表和初始化数据脚本。

## 默认数据库

```text
shihua_ai_flower
```

## 执行顺序

在 DataGrip 中按顺序执行：

```text
01_create_database.sql
02_schema.sql
03_seed_data.sql
```

也可以在 MySQL 命令行中执行：

```sql
source database/01_create_database.sql;
source database/02_schema.sql;
source database/03_seed_data.sql;
```

## 表说明

```text
sys_user            用户表
flower_category     鲜花分类表
flower              鲜花商品表
user_address        收货地址表
cart                购物车表
orders              订单主表
order_item          订单明细表
flower_knowledge    AI 识花和花语知识映射表
review              评论表
ai_card             AI 贺卡记录表
browse_record       浏览记录表
system_log          系统日志表
```

订单表使用 `orders`，不使用 `order`，避免和 SQL 关键字冲突。

## 后续维护规则

- 新增表结构写入 `02_schema.sql`。
- 初始化演示数据写入 `03_seed_data.sql`。
- 如果之前执行过乱码版种子数据，可以重新执行 `03_seed_data.sql` 修正分类和鲜花中文名称。
- 不要把真实用户数据、真实手机号、真实订单数据提交到 Git。
- 涉及库存、订单、购物车的字段要优先考虑索引。
- 金额字段统一使用 `DECIMAL`，不要使用浮点数。

## 默认账号

`03_seed_data.sql` 会初始化两个开发测试账号：

```text
管理员：admin / admin123
普通用户：demo / admin123
```

两个账号的密码均使用 BCrypt 保存。管理员账号角色为 `ADMIN`，普通用户角色为 `USER`，用于验证后台 `/api/v1/admin/**` 角色拦截。

## 订单状态

订单状态字段 `orders.status` 当前约定为：

```text
0 待支付
1 待发货
2 待收货
3 已完成
4 已取消
```

提交订单时扣减库存；取消待支付或待发货订单时恢复库存。

## 和后端对应关系

SpringBoot 后续实体类建议按表名创建：

```text
SysUser
FlowerCategory
Flower
UserAddress
Cart
Orders
OrderItem
FlowerKnowledge
Review
AiCard
BrowseRecord
SystemLog
```
