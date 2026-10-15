# Filter 机制指南 | Filter Chain Guide

[English](#english) | [中文](#中文)

---

## 中文

Druid 的 Filter-Chain 是一个基于责任链模式的可插拔拦截器架构，允许在 JDBC 操作的各个关键节点插入自定义逻辑，实现日志记录、统计采集、安全防护等功能。

### 架构设计

```
Application
    │
    ▼
┌─────────────────┐
│  DruidDataSource │
│   (FilterChain) │
└─────────┬───────┘
          │
  ┌───────▼────────┐
  │   StatFilter   │  ← 统计 SQL 执行数据
  └───────┬────────┘
          │
  ┌───────▼────────┐
  │   WallFilter   │  ← SQL 安全检查
  └───────┬────────┘
          │
  ┌───────▼────────┐
  │ Slf4jLogFilter │  ← 记录 SQL 日志
  └───────┬────────┘
          │
  ┌───────▼────────┐
  │  JDBC Driver   │  ← 实际执行
  └────────────────┘
```

### 内置 Filter

#### StatFilter — 统计监控

采集 SQL 执行统计数据，包括执行次数、耗时、慢 SQL 等。

```yaml
spring:
  datasource:
    druid:
      filter:
        stat:
          enabled: true
          db-type: mysql
          log-slow-sql: true        # 记录慢 SQL
          slow-sql-millis: 2000     # 慢 SQL 阈值（毫秒）
          merge-sql: true           # 合并相同结构的 SQL
```

```java
// 编程方式配置
StatFilter statFilter = new StatFilter();
statFilter.setSlowSqlMillis(2000);
statFilter.setLogSlowSql(true);
statFilter.setMergeSql(true);
dataSource.setProxyFilters(Arrays.asList(statFilter));
```

#### WallFilter — SQL 防火墙

基于 AST 分析的 SQL 安全防护。详见 [SQL 防火墙指南](wall-security-guide.md)。

```yaml
spring:
  datasource:
    druid:
      filter:
        wall:
          enabled: true
          db-type: mysql
          config:
            delete-allow: false         # 禁止 DELETE
            drop-table-allow: false     # 禁止 DROP TABLE
            multi-statement-allow: false # 禁止多语句执行
```

#### 日志 Filter

Druid 提供多种日志框架适配的 Filter：

| Filter | 日志框架 | 配置 key |
|--------|---------|---------|
| `Slf4jLogFilter` | SLF4J | `filter.slf4j` |
| `Log4jFilter` | Log4j 1.x | `filter.log4j` |
| `Log4j2Filter` | Log4j 2.x | `filter.log4j2` |
| `CommonsLogFilter` | Commons Logging | `filter.commons-log` |

```yaml
spring:
  datasource:
    druid:
      filter:
        slf4j:
          enabled: true
          statement-executable-sql-log-enable: true  # 记录实际执行 SQL（含参数值）
          connection-log-enabled: true               # 记录连接获取/释放
```

#### ConfigFilter — 配置加密

支持对数据源密码进行加密存储：

```bash
# 生成加密密码
java -cp druid-1.2.24.jar com.alibaba.druid.filter.config.ConfigTools your_password
```

```yaml
spring:
  datasource:
    password: encrypted_password_here
    druid:
      filters: config
      connection-properties: config.decrypt=true;config.decrypt.key=your_public_key
```

#### EncodingConvertFilter — 编码转换

用于解决数据库字符编码与应用编码不一致的问题：

```yaml
spring:
  datasource:
    druid:
      filter:
        encoding:
          enabled: true
          client-encoding: UTF-8
          server-encoding: ISO-8859-1
```

### 快捷配置方式

通过 `filters` 属性可快速启用内置 Filter（使用默认配置）：

```yaml
spring:
  datasource:
    druid:
      filters: stat,wall,slf4j    # 逗号分隔，使用默认配置
```

> **注意：** 通过 `filters` 属性启用的 Filter 使用默认配置。如需自定义配置，请使用 `filter.<name>.xxx` 方式。

### 开发自定义 Filter

#### 1. 实现 Filter

继承 `FilterEventAdapter`（或 `FilterAdapter`），重写需要拦截的方法：

```java
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

public class MyCustomFilter extends FilterEventAdapter {

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        // SQL 执行前的逻辑
        System.out.println("[Before] Executing SQL: " + sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        // SQL 执行后的逻辑
        System.out.println("[After] SQL executed, result: " + result);
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        // 查询执行前
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql,
                                               ResultSetProxy resultSet) {
        // 查询执行后
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        // 更新执行前
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql,
                                                int updateCount) {
        // 更新执行后
    }
}
```

#### 2. 注册自定义 Filter

```java
MyCustomFilter myFilter = new MyCustomFilter();
dataSource.setProxyFilters(Arrays.asList(myFilter));
```

#### 可拦截的操作

`FilterEventAdapter` 提供以下拦截点：

| 方法类别 | 拦截点 |
|---------|--------|
| **连接** | `connection_connectBefore/After`、`connection_closeBefore/After` |
| **语句** | `statementExecuteBefore/After`、`statementExecuteQueryBefore/After`、`statementExecuteUpdateBefore/After` |
| **结果集** | `resultSet_nextBefore/After`、`resultSetOpenAfter`、`resultSetCloseBefore/After` |
| **事务** | `connection_commitBefore/After`、`connection_rollbackBefore/After` |
| **PreparedStatement** | `preparedStatement_executeBefore/After` 等 |

### Filter 执行顺序

Filter 按照注册顺序依次执行。`filters` 属性中的顺序即为执行顺序：

```yaml
filters: stat,wall,slf4j
# 执行顺序: stat → wall → slf4j → JDBC Driver
# 返回顺序: JDBC Driver → slf4j → wall → stat
```

---

## English

Druid's Filter-Chain is a pluggable interceptor architecture based on the Chain of Responsibility pattern. It allows custom logic to be inserted at key points during JDBC operations.

### Built-in Filters

| Filter | Purpose | Config Key |
|--------|---------|-----------|
| `StatFilter` | SQL execution statistics, slow SQL logging | `filter.stat` |
| `WallFilter` | AST-based SQL injection protection | `filter.wall` |
| `Slf4jLogFilter` | SLF4J-based SQL logging | `filter.slf4j` |
| `Log4jFilter` | Log4j 1.x logging | `filter.log4j` |
| `Log4j2Filter` | Log4j 2.x logging | `filter.log4j2` |
| `CommonsLogFilter` | Commons Logging | `filter.commons-log` |
| `ConfigFilter` | Password encryption/decryption | `filter.config` |
| `EncodingConvertFilter` | Character encoding conversion | `filter.encoding` |

### Quick Enable

```yaml
spring.datasource.druid.filters: stat,wall,slf4j
```

### Custom Filter Development

Extend `FilterEventAdapter` and override intercept methods:

```java
public class MyFilter extends FilterEventAdapter {
    @Override
    protected void statementExecuteBefore(StatementProxy stmt, String sql) {
        // before SQL execution
    }

    @Override
    protected void statementExecuteAfter(StatementProxy stmt, String sql, boolean result) {
        // after SQL execution
    }
}
```

Register with: `dataSource.setProxyFilters(Arrays.asList(new MyFilter()));`
