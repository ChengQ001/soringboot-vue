# com-chengq-boot（系统管理 Demo）

Vue3 + Spring Boot 的后台管理系统示例，包含登录鉴权、系统管理（用户/角色/菜单）、权限绑定（角色-菜单、用户-角色）以及用户个人信息/修改密码/退出登录。

## 技术栈

- 前端：Vue 3 + Vue Router + Axios + Vite
- 后端：Spring Boot 2.7.x + Spring Security + MyBatis-Plus
- 鉴权：JWT（无状态），登录/退出接口在 `/auth/*`
- 文档：Swagger（`/swagger`）

## 快速开始

### 1. 初始化数据库

使用根目录 `init_database.sql` 作为全量初始化脚本（建库/建表/插入初始数据/默认绑定）。

数据库连接配置在 `com-chengq-app/src/main/resources/application.yml`：

- MySQL：`jdbc:mysql://localhost:3306/com_chengq?...`
- 用户名/密码：`root / root123456`

初始化后默认账户如下（密码固定：`123456`，登录使用手机号）：

- admin：手机号 `13800138000`
- user ：手机号 `13900139000`

### 2. 启动后端

在项目根目录执行：

```bash
mvn -q -pl com-chengq-api install -DskipTests
cd com-chengq-app
mvn spring-boot:run
```

- 后端地址：`http://localhost:8081/`
- Swagger：`http://localhost:8081/swagger`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

- 前端地址：`http://localhost:3000/`

## 功能概览

- 登录/注册
  - 登录：手机号 + 密码
  - 注册：用户名 + 手机号 + 密码（校验手机号格式）
- 系统管理
  - 用户管理、角色管理、菜单管理
  - 绑定页：角色-菜单绑定、用户-角色绑定
- 用户中心（右上角下拉）
  - 个人信息：显示用户名、手机号、以及“自己的角色”
  - 修改密码：保存成功后会自动退出登录
  - 退出登录：调用 `/auth/logout` 并清理本地 token

## 维护说明

- 所有 Toast 弹窗提示都走全局 `frontend/src/utils/flash.js` + `FlashToast.vue`
- 用户角色展示依赖 `tb_user_role`：
  - `GET /api/users/current-user`（后端 `/users/current-user`）会把角色填充到 `UserDetails` 中
- 数据库初始化脚本已整合为单文件：`init_database.sql`

## Redis / Redisson（分布式锁）

### 1. Redis 配置位置

Redis 配置在 `com-chengq-app/src/main/resources/application.yml`：

- `spring.redis.host`：默认 `127.0.0.1`
- `spring.redis.port`：默认 `6379`
- `spring.redis.database`：默认 `0`（即 `db0`）

`RedisTool`/`RedissonLockTool` 都复用这些配置连接 Redis。

### 2. Redis 基础封装

- `com-chengq-app/src/main/java/com/chengq/app/util/RedisTool.java`

常用方法示例：

```java
// set/get
redisTool.set("k1", "v1");
String v = redisTool.get("k1", String.class);

// set + TTL
redisTool.set("k2", "tmp", Duration.ofSeconds(30));

// hash
redisTool.hSet("hash1", "field1", "v1");
String hv = redisTool.hGet("hash1", "field1", String.class);
```

### 3. 分布式锁（Redisson）

- Maven 依赖：`org.redisson:redisson`
- 客户端配置：`com-chengq-app/src/main/java/com/chengq/app/config/RedissonConfig.java`
- 锁工具封装：`com-chengq-app/src/main/java/com/chengq/app/util/RedissonLockTool.java`

推荐用法：不传等待/租约时间，直接自动加锁并 finally 解锁：

```java
redissonLockTool.withLock(
  "lock:order:submit:" + orderId,
  () -> {
    // 临界区：同一 lockKey 只能串行执行
  }
);
```

> lockKey 粒度建议按业务维度拼接（例如：`userId/menuId/orderId`），避免“全局一把锁”导致性能下降。

## XXL-Job 后台访问（Admin）

本项目集成的是 **XXL-JOB 3.3.2**：
- `xxl-job-admin`：后台调度中心
- `com-chengq-app`：作为 executor（执行器）接收并执行任务

### 1. 先启动 xxl-job（Admin）
进入你本机的 `xxl-job` 目录（你说的“`xxl-job` 文件下面有启动项”），直接运行里面的启动脚本/启动项（启动 `xxl-job-admin`）。

启动完成后即可访问后台。

### 2. 访问后台
打开浏览器访问：
`http://localhost:8080/xxl-job-admin/#/xxl-job-admin/dashboard`

如果你 admin 端口不是 `8080`，把 URL 里的端口替换成实际端口即可。

### 3. 启动 executor（com-chengq-app，可选但建议）
启动 `com-chengq-app` 后，它会自动作为 executor 注册。

本项目内置示例 handler：
`demoJobHandler`

日志输出位置（executor 日志 + XXL-Job 任务日志）：
`com-chengq-boot/logs/`（其中 XXL-Job 任务日志在 `logs/xxl-job/` 下，按日期/任务号生成文件）

在 admin 里创建任务时，`ExecutorHandler` 填 `demoJobHandler`，然后点击“立即执行”。

验证执行是否成功：查看 `com-chengq-app` 控制台日志是否打印 `XXL-Job demoJobHandler executed.`

