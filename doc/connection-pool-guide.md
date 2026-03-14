# 连接池配置指南 | Connection Pool Guide

[English](#english) | [中文](#中文)

---

## 中文

### 基本配置

`DruidDataSource` 是 Druid 的核心连接池实现。以下是完整的配置参数说明：

#### 必选参数

| 参数 | 说明 | 示例 |
|------|------|------|
| `url` | JDBC 连接 URL | `jdbc:mysql://localhost:3306/mydb` |
| `username` | 数据库用户名 | `root` |
| `password` | 数据库密码 | `password` |

#### 连接池容量

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `initialSize` | 0 | 初始化时创建的物理连接数 |
| `maxActive` | 8 | 最大连接池数量 |
| `minIdle` | 0 | 最小空闲连接数 |
| `maxWait` | -1 | 获取连接最大等待时间（毫秒），-1 表示无限等待 |

#### 连接检测

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `validationQuery` | - | 连接有效性检测 SQL（如 `SELECT 1`） |
| `validationQueryTimeout` | -1 | 检测超时时间（秒） |
| `testOnBorrow` | false | 获取连接时检测有效性 |
| `testOnReturn` | false | 归还连接时检测有效性 |
| `testWhileIdle` | true | 空闲时检测有效性（推荐开启） |

#### 连接回收

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `timeBetweenEvictionRunsMillis` | 60000 | 回收线程执行间隔（毫秒） |
| `minEvictableIdleTimeMillis` | 300000 | 连接最小空闲时间，超过则可被回收（5分钟） |
| `maxEvictableIdleTimeMillis` | 25200000 | 连接最大空闲时间，超过则强制回收（7小时） |
| `keepAlive` | false | 是否对空闲连接发送心跳保活 |
| `keepAliveBetweenTimeMillis` | 120000 | KeepAlive 间隔时间（毫秒） |

#### PreparedStatement 缓存

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `poolPreparedStatements` | false | 是否启用 PSCache |
| `maxPoolPreparedStatementPerConnectionSize` | 10 | 每个连接的 PSCache 大小 |

### 配置示例

#### Java 代码配置

```java
DruidDataSource dataSource = new DruidDataSource();

// 基本配置
dataSource.setUrl("jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8");
dataSource.setUsername("root");
dataSource.setPassword("password");

// 连接池容量
dataSource.setInitialSize(5);
dataSource.setMaxActive(20);
dataSource.setMinIdle(5);
dataSource.setMaxWait(60000);

// 连接检测
dataSource.setValidationQuery("SELECT 1");
dataSource.setTestWhileIdle(true);
dataSource.setTestOnBorrow(false);
dataSource.setTestOnReturn(false);

// 连接回收
dataSource.setTimeBetweenEvictionRunsMillis(60000);
dataSource.setMinEvictableIdleTimeMillis(300000);

// KeepAlive
dataSource.setKeepAlive(true);
dataSource.setKeepAliveBetweenTimeMillis(120000);

// PSCache（MySQL 建议关闭，Oracle/DB2/PostgreSQL 建议开启）
dataSource.setPoolPreparedStatements(false);

// Filter
dataSource.setFilters("stat,wall");

// 初始化
dataSource.init();
```

#### Spring Boot 配置（application.yml）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8
    username: root
    password: password
    druid:
      initial-size: 5
      max-active: 20
      min-idle: 5
      max-wait: 60000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      keep-alive: true
      filters: stat,wall
```

### 最佳实践

#### 1. 连接池大小

根据应用并发量合理设置：

| 场景 | initialSize | minIdle | maxActive |
|------|------------|---------|-----------|
| 小型应用（< 50 QPS） | 2 | 2 | 10 |
| 中型应用（50-500 QPS） | 5 | 5 | 20 |
| 大型应用（> 500 QPS） | 10 | 10 | 50 |

> **经验公式：** `maxActive ≈ (应用节点数 × 单节点最大并发线程数) / 数据库可承载最大连接数`

#### 2. 连接检测策略

- **推荐启用 `testWhileIdle`** — 在空闲检测时验证连接，性能开销最小
- **生产环境慎用 `testOnBorrow`** — 每次获取连接都会执行检测 SQL，高并发下有性能影响
- **必须设置 `validationQuery`** — 否则连接检测无法生效

#### 3. 超时配置

```yaml
# 获取连接等待超时（避免无限等待）
max-wait: 60000

# 防止连接泄漏（开发环境推荐）
remove-abandoned: true
remove-abandoned-timeout: 300
log-abandoned: true
```

#### 4. MySQL 特定配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://host:3306/db?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false
    druid:
      validation-query: SELECT 1
      pool-prepared-statements: false  # MySQL 不建议开启 PSCache
```

#### 5. Oracle 特定配置

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@host:1521:sid
    druid:
      validation-query: SELECT 1 FROM DUAL
      pool-prepared-statements: true   # Oracle 建议开启 PSCache
      max-pool-prepared-statement-per-connection-size: 20
```

### 连接泄漏检测

当应用存在连接泄漏（获取连接后未正确关闭）时，可启用 RemoveAbandoned 功能：

```java
dataSource.setRemoveAbandoned(true);
dataSource.setRemoveAbandonedTimeout(300); // 300 秒
dataSource.setLogAbandoned(true);          // 记录泄漏连接的堆栈
```

> **注意：** `removeAbandoned` 仅建议在开发和测试环境使用。生产环境应通过代码审查和测试确保连接正确关闭。

---

## English

### Basic Configuration

`DruidDataSource` is Druid's core connection pool implementation.

#### Required Parameters

| Parameter | Description | Example |
|-----------|-------------|---------|
| `url` | JDBC connection URL | `jdbc:mysql://localhost:3306/mydb` |
| `username` | Database username | `root` |
| `password` | Database password | `password` |

#### Pool Capacity

| Parameter | Default | Description |
|-----------|---------|-------------|
| `initialSize` | 0 | Number of physical connections created at initialization |
| `maxActive` | 8 | Maximum number of connections in the pool |
| `minIdle` | 0 | Minimum number of idle connections |
| `maxWait` | -1 | Maximum wait time (ms) to get a connection; -1 means infinite |

#### Connection Validation

| Parameter | Default | Description |
|-----------|---------|-------------|
| `validationQuery` | - | SQL used to validate connections (e.g., `SELECT 1`) |
| `testOnBorrow` | false | Validate on connection acquisition |
| `testOnReturn` | false | Validate on connection return |
| `testWhileIdle` | true | Validate idle connections (recommended) |

#### Connection Eviction

| Parameter | Default | Description |
|-----------|---------|-------------|
| `timeBetweenEvictionRunsMillis` | 60000 | Eviction thread interval (ms) |
| `minEvictableIdleTimeMillis` | 300000 | Minimum idle time before eviction (5 min) |
| `maxEvictableIdleTimeMillis` | 25200000 | Maximum idle time, force eviction (7 hours) |
| `keepAlive` | false | Send heartbeats to idle connections |

### Best Practices

1. **Enable `testWhileIdle`** — lowest overhead validation strategy
2. **Always set `validationQuery`** — required for validation to work
3. **Set `maxWait`** — avoid infinite waits in high-concurrency scenarios
4. **Disable `poolPreparedStatements` for MySQL** — enable for Oracle/DB2/PostgreSQL
5. **Use `removeAbandoned` in dev/test** — helps detect connection leaks
