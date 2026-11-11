# SaaS 多租户知识库管理系统

企业级 Java 后端项目：知识库/文档/标签三类资源的 CRUD + 分页检索，强制租户隔离与防越权。

## 技术栈

Spring Boot 3.4.5 · Java 17 · MyBatis-Plus · MySQL 8 · springdoc · Docker Compose · JUnit 5

## 亮点

- **多租户隔离**：所有查询强制携带 tenantId 条件，更新/删除前先做归属校验，防止越权访问
- **工程化规范**：雪花 ID、逻辑删除、字段自动填充、分页上下限约束、统一返回体、全局异常处理
- **部署能力**：Dockerfile + docker-compose（MySQL healthcheck 依赖编排）
- **测试**：单元测试覆盖分页与租户隔离场景

## 运行

```bash
# 方式一：本地运行（需 MySQL）
mvn spring-boot:run

# 方式二：Docker Compose 一键启动（含 MySQL）
docker compose up -d
```

## 面试要点

1. 多租户隔离方案对比：字段隔离 vs 独立 Schema vs 独立库，何时演进到分库分表
2. 为什么更新/删除前要做归属校验（只靠查询条件隔离不够，绕过场景）
3. 雪花 ID 的组成与时钟回拨问题
