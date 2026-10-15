# Alibaba Druid

[![Java CI](https://img.shields.io/github/actions/workflow/status/alibaba/druid/ci.yaml?branch=master&logo=github&logoColor=white)](https://github.com/alibaba/druid/actions/workflows/ci.yaml)
[![Codecov](https://img.shields.io/codecov/c/github/alibaba/druid/master?logo=codecov&logoColor=white)](https://codecov.io/gh/alibaba/druid/branch/master)
[![Maven Central](https://img.shields.io/maven-central/v/com.alibaba/druid?logo=apache-maven&logoColor=white)](https://search.maven.org/artifact/com.alibaba/druid)
[![Last SNAPSHOT](https://img.shields.io/nexus/snapshots/https/oss.sonatype.org/com.alibaba/druid?label=latest%20snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/alibaba/druid/)
[![GitHub release](https://img.shields.io/github/release/alibaba/druid)](https://github.com/alibaba/druid/releases)
[![License](https://img.shields.io/github/license/alibaba/druid?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](README.md)

---

**Druid** is a high-performance database connection pool and SQL parser open-sourced by Alibaba. It deeply integrates JDBC connection pooling, SQL parsing and analysis, security protection, and monitoring statistics into one package, making it one of the most comprehensive database middleware solutions in the Java ecosystem.

## Key Features

| Capability | Description |
|-----------|-------------|
| **JDBC Connection Pool** | High-performance, monitorable pool implementation `DruidDataSource` with connection warm-up, PSCache, KeepAlive and more |
| **SQL Parser** | Full parser for 30 SQL dialects, generating AST (Abstract Syntax Tree) with support for formatting, rewriting and analysis |
| **SQL Firewall** | AST-based `WallFilter` for SQL injection protection, blocking dangerous operations |
| **Monitoring & Stats** | Built-in `StatFilter` for real-time SQL execution statistics, connection pool status, and Web monitoring console |
| **Filter Extension** | Pluggable Filter-Chain architecture supporting logging, encryption, statistics and custom extensions |
| **Spring Boot Integration** | Starters for Spring Boot 2.x / 3.x / 4.x with auto-configuration |
| **High Availability** | `HighAvailableDataSource` supporting multi-datasource load balancing, health checks and failover |

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.24</version>
</dependency>
```

### Spring Boot (Recommended)

Choose the starter matching your Spring Boot version:

| Spring Boot Version | Starter |
|-------------------|---------|
| 2.x | `druid-spring-boot-starter` |
| 3.x | `druid-spring-boot-3-starter` |
| 4.x | `druid-spring-boot-4-starter` |

```xml
<!-- Spring Boot 3.x example -->
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
      filter:
        stat:
          enabled: true
          log-slow-sql: true
          slow-sql-millis: 2000
        wall:
          enabled: true
```

### Direct DruidDataSource Usage

```java
DruidDataSource dataSource = new DruidDataSource();
dataSource.setUrl("jdbc:mysql://localhost:3306/mydb");
dataSource.setUsername("root");
dataSource.setPassword("password");
dataSource.setInitialSize(5);
dataSource.setMaxActive(20);
dataSource.setMinIdle(5);
dataSource.init();

try (Connection conn = dataSource.getConnection()) {
    // execute SQL
}
```

### SQL Parser

```java
// Parse SQL
String sql = "SELECT id, name FROM users WHERE age > 18 ORDER BY name";
List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);

// Format SQL
String formatted = SQLUtils.format(sql, DbType.mysql);

// Extract schema information
SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
stmts.get(0).accept(visitor);
System.out.println("Tables: " + visitor.getTables());
System.out.println("Columns: " + visitor.getColumns());
```

## Project Modules

```
druid/
├── core/                           # Core library (pool, SQL parser, security, monitoring)
├── druid-spring-boot-starter/      # Spring Boot 2.x auto-configuration
├── druid-spring-boot-3-starter/    # Spring Boot 3.x auto-configuration
├── druid-spring-boot-4-starter/    # Spring Boot 4.x auto-configuration
├── druid-wrapper/                  # Wrapper utilities
├── druid-admin/                    # Cluster monitoring admin
└── doc/                            # Documentation
```

## SQL Dialect Support

The Druid SQL parser supports 30 database dialects, each with full Lexer, Parser, AST and Visitor implementations:

| Category | Supported Databases |
|----------|-------------------|
| **Major RDBMS** | MySQL, PostgreSQL, Oracle, SQL Server, DB2, H2, Informix |
| **Chinese Databases** | Dameng (DM), Oscar, GaussDB |
| **Analytical / MPP** | ClickHouse, Doris, StarRocks, Teradata, Redshift |
| **Cloud / Data Warehouse** | BigQuery, Snowflake, Synapse, Hologres, ODPS (MaxCompute) |
| **Compute Engines** | Hive, Spark, Presto, Impala, Athena, Blink, Databricks |
| **Other** | Phoenix, SuperSQL, Transact-SQL |

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture Overview](doc/architecture.md) | System architecture and core component interactions |
| [Connection Pool Guide](doc/connection-pool-guide.md) | DruidDataSource configuration, tuning and best practices |
| [SQL Parser Guide](doc/sql-parser-guide.md) | SQL parsing, formatting, rewriting and dialect extensions |
| [SQL Dialect Support](doc/sql-dialect-support.md) | Complete dialect support matrix and per-dialect features |
| [Filter Mechanism](doc/filter-guide.md) | Filter-Chain architecture and custom Filter development |
| [SQL Firewall](doc/wall-security-guide.md) | WallFilter configuration and SQL security |
| [Monitoring & Statistics](doc/monitoring-guide.md) | StatFilter and Web monitoring console |
| [High Availability](doc/ha-datasource.md) | HighAvailableDataSource configuration and usage |
| [Spring Boot Integration](druid-spring-boot-starter/README_EN.md) | Spring Boot Starter configuration guide |
| [Wiki](https://github.com/alibaba/druid/wiki/FAQ) | FAQ and additional documentation |

## Building from Source

```bash
git clone https://github.com/alibaba/druid.git
cd druid
mvn clean install
```

**Requirements:** Java 8+ JDK, Apache Maven 3.6+

## Contributing

Contributions are welcome! Please read the [Contributing Guide](CONTRIBUTING.md) for details.

## Security

Please do not report security vulnerabilities through public issues. See [Security Policy](SECURITY.md).

## License

Druid is open-sourced under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).
