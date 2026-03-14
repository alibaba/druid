# SQL 防火墙指南 | SQL Firewall (WallFilter) Guide

[English](#english) | [中文](#中文)

---

## 中文

Druid WallFilter 是基于 SQL AST（抽象语法树）分析的 SQL 安全防护组件，可以有效防御 SQL 注入攻击和危险 SQL 操作。

### 工作原理

WallFilter 在 SQL 执行前对 SQL 进行解析，生成 AST，然后通过方言特化的 WallVisitor 遍历 AST，检查是否存在违规操作：

```
SQL → Parser → AST → WallVisitor(检查规则) → 允许/拒绝
```

与基于正则匹配的安全方案不同，AST 分析能精确理解 SQL 语义，大幅减少误报和漏报。

### 快速启用

#### Spring Boot

```yaml
spring:
  datasource:
    druid:
      filter:
        wall:
          enabled: true
          db-type: mysql
```

#### Java 代码

```java
WallFilter wallFilter = new WallFilter();
wallFilter.setDbType(DbType.mysql);
dataSource.setProxyFilters(Arrays.asList(wallFilter));
```

#### 快捷方式

```yaml
spring.datasource.druid.filters: wall
```

### 配置项

#### 基本安全规则

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `selectAllow` | true | 是否允许 SELECT |
| `selectAllColumnAllow` | true | 是否允许 `SELECT *` |
| `insertAllow` | true | 是否允许 INSERT |
| `updateAllow` | true | 是否允许 UPDATE |
| `deleteAllow` | true | 是否允许 DELETE |
| `mergeAllow` | true | 是否允许 MERGE |
| `callAllow` | true | 是否允许存储过程调用 |

#### DDL 控制

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `createTableAllow` | true | 是否允许 CREATE TABLE |
| `alterTableAllow` | true | 是否允许 ALTER TABLE |
| `dropTableAllow` | true | 是否允许 DROP TABLE |
| `truncateAllow` | true | 是否允许 TRUNCATE |
| `commentAllow` | false | 是否允许 SQL 注释 |

#### 注入防护

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `multiStatementAllow` | false | 是否允许多语句执行（堆叠注入防护） |
| `noneBaseStatementAllow` | false | 是否允许非基本语句（如 `SET`、`SHOW`） |
| `conditionAndAlwayTrueAllow` | true | 是否允许恒真条件 `AND 1=1` |
| `conditionAndAlwayFalseAllow` | false | 是否允许恒假条件 `AND 1=0` |
| `selectIntoAllow` | true | 是否允许 `SELECT INTO` |
| `selectUnionCheck` | true | 检查 UNION 注入 |
| `selectWhereAlwayTrueCheck` | true | 检查 WHERE 恒真 |
| `selectHavingAlwayTrueCheck` | true | 检查 HAVING 恒真 |
| `deleteWhereAlwayTrueCheck` | true | 检查 DELETE WHERE 恒真 |
| `updateWhereAlayTrueCheck` | true | 检查 UPDATE WHERE 恒真 |

#### 对象访问控制

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `tableCheck` | true | 检查表名是否在白名单中 |
| `schemaCheck` | true | 检查 Schema 名 |
| `functionCheck` | true | 检查函数调用 |
| `objectCheck` | true | 检查对象访问 |
| `variantCheck` | true | 检查变量使用 |

### 配置示例

#### 严格模式（生产环境推荐）

```yaml
spring:
  datasource:
    druid:
      filter:
        wall:
          enabled: true
          db-type: mysql
          config:
            # 禁止危险操作
            delete-allow: false
            drop-table-allow: false
            truncate-allow: false
            alter-table-allow: false

            # 注入防护
            multi-statement-allow: false
            comment-allow: false
            condition-and-alway-true-allow: false
            select-union-check: true
            delete-where-alway-true-check: true
            update-where-alay-true-check: true
```

#### 宽松模式（开发/测试环境）

```yaml
spring:
  datasource:
    druid:
      filter:
        wall:
          enabled: true
          db-type: mysql
          config:
            multi-statement-allow: true
            comment-allow: true
            none-base-statement-allow: true
```

### 白名单与黑名单

WallFilter 支持通过白名单和黑名单控制可访问的表和 Schema：

```java
WallConfig config = new WallConfig();

// 添加允许访问的表（白名单）
config.getPermitTables().add("users");
config.getPermitTables().add("orders");

// 添加禁止访问的表（黑名单）
config.getDenyTables().add("admin_secrets");

WallFilter wallFilter = new WallFilter();
wallFilter.setConfig(config);
```

也可以通过配置文件批量管理白名单/黑名单，在 WallConfig 资源目录下放置 `permit-table.txt` 和 `deny-table.txt` 文件，每行一个表名。

### 方言支持

WallFilter 针对不同数据库方言提供了专门的 WallProvider：

| 方言 | WallProvider |
|------|-------------|
| MySQL | `MySqlWallProvider` |
| Oracle | `OracleWallProvider` |
| SQL Server | `SQLServerWallProvider` |
| PostgreSQL | `PGWallProvider` |
| DB2 | `DB2WallProvider` |
| SQLite | `SQLiteWallProvider` |

> **说明：** SQLite 虽未列入 Druid 的 30 个 SQL 方言解析器，但提供了独立的 WallProvider 用于基本的 SQL 安全防护。

### SQL 注入防护示例

WallFilter 可以检测并阻止常见的 SQL 注入攻击：

```sql
-- 堆叠注入（被 multiStatementAllow=false 拦截）
SELECT * FROM users WHERE id = 1; DROP TABLE users;

-- UNION 注入（被 selectUnionCheck=true 拦截）
SELECT * FROM users WHERE id = 1 UNION SELECT * FROM admin;

-- 恒真条件注入（被 conditionAndAlwayTrueAllow=false 拦截）
SELECT * FROM users WHERE id = 1 OR 1=1;

-- 注释注入（被 commentAllow=false 拦截）
SELECT * FROM users WHERE id = 1 -- AND password = 'xxx';
```

### 处理拦截

当 SQL 被拦截时，WallFilter 会抛出 `SQLException`（消息以 `"sql injection violation"` 开头）。可在应用层捕获处理：

```java
try {
    jdbcTemplate.query(sql, ...);
} catch (SQLException e) {
    if (e.getMessage() != null && e.getMessage().startsWith("sql injection violation")) {
        // SQL 被防火墙拦截
        logger.warn("SQL blocked by WallFilter: {}", e.getMessage());
        // 进行告警、审计等处理
    }
}
```

> **注意：** 可通过 `druid.wall.throwException=false` 配置禁止 WallFilter 抛出异常，此时违规 SQL 会被静默拦截（不执行但也不报错）。

---

## English

### Overview

Druid WallFilter is an AST-based SQL security component that protects against SQL injection attacks and dangerous SQL operations. Unlike regex-based approaches, AST analysis accurately understands SQL semantics, significantly reducing false positives and negatives.

### Quick Start

```yaml
spring:
  datasource:
    druid:
      filter:
        wall:
          enabled: true
          db-type: mysql
          config:
            delete-allow: false
            drop-table-allow: false
            multi-statement-allow: false
```

### Protection Capabilities

- **Stack injection** — Blocks multi-statement execution
- **UNION injection** — Detects UNION-based attacks
- **Tautology injection** — Blocks always-true conditions (`1=1`, `'a'='a'`)
- **Comment injection** — Blocks SQL comments used to bypass checks
- **DDL protection** — Blocks `DROP TABLE`, `TRUNCATE`, `ALTER TABLE`
- **Object access control** — Table/schema whitelist and blacklist

### Dialect-Specific Providers

WallFilter uses dialect-specific providers: `MySqlWallProvider`, `OracleWallProvider`, `SQLServerWallProvider`, `PGWallProvider`, `DB2WallProvider`, `SQLiteWallProvider`.

> **Note:** SQLite is not among Druid's 30 SQL dialect parsers, but a standalone WallProvider is available for basic SQL security protection.
