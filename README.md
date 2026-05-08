# Gym Management System (健身房管理系统)

"基于 SpringBoot 3 + Vue 3 的现代化健身房业务管理系统，集成了会员、教练、课程、器材及签到管理的全栈解决方案。"

## ✨ 原始需求

> 请你帮我再当前目录下写一个健身房管理系统的前后端分离项目，看看文件结构创建全了没，技术栈使用Java spring boot mybatiesplus and vue3 elementplus。软件工程流程方法。

## 🚀 运行步骤

本系统完全 Docker 化，支持一键启动。

### 前置条件

- Docker Desktop (或 Docker Engine + Docker Compose)

### 1. 生产环境 (Pure Mode)

适用于正式部署，数据库初始化后为空（仅包含默认管理员）。

```bash
# 启动
docker compose up
```

### 2. 访问系统

- **前端页面**: [http://localhost:3000](http://localhost:3000)
- **后端 API**: [http://localhost:8080](http://localhost:8080)
- **默认账号**: `admin` / `admin123`

## 🏗 代码架构

采用经典的前后端分离架构：

### 前端架构 (Frontend)

- **核心栈**: Vue 3 (Composition API) + Vite + JavaScriipt
- **UI 组件库**: Element Plus (按需引入)
- **路由管理**: Vue Router 4
- **网络请求**: Axios (封装了拦截器，统一处理 Token 和异常)
- **开发规范**: ESLint + Prettier

### 后端架构 (Backend)

- **核心栈**: Spring Boot 3 + Java 17
- **持久层**: MyBatis-Plus + MySQL 8.4
- **鉴权机制**: JWT (JSON Web Token) 无状态认证
- **分层设计**:
  - `Controller`: 处理 HTTP 请求与参数校验 (@Valid)
  - `Service`: 核心业务逻辑实现
  - `Entity/Mapper`: 数据库实体映射与操作
  - `Common`: 全局异常处理 (GlobalExceptionHandler) 与统一响应封装 (ApiResponse)

### 基础设施 (Infrastructure)

- **反向代理**: Nginx (托管静态资源 + 转发 /api 请求)
- **容器化**: Docker + Docker Compose (多环境配置管理)

## 🛠 工程细节

1. **数据库初始化策略**:
   - `backend/scripts/init-db.sql`: 负责 Schema 定义 (CREATE TABLE)，在所有环境执行。
   - `backend/scripts/seed-data.sql`: 负责测试数据注入 (INSERT)，仅在 Dev 环境挂载执行。
   - **执行顺序**: 利用 Docker `docker-entrypoint-initdb.d` 的字母序加载机制，`init-db.sql` 优先于 `init-seed.sql` (挂载名) 执行。

2. **字符集处理**:
   - 数据库容器通过 `--character-set-server=utf8mb4` 强制使用 UTF8MB4，彻底解决中文乱码问题。
   - 初始化脚本显式声明 `SET NAMES utf8mb4;`。

3. **快照设计**:
   - `member_checkin` 表冗余存储 `member_name`，避免因会员被删除导致签到记录查询报错，符合审计要求。

4. **交互优化**:
   - 所有 ID 输入框升级为远程搜索/下拉选择，提升操作效率。
   - 表格列宽与对齐方式经过微调，适配不同屏幕密度。
