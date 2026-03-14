# Alibaba Druid

[![Java CI](https://img.shields.io/github/actions/workflow/status/alibaba/druid/ci.yaml?branch=master&logo=github&logoColor=white)](https://github.com/alibaba/druid/actions/workflows/ci.yaml)
[![Codecov](https://img.shields.io/codecov/c/github/alibaba/druid/master?logo=codecov&logoColor=white)](https://codecov.io/gh/alibaba/druid/branch/master)
[![Maven Central](https://img.shields.io/maven-central/v/com.alibaba/druid?logo=apache-maven&logoColor=white)](https://search.maven.org/artifact/com.alibaba/druid)
[![Last SNAPSHOT](https://img.shields.io/nexus/snapshots/https/oss.sonatype.org/com.alibaba/druid?label=latest%20snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/alibaba/druid/)
[![GitHub release](https://img.shields.io/github/release/alibaba/druid)](https://github.com/alibaba/druid/releases)
[![License](https://img.shields.io/github/license/alibaba/druid?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)

[English](README_EN.md) | 中文

---

**Druid** 是阿里巴巴开源的高性能数据库连接池和 SQL 解析器。它将 JDBC 连接池、SQL 解析分析、安全防护和监控统计深度整合为一体，是 Java 生态中功能最全面的数据库中间件之一。

## 核心特性

| 功能领域 | 说明 |
|---------|------|
| **JDBC 连接池** | 高性能、可监控的连接池实现 `DruidDataSource`，支持物理连接预热、PSCache、KeepAlive 等高级特性 |
| **SQL 解析器** | 支持 30 种 SQL 方言的完整解析器，生成 AST 抽象语法树，支持 SQL 格式化、改写和分析 |
| **SQL 防火墙** | 基于 AST 的 `WallFilter` SQL 安全防护，可拦截 SQL 注入、危险操作等 |
| **监控统计** | 内置 `StatFilter` 实时采集 SQL 执行统计、连接池状态，提供 Web 监控页面 |
| **Filter 扩展** | 可插拔的 Filter-Chain 架构，支持日志、加密、统计等扩展 |
| **Spring Boot 集成** | 提供 Spring Boot 2.x / 3.x / 4.x Starter，开箱即用 |
| **高可用** | `HighAvailableDataSource` 支持多数据源负载均衡、健康检查和故障切换 |

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.24</version>
</dependency>
```

### Spring Boot 项目（推荐）

根据 Spring Boot 版本选择对应 Starter：

| Spring Boot 版本 | Starter |
|-----------------|---------|
| 2.x | `druid-spring-boot-starter` |
| 3.x | `druid-spring-boot-3-starter` |
| 4.x | `druid-spring-boot-4-starter` |

```xml
<!-- Spring Boot 3.x 示例 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-3-starter</artifactId>
    <version>1.2.24</version>
</dependency>
```

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    druid:
      initial-size: 5
      max-active: 20
      min-idle: 5
      max-wait: 60000
      # 启用 Filter
      filter:
        stat:
          enabled: true
          log-slow-sql: true
          slow-sql-millis: 2000
        wall:
          enabled: true
```

### 直接使用 DruidDataSource

```java
DruidDataSource dataSource = new DruidDataSource();
dataSource.setUrl("jdbc:mysql://localhost:3306/mydb");
dataSource.setUsername("root");
dataSource.setPassword("password");
dataSource.setInitialSize(5);
dataSource.setMaxActive(20);
dataSource.setMinIdle(5);
dataSource.init();

// 使用连接
try (Connection conn = dataSource.getConnection()) {
    // 执行 SQL
}
```

### SQL 解析器

```java
// 解析 SQL
String sql = "SELECT id, name FROM users WHERE age > 18 ORDER BY name";
List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);

// 格式化 SQL
String formatted = SQLUtils.format(sql, DbType.mysql);

// 获取表信息
SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
stmts.get(0).accept(visitor);
System.out.println("Tables: " + visitor.getTables());
System.out.println("Columns: " + visitor.getColumns());
```

## 项目模块

```
druid/
├── core/                           # 核心库（连接池、SQL 解析、安全、监控）
├── druid-spring-boot-starter/      # Spring Boot 2.x 自动配置
├── druid-spring-boot-3-starter/    # Spring Boot 3.x 自动配置
├── druid-spring-boot-4-starter/    # Spring Boot 4.x 自动配置
├── druid-wrapper/                  # 包装工具
├── druid-admin/                    # 集群监控管理端
└── doc/                            # 文档
```

## SQL 方言支持

Druid SQL 解析器支持 30 种数据库方言，每种方言都提供完整的 Lexer、Parser、AST 和 Visitor 实现：

| 分类 | 支持的数据库 |
|------|------------|
| **主流关系型** | MySQL, PostgreSQL, Oracle, SQL Server, DB2, H2, Informix |
| **国产数据库** | 达梦 (DM), Oscar, GaussDB |
| **分析型/MPP** | ClickHouse, Doris, StarRocks, Teradata, Redshift |
| **云原生/数仓** | BigQuery, Snowflake, Synapse, Hologres, ODPS (MaxCompute) |
| **计算引擎** | Hive, Spark, Presto, Impala, Athena, Blink, Databricks |
| **其他** | Phoenix, SuperSQL, Transact-SQL |

## 文档

| 文档 | 说明 |
|------|------|
| [架构概览](doc/architecture.md) | 整体架构设计与核心组件交互 |
| [连接池指南](doc/connection-pool-guide.md) | DruidDataSource 配置、调优与最佳实践 |
| [SQL 解析器指南](doc/sql-parser-guide.md) | SQL 解析、格式化、改写与方言扩展 |
| [SQL 方言支持](doc/sql-dialect-support.md) | 完整方言支持矩阵与各方言特性 |
| [Filter 机制](doc/filter-guide.md) | Filter-Chain 架构与自定义 Filter 开发 |
| [SQL 防火墙](doc/wall-security-guide.md) | WallFilter 配置与 SQL 安全防护 |
| [监控统计](doc/monitoring-guide.md) | StatFilter 与 Web 监控页面 |
| [高可用数据源](doc/ha-datasource.md) | HighAvailableDataSource 配置与使用 |
| [Spring Boot 集成](druid-spring-boot-starter/README.md) | Spring Boot Starter 配置指南 |
| [Wiki (中文)](https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98) | 常见问题与进阶文档 |
| [Wiki (English)](https://github.com/alibaba/druid/wiki/FAQ) | FAQ and documentation |

## 从源码构建

```bash
git clone https://github.com/alibaba/druid.git
cd druid
mvn clean install
```

**要求：** Java 8+ JDK，Apache Maven 3.6+

## 贡献

欢迎参与 Druid 项目！请阅读 [贡献指南](CONTRIBUTING.md) 了解如何参与开发。

## 安全漏洞

请勿通过公开 Issue 报告安全漏洞。详见 [安全策略](SECURITY.md)。

## 相关阿里云产品

* [DataWorks 数据集成](https://help.aliyun.com/document_detail/137663.html)

## License

Druid 基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) 开源。
