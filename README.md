# com-chengq-boot

基于 `Vue3 + Spring Boot` 的后台管理示例，覆盖登录鉴权、用户/角色/菜单管理、园区维度权限绑定等常见能力。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3、Vue Router、Axios、Vite |
| 后端 | Spring Boot 2.7、Spring Security、MyBatis-Plus |
| 鉴权 | JWT（`/auth/*`） |
| API 文档 | SpringDoc / Swagger（`/swagger`） |
| 缓存 / 锁 | Redis、Redisson（可选） |
| 消息队列 | RabbitMQ（可选，配置见 `application.yml`） |
| 定时任务 | XXL-Job 3.3.2（本仓库为 Executor，Admin 需单独部署） |

## 环境要求

- JDK 17
- Maven 3.8+
- Node.js 18+
- MySQL 8.x
- （可选）Redis 6+
- （可选）RabbitMQ 3.x

## 快速开始

### 1）初始化数据库

在 MySQL 中执行 `sql/init_database.sql`（建库、建表、初始数据）。

默认数据源配置位于 `com-chengq-app/src/main/resources/application.yml`：

- 库名：`com_chengq`
- 用户：`root`
- 密码：`root123456`

请按本机环境修改。

### 2）启动后端

在项目根目录执行：

```bash
mvn -q -pl com-chengq-api install -DskipTests
cd com-chengq-app
mvn spring-boot:run
```

- 应用地址：`http://localhost:8081/`
- Swagger：`http://localhost:8081/swagger`

### 3）启动前端（开发模式）

```bash
cd frontend
npm install
npm run dev
```

访问：`http://localhost:3000/`

### 4）默认账号

初始化脚本仅内置一个管理员账号：

- 手机号：`17688888888`
- 密码：`123456`

> 登录使用手机号。

## 前端部署说明（重要）

前端路由已切换为 `Hash` 模式（如 `/#/admin/menus`），主要用于降低云服务器静态部署复杂度：

- 仅上传 `frontend/dist` 到静态站点目录即可；
- 不依赖 Nginx `try_files ... /index.html` 的 history 回退配置；
- 若后续想改回无 `#` 路径，再切回 `createWebHistory` 并补齐 Nginx 回退规则。

同时请确保反向代理将 `/api` 转发到后端 `8081`。

## RabbitMQ（可选）

`application.yml` 中已预留 RabbitMQ 配置段（当前注释状态），按需开启：

```yml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
    virtual-host: /
```

建议先本地确认 RabbitMQ 可连通，再启动后端观察日志。

## XXL-Job（可选）

项目集成 `xxl-job-core`，作为执行器接入调度中心。`xxl-job-admin` 需独立部署。

1. 配置：`application.yml` 的 `xxl.job`
   - `admin.addresses`：你的 admin 地址（示例 `http://localhost:8080/xxl-job-admin`）
   - `accessToken`：与 admin 保持一致
   - `executor.appname`：执行器注册名（默认 `com-chengq-executor`）
2. 启动顺序：先启动 `xxl-job-admin`，再启动 `com-chengq-app`
3. 示例任务：`com.chengq.app.jobhandler.XxlJobDemoJobHandler`
   - 在 admin 新建任务时，`JobHandler` 填 `demoJobHandler`

## 常见问题

- 登录后页面不跳转/刷新 404：优先确认是否部署了最新前端包（含 Hash 路由改造）。
- 接口 401 自动回登录：检查 token 是否过期、后端是否可用、浏览器本地存储是否被清空。
- XXL-Job 注册报错：若本地未启动 admin，日志提示 `Connection refused` 属预期。
