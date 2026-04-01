# com-chengq-boot

Vue3 + Spring Boot 的后台管理示例：登录鉴权、用户/角色/菜单、园区与权限绑定等。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3、Vue Router、Axios、Vite |
| 后端 | Spring Boot 2.7、Spring Security、MyBatis-Plus |
| 鉴权 | JWT（`/auth/*`） |
| API 文档 | SpringDoc / Swagger（`/swagger`） |
| 缓存 / 锁 | Redis、Redisson（可选，见 `application.yml`） |
| 定时任务 | XXL-Job 3.3.2（本仓库为 **Executor**，Admin 需单独部署） |

环境：JDK 8+、Maven、Node.js、MySQL。

## 使用

### 1. 初始化数据库

在 MySQL 中执行 **`sql/init_database.sql`**（建库、建表、初始数据）。

数据源见 **`com-chengq-app/src/main/resources/application.yml`**（默认库 `com_chengq`，账号 `root` / `root123456`，按本机修改）。

### 2. 启动后端

在项目根目录：

```bash
mvn -q -pl com-chengq-api install -DskipTests
cd com-chengq-app
mvn spring-boot:run
```

- 服务：`http://localhost:8081/`
- Swagger：`http://localhost:8081/swagger`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 **`http://localhost:3000/`**（端口见 `frontend/vite.config.js`）。

### 默认登录

脚本初始化后仅有 **admin**：手机号 **`17688888888`**，密码 **`123456`**（登录填手机号）。

---

## XXL-Job（可选）

本项目集成 **xxl-job-core**，作为 **执行器（Executor）** 接入调度中心；**调度中心（xxl-job-admin）需自行下载部署**，与业务应用分离。

1. **配置**：`com-chengq-app/src/main/resources/application.yml` 中 `xxl.job`  
   - `admin.addresses`：指向你的 admin 地址（默认示例 `http://localhost:8080/xxl-job-admin`）；留空则本应用不向 admin 注册。  
   - `accessToken` 与 admin 侧保持一致（示例为 `default_token`）。  
   - `executor.appname`：在 admin 里注册的执行器名称（默认 `com-chengq-executor`）。

2. **启动顺序**：先启动 **xxl-job-admin**，再启动 **com-chengq-app**，执行器会自动注册到 admin。

3. **示例任务**：`com.chengq.app.jobhandler.XxlJobDemoJobHandler`  
   - 在 admin 新建任务时，**JobHandler** 填 **`demoJobHandler`**，路由到本执行器后可在控制台与 `logs/xxl-job` 下查看执行日志。

4. **版本**：与根 `pom.xml` 中 **`xxl.job.version`（3.3.2）** 保持一致，避免 admin 与 executor 主版本混用。

Redis 等其它可选组件同样见 **`application.yml`**。
