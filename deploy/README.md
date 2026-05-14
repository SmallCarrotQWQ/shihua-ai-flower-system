# Deploy 本地运行说明

本目录用于保存部署脚本、端口说明和后续 Docker 配置。

## 本地端口

```text
Frontend:   5173
SpringBoot: 8080
FastAPI:    5000
MySQL:      3306
Redis:      6379
```

## 推荐启动顺序

1. MySQL
2. Redis，可选
3. FastAPI
4. SpringBoot
5. Frontend

## 常见端口冲突

查看 8080 是否被占用：

```powershell
netstat -ano | findstr :8080
```

结束进程需要管理员 PowerShell：

```powershell
taskkill /PID <pid> /F
```

如果不想结束进程，也可以临时换端口：

```properties
SERVER_PORT=8081
```

前端也要同步修改：

```properties
VITE_API_BASE_URL=http://localhost:8081/api/v1
```

## 后续可补充

- `docker-compose.yml`
- Nginx 配置
- Redis 配置
- MySQL 初始化挂载
- 生产环境环境变量模板

