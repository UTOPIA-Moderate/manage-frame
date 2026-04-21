# manage-frame

企业级中后台管理框架（Spring Cloud 微服务版）

## 项目结构

```
manage-frame/
├── manage-common/       # 公共模块（实体/工具/异常/枚举）
├── manage-gateway/       # API 网关 (8080)
├── manage-auth/          # 认证服务 (8081)
├── manage-system/        # 系统服务 (8082)
├── manage-job/          # 定时任务服务 (8083)
├── manage-file/         # 文件服务 (8084)
└── docs/
    ├── sql/schema.sql   # 数据库初始化脚本
    ├── 需求文档.md
    └── 后端接口定义.md
```

## 技术栈

| 技术 | 版本 |
|------|------|
| Spring Boot | 3.2.0 |
| Spring Cloud | 2023.0.0 |
| MyBatis-Plus | 3.5.5 |
| PostgreSQL | - |
| Nacos | - |
| Open Feign | - |
| JWT | 0.12.3 |
| MinIO | 8.5.7 |
| XXL-Job | 2.4.0 |

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| NACOS_HOST | localhost | Nacos 地址 |
| NACOS_NAMESPACE | public | Nacos 命名空间 |
| NACOS_GROUP | DEFAULT_GROUP | Nacos 分组 |
| DB_HOST | localhost | 数据库地址 |
| DB_NAME | xxx | 数据库名（各服务不同） |
| DB_USERNAME | postgres | 数据库用户名 |
| DB_PASSWORD | postgres | 数据库密码 |
| REDIS_HOST | localhost | Redis 地址 |
| REDIS_PORT | 6379 | Redis 端口 |

## 快速启动

### 1. 环境准备

- JDK 17+
- Maven 3.8+
- PostgreSQL 14+
- Nacos 2.x
- MinIO（可选）

### 2. 数据库初始化

```bash
psql -U postgres -f docs/sql/schema.sql
```

### 3. Nacos 配置

在 Nacos 中创建以下配置文件（命名空间: manage-frame）：

- `manage-log.yml` - 日志配置
- `manage-swagger.yml` - Swagger 配置
- `manage-biz.yml` - 业务公共配置
- `manage-auth-jwt.yml` - JWT 配置
- `manage-job-xxljob.yml` - XXL-Job 配置
- `manage-file-storage.yml` - 文件存储配置

### 4. 编译

```bash
mvn clean install -DskipTests
```

### 5. 启动服务

```bash
# 启动网关
java -jar manage-gateway/target/manage-gateway-1.0.0.jar

# 启动认证服务
java -jar manage-auth/target/manage-auth-1.0.0.jar

# 启动系统服务
java -jar manage-system/target/manage-system-1.0.0.jar

# 启动定时任务服务
java -jar manage-job/target/manage-job-1.0.0.jar

# 启动文件服务
java -jar manage-file/target/manage-file-1.0.0.jar
```

### 6. Docker Compose（可选）

```yaml
version: '3.8'
services:
  nacos:
    image: nacos/nacos-server:v2.2.3
    environment:
      MODE: standalone
    ports:
      - "8848:8848"

  postgres:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - ./docs/sql/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  minio:
    image: minio/minio:latest
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

## API 路由

| 路径 | 服务 | 端口 |
|------|------|------|
| `/api/auth/*` | manage-auth | 8081 |
| `/api/system/*` | manage-system | 8082 |
| `/api/job/*` | manage-job | 8083 |
| `/api/file/*` | manage-file | 8084 |

## API 文档

启动服务后访问 Swagger UI：
- http://localhost:8081/swagger-ui.html (auth)
- http://localhost:8082/swagger-ui.html (system)
- http://localhost:8083/swagger-ui.html (job)
- http://localhost:8084/swagger-ui.html (file)

## 默认账号

- 用户名: `admin`
- 密码: `admin123`（需在 Nacos 中配置 JWT 密钥后生效）
