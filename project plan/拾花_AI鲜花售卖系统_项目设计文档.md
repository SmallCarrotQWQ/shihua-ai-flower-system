# **拾花** —— AI 赋能鲜花售卖系统项目设计文档

> **课程**：JavaWeb程序设计实训  
> **团队**：3人小组  
> **技术栈**：SpringBoot + FastAPI + LangChain + Vue3 + MySQL  
> **版本**：v1.0  
> **日期**：2026-05-07

---

## 1. 项目概述

### 1.1 项目背景
在传统鲜花电商基础上，融入 **AI 视觉识别** 与 **大语言模型（LLM）** 能力，打造一款具备拍照识花、智能贺卡生成、花语问答等创新功能的鲜花售卖系统。

### 1.2 核心创新点
| 创新功能 | 技术实现 | 业务价值 |
|---------|---------|---------|
| **AI 拍照识花** | 本地 FastAPI 加载 Oxford-102 预训练 ResNet50 模型 | 用户上传照片即可识别花种并推荐商品 |
| **AI 贺卡生成** | LangChain + DeepSeek-V4 编排 Prompt 自动生成文案 | 根据花种、节日、收花人关系生成定制祝福语 |
| **智能花语客服** | LangChain ConversationBufferMemory + DeepSeek-V4 | 7x24 小时回答鲜花养护、送花礼仪等问题 |
| **智能推荐** | 基于用户浏览/购买行为的协同过滤算法 | 首页猜你喜欢提升转化率 |

---

## 2. 需求分析

### 2.1 功能需求

#### 2.1.1 基础功能（必做）

| 模块 | 功能点 | 说明 |
|------|--------|------|
| **用户模块** | 用户注册 | 用户名、密码、性别、手机号；JS 校验（非空、密码>=6位、确认密码一致、验证码） |
| | 用户登录 | JS 校验用户名/密码非空；Servlet 校验账号密码；记住用户名 |
| | 用户信息修改 | 修改头像、收货地址、密码 |
| | 权限管理 | RBAC 模型：ROLE_USER / ROLE_ADMIN |
| **商品模块** | 鲜花查询 | 按分类、价格区间、花语关键词检索；支持分页 |
| | 鲜花详情 | 展示图片、价格、库存、花语、养护指南 |
| | 选购鲜花 | 未登录可浏览，登录后可加入购物车 |
| **购物车模块** | 查看购物车 | 展示商品、数量、单价、小计、时间戳 |
| | 数量调整 | JS 实现 +/- 按钮实时计算总价 |
| | 删除商品 | 单删或批量清空 |
| **订单模块** | 提交订单 | 选择收货地址、生成订单、扣减库存 |
| | 订单管理 | 用户查看历史订单；管理员查看全部订单 |
| | 订单状态流转 | 待付款 -> 已付款 -> 已发货 -> 已完成 |
| **管理员模块** | 鲜花信息管理 | 增删改查鲜花（富文本编辑器录入花语/养护） |
| | 订单管理 | 查看、发货、取消订单 |
| | 用户管理 | 查看注册用户、禁用账号 |
| | 数据统计 | 订单量、销售额、热销榜（ECharts 图表） |

#### 2.1.2 AI 创新功能（加分项）

| 功能 | 用户场景 | 技术归属 |
|------|---------|---------|
| **AI 拍照识花** | 用户在路边看到一束好看的花，拍照上传，系统识别花名并跳转购买页 | FastAPI + 本地 ResNet50 |
| **AI 贺卡生成** | 结算页选择收花人关系（妈妈/女友/老师），一键生成温馨文案 | FastAPI + LangChain + DeepSeek |
| **智能花语客服** | 商品页右侧悬浮窗，询问玫瑰适合送什么人 | FastAPI + LangChain + DeepSeek |
| **评论情感分析** | 管理员后台自动分析用户评价情感倾向（正面/中性/负面） | FastAPI + DeepSeek |

### 2.2 非功能需求

| 类别 | 要求 |
|------|------|
| **性能** | 页面首屏加载 < 2s；AI 识花接口 < 800ms；LLM 接口 < 3s |
| **并发** | 支持 50 人同时在线（课程设计演示规模） |
| **兼容性** | 移动端优先适配（Flex/Grid + 媒体查询），兼容 Chrome/Safari/Edge |
| **安全** | 密码 BCrypt 加密；JWT Token 鉴权；SQL 注入防护（MyBatis 预编译）；XSS 过滤 |
| **可靠性** | AI 服务降级策略：LLM 超时或异常时，返回预设兜底文案 |

---

## 3. 系统架构设计

### 3.1 整体架构图

```
+-------------------------------------------------------------+
|                        前端层 (Vue3)                         |
|  +----------+ +----------+ +----------+ +----------+      |
|  | 用户端H5 | | 管理端PC | | 拍照组件 | | 聊天浮窗 |      |
|  +----------+ +----------+ +----------+ +----------+      |
+----------------------+--------------------------------------+
                       | Axios / REST
+----------------------v--------------------------------------+
|              网关层 (Nginx / SpringBoot CORS)               |
+----------------------+--------------------------------------+
                       |
          +------------+------------+
          |                         |
+---------v----------+    +---------v------------+
|  业务后端            |    |   AI 中台 (FastAPI)  |
|  SpringBoot        |    |   Python 3.10+       |
|  +-用户服务         |    |   +- /recognize     |
|  +-商品服务         |    |   +- /generate_card |
|  +-订单服务         |    |   +- /chat           |
|  +-购物车服务       |    |   +- /sentiment      |
|  +-权限服务         |    |   +- /recommend      |
+---------+----------+    +---------+------------+
          |                         |
          |    +--------------------+
          |    | HTTP 内部调用
+---------v----v----------------------------------------------+
|                  数据层                                      |
|  +--------------+  +--------------+  +--------------+      |
|  |   MySQL 8.0  |  |  本地文件    |  | DeepSeek API |      |
|  |  业务数据    |  |  模型 .h5    |  |  (云端)      |      |
|  +--------------+  +--------------+  +--------------+      |
+-------------------------------------------------------------+
```

### 3.2 技术栈选型

| 层级 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **前端** | Vue3 | 3.4+ | 用户端 SPA + 管理端 SPA |
| | Element Plus / Vant | - | PC 端组件库 / 移动端组件库 |
| | Axios | 1.6+ | HTTP 请求拦截、统一错误处理 |
| | Pinia | 2.1+ | 全局状态管理（用户态、购物车） |
| | ECharts | 5.4+ | 管理员数据可视化图表 |
| **业务后端** | SpringBoot | 3.2+ | 主业务 API 服务 |
| | Spring Security | 6.2+ | JWT 认证与 RBAC 权限 |
| | MyBatis-Plus | 3.5+ | ORM 简化 CRUD |
| | Druid | 1.2+ | 数据库连接池 |
| | Log4j2 | 2.22+ | 日志分级记录 |
| **AI 中台** | FastAPI | 0.110+ | 高性能异步 AI 服务 |
| | Uvicorn | 0.27+ | ASGI 服务器 |
| | TensorFlow/Keras | 2.16+ | 加载 Oxford-102 ResNet50 模型 |
| | LangChain | 0.1+ | LLM 调用编排、Prompt 管理、Memory |
| | OpenAI SDK | 1.12+ | 调用 DeepSeek API（兼容 OpenAI 格式） |
| | Pillow | 10+ | 图像预处理 |
| **数据库** | MySQL | 8.0+ | 业务数据持久化 |
| | Redis | 7.0+ | JWT 黑名单、验证码缓存、热点数据（可选） |

### 3.3 服务拆分与通信

| 服务         | 端口      | 职责           | 调用关系              |
| ---------- | ------- | ------------ | ----------------- |
| Vue 前端     | 80/5173 | 页面渲染、路由、状态管理 | 调用 SpringBoot     |
| SpringBoot | 8080    | 业务逻辑、事务、权限   | 调用 FastAPI (内部网络) |
| FastAPI    | 5000    | AI 推理、LLM 代理 | 调用 DeepSeek API   |
| MySQL      | 3306    | 数据存储         | 被 SpringBoot 读写   |

**通信约定**：
- 前端 <-> SpringBoot：RESTful JSON，JWT 放在 Header Authorization: Bearer token
- SpringBoot <-> FastAPI：内部 HTTP（如 http://localhost:5000），无需鉴权（部署时通过 Docker 网络隔离）
-  FastAPI <-> DeepSeek：HTTPS 调用官方 API，API Key 环境变量注入

---

## 4. 数据库设计

### 4.1 E-R 图（文字描述）

```
用户(1) ---< 订单(n) ---> 订单明细(n) ---> 鲜花(n)
  |            |
  |            +--> 收货地址(1)
  |
  +--< 购物车(n) ---> 鲜花(n)
  |
  +--< 评论(n) ---> 鲜花(n)
  |
  +--< 浏览记录(n) ---> 鲜花(n)

鲜花(n) ---> 分类(1)
鲜花(n) ---< 鲜花知识(1)  [AI识花结果映射]

管理员(1) ---< 系统日志(n)
```

### 4.2 表结构设计

#### 4.2.1 用户表 sys_user

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| user_id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(32) | UNIQUE, NOT NULL | 用户名 |
| password | VARCHAR(128) | NOT NULL | BCrypt 加密密码 |
| gender | TINYINT | DEFAULT 0 | 0保密 1男 2女 |
| phone | VARCHAR(16) | - | 手机号 |
| email | VARCHAR(64) | - | 邮箱 |
| avatar | VARCHAR(255) | - | 头像URL |
| role | VARCHAR(16) | DEFAULT 'USER' | ADMIN / USER |
| status | TINYINT | DEFAULT 1 | 0禁用 1正常 |
| create_time | DATETIME | DEFAULT NOW() | 注册时间 |
| update_time | DATETIME | DEFAULT NOW() | 更新时间 |

#### 4.2.2 鲜花表 flower

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| flower_id | BIGINT | PK, AUTO_INCREMENT | 鲜花ID |
| flower_name | VARCHAR(64) | NOT NULL | 花名 |
| category_id | BIGINT | FK | 分类ID |
| price | DECIMAL(10,2) | NOT NULL | 单价 |
| stock | INT | DEFAULT 0 | 库存 |
| cover_image | VARCHAR(255) | - | 封面图 |
| detail_images | JSON | - | 详情图数组 |
| description | TEXT | - | 商品描述（富文本） |
| flower_language | VARCHAR(255) | - | 花语 |
| care_guide | TEXT | - | 养护指南 |
| sales_count | INT | DEFAULT 0 | 销量 |
| status | TINYINT | DEFAULT 1 | 0下架 1上架 |
| create_time | DATETIME | DEFAULT NOW() | - |

#### 4.2.3 鲜花分类表 flower_category

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| category_id | BIGINT | PK | 分类ID |
| category_name | VARCHAR(32) | NOT NULL | 分类名：玫瑰、百合、康乃馨等 |
| parent_id | BIGINT | DEFAULT 0 | 父分类（0为根） |
| sort_order | INT | DEFAULT 0 | 排序 |

#### 4.2.4 购物车表 cart

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| cart_id | BIGINT | PK | ID |
| user_id | BIGINT | FK, NOT NULL | 用户ID |
| flower_id | BIGINT | FK, NOT NULL | 鲜花ID |
| quantity | INT | DEFAULT 1 | 数量 |
| add_time | DATETIME | DEFAULT NOW() | 加入时间 |

#### 4.2.5 订单主表 `order`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| order_id | BIGINT | PK | 订单号（时间戳+随机数） |
| user_id | BIGINT | FK | 用户ID |
| total_amount | DECIMAL(12,2) | NOT NULL | 订单总金额 |
| status | TINYINT | DEFAULT 0 | 0待付款 1已付款 2已发货 3已完成 4已取消 |
| address_id | BIGINT | FK | 收货地址 |
| remark | VARCHAR(255) | - | 订单备注 |
| pay_time | DATETIME | - | 付款时间 |
| deliver_time | DATETIME | - | 发货时间 |
| create_time | DATETIME | DEFAULT NOW() | - |

#### 4.2.6 订单明细表 order_item

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| item_id | BIGINT | PK | ID |
| order_id | BIGINT | FK | 订单号 |
| flower_id | BIGINT | FK | 鲜花ID |
| flower_name | VARCHAR(64) | - | 冗余存储，防止商品改名 |
| price | DECIMAL(10,2) | - | 下单时单价 |
| quantity | INT | - | 数量 |
| subtotal | DECIMAL(12,2) | - | 小计 |

#### 4.2.7 收货地址表 user_address

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| address_id | BIGINT | PK | ID |
| user_id | BIGINT | FK | 用户ID |
| receiver | VARCHAR(32) | - | 收件人 |
| phone | VARCHAR(16) | - | 电话 |
| province | VARCHAR(32) | - | 省 |
| city | VARCHAR(32) | - | 市 |
| district | VARCHAR(32) | - | 区 |
| detail | VARCHAR(255) | - | 详细地址 |
| is_default | TINYINT | DEFAULT 0 | 是否默认 |

#### 4.2.8 鲜花知识表 flower_knowledge（AI识花映射）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| knowledge_id | BIGINT | PK | ID |
| oxford_class_id | INT | UNIQUE | Oxford-102 模型输出的类别ID(0-101) |
| flower_name | VARCHAR(64) | - | 中文花名 |
| flower_name_en | VARCHAR(64) | - | 英文名 |
| flower_language | VARCHAR(255) | - | 花语 |
| care_guide | TEXT | - | 养护知识 |
| story | TEXT | - | 典故/故事 |
| related_flower_ids | JSON | - | 关联站内商品ID数组 |

#### 4.2.9 评论表 review

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| review_id | BIGINT | PK | ID |
| user_id | BIGINT | FK | 用户 |
| flower_id | BIGINT | FK | 鲜花 |
| order_id | BIGINT | FK | 订单 |
| rating | TINYINT | - | 1-5星 |
| content | TEXT | - | 内容 |
| sentiment | VARCHAR(8) | - | AI分析：positive / neutral / negative |
| keywords | VARCHAR(255) | - | AI提取关键词 |
| create_time | DATETIME | DEFAULT NOW() | - |

#### 4.2.10 AI贺卡记录表 ai_card

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| card_id | BIGINT | PK | ID |
| user_id | BIGINT | FK | 用户 |
| flower_id | BIGINT | FK | 鲜花 |
| relation | VARCHAR(16) | - | 关系：妈妈/女友/老师/朋友 |
| occasion | VARCHAR(16) | - | 场合：生日/情人节/母亲节 |
| prompt | TEXT | - | 实际发送给LLM的Prompt |
| generated_content | TEXT | - | AI生成文案 |
| is_used | TINYINT | DEFAULT 0 | 是否被用户采用 |
| create_time | DATETIME | DEFAULT NOW() | - |

---

## 5. 界面设计（页面清单）

### 5.1 用户端（Vue3 + Vant，移动端优先）

| 页面 | 路由 | 功能说明 | 核心交互 |
|------|------|---------|---------|
| **首页** | /home | 轮播图、分类入口、热销榜、猜你喜欢 | 下拉刷新、瀑布流加载 |
| **分类页** | /category | 左侧分类树 + 右侧商品网格 | 点击分类筛选 |
| **商品列表** | /list | 按关键词/分类/价格筛选结果 | 分页、排序（销量/价格/新品） |
| **商品详情** | /detail/:id | 轮播图、价格、花语、养护、评价、加入购物车 | 数量选择、AI客服悬浮窗 |
| **AI识花** | /ai-scan | 拍照/相册上传 -> 显示识别结果 -> 推荐商品 | 调用相机、图片预览、Loading动画 |
| **购物车** | /cart | 商品列表、数量调整、合计、结算 | 左滑删除、全选、实时计算 |
| **订单确认** | /order/confirm | 选择地址、商品清单、优惠券、备注 | 地址选择弹窗 |
| **AI贺卡** | /order/card | 选择关系/场合 -> 一键生成 -> 编辑/复制 | 流式打字机效果展示生成过程 |
| **订单支付** | /order/pay | 模拟支付（课程设计简化） | 倒计时、支付成功跳转 |
| **我的订单** | /order/list | 全部/待付款/已发货/已完成标签页 | 上拉加载、确认收货 |
| **个人中心** | /user | 头像、昵称、订单入口、地址管理、设置 | 登录态判断 |
| **登录** | /login | 用户名密码登录、验证码 | 表单校验、密码显隐切换 |
| **注册** | /register | 用户名、密码、确认密码、性别、验证码 | 实时校验用户名是否存在 |

### 5.2 管理端（Vue3 + Element Plus，PC端）

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| **登录页** | /admin/login | 管理员独立登录入口 |
| **数据看板** | /admin/dashboard | 今日订单、销售额、用户增长、热销排行（ECharts） |
| **鲜花管理** | /admin/flower | 增删改查、上架下架、批量导入 |
| **分类管理** | /admin/category | 分类树形表格、拖拽排序 |
| **订单管理** | /admin/order | 订单列表、筛选、发货、取消 |
| **用户管理** | /admin/user | 用户列表、禁用/启用、查看消费记录 |
| **评价管理** | /admin/review | 评价列表、AI情感标签、回复评价 |
| **AI知识库** | /admin/knowledge | 维护 Oxford-102 映射表（花名、花语、关联商品） |
| **系统日志** | /admin/log | 操作日志、登录日志、异常日志 |

### 5.3 公共组件

| 组件 | 用途 |
|------|------|
| AiChatWidget | 全局悬浮窗，所有页面右下角可唤起智能客服 |
| ImageUploader | 统一图片上传（支持压缩、预览、多图） |
| Captcha | 图形验证码组件 |
| EmptyState | 空状态插画（购物车空、订单空、网络错误） |

---

## 6. 接口设计（RESTful API）

### 6.1 接口规范

- **Base URL**：http://localhost:8080/api/v1
- **请求格式**：JSON，UTF-8
- **响应格式**：统一包装
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": 1715078400000
  }
  ```
- **鉴权**：Header 携带 Authorization: Bearer JWT
- **分页**：?page=1&size=10，返回 { list, total, page, size }

### 6.2 用户模块接口

| 方法 | 路径 | 说明 | 请求体 | 响应 |
|------|------|------|--------|------|
| POST | /user/register | 用户注册 | {username, password, confirmPwd, gender, phone, captcha} | 用户信息 |
| POST | /user/login | 用户登录 | {username, password} | {token, userInfo} |
| GET | /user/info | 获取当前用户信息 | - | 用户详情 |
| PUT | /user/info | 修改用户信息 | {avatar, phone, email} | 更新后对象 |
| PUT | /user/password | 修改密码 | {oldPwd, newPwd} | - |
| GET | /user/address | 查询收货地址列表 | - | 地址数组 |
| POST | /user/address | 新增地址 | 地址对象 | 地址对象 |
| PUT | /user/address/:id | 修改地址 | 地址对象 | 地址对象 |
| DELETE | /user/address/:id | 删除地址 | - | - |

### 6.3 商品模块接口

| 方法 | 路径 | 说明 | 请求参数 |
|------|------|------|---------|
| GET | /flower | 鲜花列表 | ?categoryId&keyword&minPrice&maxPrice&sort&page&size |
| GET | /flower/:id | 鲜花详情 | - |
| GET | /flower/hot | 热销榜单 | ?limit=10 |
| GET | /flower/recommend | 猜你喜欢（基于用户） | Header JWT |
| GET | /category | 全部分类树 | - |

### 6.4 购物车模块接口

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| GET | /cart | 查询购物车 | - |
| POST | /cart | 加入购物车 | {flowerId, quantity} |
| PUT | /cart/:id | 修改数量 | {quantity} |
| DELETE | /cart/:id | 删除商品 | - |
| DELETE | /cart | 清空购物车 | - |

### 6.5 订单模块接口

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| POST | /order | 提交订单 | {addressId, cartIds, remark} |
| POST | /order/pay | 模拟支付 | {orderId} |
| GET | /order | 查询订单列表 | ?status&page&size |
| GET | /order/:id | 订单详情 | - |
| PUT | /order/:id/receive | 确认收货 | - |
| POST | /order/:id/review | 发表评价 | {rating, content} |

### 6.6 管理员模块接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /admin/login | 管理员登录 |
| GET | /admin/dashboard/stats | 统计数据（今日订单数、销售额、新增用户） |
| GET | /admin/dashboard/chart | 图表数据（近7天订单趋势、品类占比） |
| GET | /admin/flower | 鲜花分页列表（含下架商品） |
| POST | /admin/flower | 新增鲜花 |
| PUT | /admin/flower/:id | 修改鲜花 |
| DELETE | /admin/flower/:id | 删除鲜花 |
| GET | /admin/order | 全部订单 |
| PUT | /admin/order/:id/deliver | 发货 |
| GET | /admin/user | 用户列表 |
| PUT | /admin/user/:id/status | 禁用/启用用户 |
| GET | /admin/review | 评价列表 |
| PUT | /admin/review/:id/reply | 回复评价 |

---

## 7. AI 服务设计（FastAPI 层）

### 7.1 FastAPI 服务架构

```
fastapi_ai/
├── main.py              # 应用入口、路由聚合
├── config.py            # 配置（DeepSeek API Key、模型路径）
├── models/
│   └── flower_recognition.py   # ResNet50 模型加载与推理
├── services/
│   ├── llm_service.py     # LangChain + DeepSeek 封装
│   ├── card_service.py    # 贺卡生成业务
│   ├── chat_service.py    # 客服对话业务
│   └── sentiment_service.py  # 情感分析
├── routers/
│   ├── recognize.py       # /recognize 识花接口
│   ├── generate.py        # /generate_card 贺卡接口
│   ├── chat.py            # /chat 客服接口
│   └── sentiment.py       # /sentiment 情感分析接口
├── prompts/
│   ├── card_prompt.txt    # 贺卡 Prompt 模板
│   └── chat_prompt.txt    # 客服 Prompt 模板
└── requirements.txt
```

### 7.2 视觉识别接口（本地模型）

**端点**：POST http://localhost:5000/recognize

**请求**：
```http
Content-Type: multipart/form-data

image: <图片文件>
top_k: 5  (可选，默认5)
```

**响应**：
```json
{
  "code": 200,
  "data": {
    "predictions": [
      {
        "class_id": 12,
        "class_name_en": "Rose",
        "class_name_cn": "玫瑰",
        "confidence": 0.9432,
        "flower_language": "爱情、热烈",
        "related_products": [1001, 1002, 1003]
      },
      {
        "class_id": 45,
        "class_name_en": "Carnation",
        "class_name_cn": "康乃馨",
        "confidence": 0.0312,
        "flower_language": "母爱、感恩",
        "related_products": [2001]
      }
    ]
  }
}
```

**实现要点**：
- 启动时加载 flower_model.h5（ResNet50，Oxford-102）
- 图片预处理：resize 224x224，归一化，expand_dims
- 通过 flower_knowledge 表映射 class_id -> 中文名、花语、关联商品
- 推理耗时控制在 500ms 以内（CPU）

### 7.3 大语言模型调用（DeepSeek-V4）

#### 7.3.1 配置与连接

DeepSeek API 兼容 OpenAI 协议，使用 openai Python SDK 或 LangChain 的 ChatOpenAI 类：

```python
# config.py
import os

DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY")
DEEPSEEK_BASE_URL = "https://api.deepseek.com/v1"
DEEPSEEK_MODEL = "deepseek-chat"  # V4 对应模型名

# 本地模型路径
MODEL_PATH = "./models/flower_model.h5"
CLASS_NAMES_PATH = "./models/class_names.json"
```

```python
# services/llm_service.py
from langchain_openai import ChatOpenAI
from langchain.memory import ConversationBufferMemory
from langchain.chains import LLMChain
from langchain.prompts import PromptTemplate

llm = ChatOpenAI(
    model_name="deepseek-chat",
    openai_api_key=DEEPSEEK_API_KEY,
    openai_api_base=DEEPSEEK_BASE_URL,
    temperature=0.7,
    max_tokens=512,
    request_timeout=10
)

# 共享 Memory（实际生产应每个用户独立）
memory = ConversationBufferMemory(
    memory_key="chat_history",
    return_messages=True
)
```

#### 7.3.2 AI 贺卡生成接口

**端点**：POST http://localhost:5000/generate_card

**请求**：
```json
{
  "flower_name": "玫瑰",
  "relation": "女友",
  "occasion": "情人节",
  "style": "浪漫",
  "length": 50
}
```

**LangChain Prompt 模板**（prompts/card_prompt.txt）：
```text
你是一位精通花语与礼仪的资深花艺师。
用户想在 {occasion} 送 {flower_name} 给 {relation}，希望文案风格为 {style}。
请生成一段 {length} 字左右的贺卡文案，要求：
1. 融入该花的花语寓意；
2. 贴合收花人身份与节日氛围；
3. 语言优美、真挚，避免俗套；
4. 不要出现此致敬礼等公文格式。

直接输出文案内容，不要加任何解释。
```

**服务层实现**：
```python
# services/card_service.py
from langchain.prompts import PromptTemplate
from services.llm_service import llm

card_prompt = PromptTemplate.from_template(open("prompts/card_prompt.txt").read())

def generate_card(flower_name: str, relation: str, occasion: str, style: str = "浪漫", length: int = 50):
    chain = card_prompt | llm
    result = chain.invoke({
        "flower_name": flower_name,
        "relation": relation,
        "occasion": occasion,
        "style": style,
        "length": length
    })
    return result.content
```

**响应**：
```json
{
  "code": 200,
  "data": {
    "generated_content": "亲爱的，这束玫瑰代表我对你炽热而坚定的爱。每一个花瓣都藏着我想对你说的话——遇见你，是我这辈子最美的意外。情人节快乐，我的女孩。",
    "model": "deepseek-chat",
    "tokens_used": 128
  }
}
```

#### 7.3.3 智能花语客服接口

**端点**：POST http://localhost:5000/chat

**请求**：
```json
{
  "session_id": "user_123_456",
  "message": "玫瑰适合送什么人？"
}
```

**LangChain 实现（带上下文记忆）**：
```python
# services/chat_service.py
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationChain
from langchain.prompts import ChatPromptTemplate, MessagesPlaceholder

# 每个 session_id 对应一个 Memory（生产环境用 Redis 存储）
session_memories = {}

system_prompt = '''你是面智花语鲜花商城的智能花艺顾问，精通各类鲜花的花语、养护知识、送礼礼仪。
规则：
1. 回答简洁友好，控制在100字以内；
2. 如果用户询问的商品在商城有售，主动推荐并给出链接；
3. 遇到非鲜花问题，礼貌引导回鲜花话题；
4. 不知道的问题诚实回答，不编造。
'''

prompt = ChatPromptTemplate.from_messages([
    ("system", system_prompt),
    MessagesPlaceholder(variable_name="history"),
    ("human", "{input}")
])

def chat(session_id: str, message: str):
    if session_id not in session_memories:
        session_memories[session_id] = ConversationBufferMemory(return_messages=True)

    memory = session_memories[session_id]
    chain = ConversationChain(llm=llm, memory=memory, prompt=prompt)
    response = chain.predict(input=message)
    return response
```

**响应**：
```json
{
  "code": 200,
  "data": {
    "reply": "玫瑰经典花语是爱情与热烈，最适合送给恋人、伴侣表达爱意。不同颜色也有细分：红玫瑰代表热恋，粉玫瑰代表初恋，白玫瑰代表纯洁。我们商城有多种玫瑰礼盒，需要我推荐几款吗？",
    "session_id": "user_123_456"
  }
}
```

#### 7.3.4 评论情感分析接口

**端点**：POST http://localhost:5000/sentiment

**请求**：
```json
{
  "text": "花很新鲜，送货很快，女朋友很喜欢！"
}
```

**Prompt 设计**：
```text
分析以下用户评论的情感倾向，只输出 JSON：{"sentiment": "positive/neutral/negative", "keywords": ["新鲜", "快", "喜欢"], "score": 0.95}
评论：{text}
```

**响应**：
```json
{
  "code": 200,
  "data": {
    "sentiment": "positive",
    "keywords": ["新鲜", "快", "喜欢"],
    "score": 0.95
  }
}
```

### 7.4 降级策略

当 DeepSeek API 超时或异常时：

| 功能 | 降级方案 |
|------|---------|
| AI贺卡 | 返回预置模板库中的文案（按花种+关系匹配） |
| 智能客服 | 返回客服忙，请拨打 400-xxx + 常见问题链接 |
| 情感分析 | 基于关键词规则判断（含好/喜欢/棒->positive，含差/慢/烂->negative） |

---

## 8. SpringBoot <-> FastAPI 调用示例

### 8.1 Java 端 AI 服务客户端

```java
@Service
public class AiServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String AI_BASE_URL = "http://localhost:5000";

    // 1. 调用识花服务
    public FlowerRecognitionResult recognizeFlower(MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new MultipartFileResource(image));
        body.add("top_k", 5);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<AiResponse> response = restTemplate.postForEntity(
            AI_BASE_URL + "/recognize", request, AiResponse.class);
        return response.getBody().getData();
    }

    // 2. 调用贺卡生成
    public String generateCard(String flowerName, String relation, String occasion) {
        Map<String, Object> request = Map.of(
            "flower_name", flowerName,
            "relation", relation,
            "occasion", occasion,
            "style", "浪漫",
            "length", 50
        );
        AiResponse response = restTemplate.postForObject(
            AI_BASE_URL + "/generate_card", request, AiResponse.class);
        return response.getData().getGeneratedContent();
    }

    // 3. 调用客服对话
    public String chatWithAi(String sessionId, String message) {
        Map<String, Object> request = Map.of(
            "session_id", sessionId,
            "message", message
        );
        AiResponse response = restTemplate.postForObject(
            AI_BASE_URL + "/chat", request, AiResponse.class);
        return response.getData().getReply();
    }
}
```

### 8.2 统一响应体封装

```java
@Data
public class AiResponse<T> {
    private Integer code;
    private String message;
    private T data;
}
```

---

## 9. 项目进度与分工

| 周次  | 日期   | 阶段    | 任务                                                         | 负责人            |
| --- | ---- | ----- | ---------------------------------------------------------- | -------------- |
| 第1周 | 5.7  | 需求+设计 | 需求分析、数据库设计、接口设计、AI方案确认                                     | 全员             |
| 第2周 | 5.14 | 基础开发  | SpringBoot 用户/权限/商品/购物车 API；Vue 基础页面                       | A:后端 B:前端 C:AI |
| 第3周 | 5.21 | AI中台  | FastAPI 搭建；ResNet50 模型本地部署；DeepSeek 联调；LangChain Prompt 设计 | C为主，A配合        |
| 第4周 | 5.28 | 前后端联调 | 订单流程打通；AI识花/贺卡/客服接入前端；移动端适配；管理员后台                          | B为主，全员联调       |
| 第5周 | 6.4  | 测试+报告 | 系统测试、Bug修复、性能优化、报告撰写、答辩PPT                                 | 全员             |

---

## 10. 答辩演示要点

1. **传统电商 vs AI电商**：对比图展示拍照识花、智能贺卡、客服机器人
2. **技术架构图**：强调微服务分层、AI中台独立、前后端分离
3. **现场演示**：
   - 手机拍照上传 -> 识别玫瑰 -> 跳转商品页
   - 结算页选择送女友+情人节 -> 生成贺卡文案
   - 右下角客服窗问百合怎么养 -> AI回答养护知识
4. **数据看板**：管理员后台展示 ECharts 订单统计图
5. **创新分陈述**：明确说出本系统在传统鲜花电商基础上，引入了计算机视觉与大语言模型两项AI技术，实现了...

---

## 附录

### A. 技术参考
- DeepSeek API 文档：https://platform.deepseek.com/
- LangChain 文档：https://python.langchain.com/
- Oxford-102 数据集：https://www.robots.ox.ac.uk/~vgg/data/flowers/102/
- 预训练模型来源：darkangrycoder/102_oxford_flower_prediction

### B. 环境要求
- Java 17+
- Python 3.10+
- Node.js 18+
- MySQL 8.0
- 模型文件：flower_model.h5（约 100MB，需提前下载）
