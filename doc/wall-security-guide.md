# SQL 防火墙指南 | SQL Firewall (WallFilter) Guide

[English](#english) | [中文](#中文)

---

## 中文

Druid WallFilter 是基于 SQL AST（抽象语法树）分析的 SQL 安全防护组件，可以有效防御 SQL 注入攻击和危险 SQL 操作。

### 架构原理

WallFilter 作为 Druid Filter-Chain 中的一环，在 SQL 到达数据库之前拦截并检查所有 SQL 操作。

```
Application
    │
    ▼
┌──────────────────────────────────────────────────┐
│                   WallFilter                      │
│                                                   │
│  ┌─────────────┐    ┌──────────────────────────┐ │
│  │ WallConfig  │    │     WallProvider          │ │
│  │ (安全规则)   │    │  ┌────────────────────┐  │ │
│  │             │    │  │ SQL Parser (方言)   │  │ │
│  │ tenantColumn│    │  │        ↓            │  │ │
│  │ updateCheck │    │  │ AST → WallVisitor   │  │ │
│  │ permitTable │    │  │        ↓            │  │ │
│  │ denyTable   │    │  │ 白名单/黑名单 Cache  │  │ │
│  └─────────────┘    │  └────────────────────┘  │ │
│                     └──────────────────────────┘ │
└──────────────────────────────────────────────────┘
    │
    ▼
JDBC Driver
```

核心组件：

| 组件 | 职责 |
|------|------|
| `WallFilter` | Filter-Chain 入口，拦截所有 JDBC 操作，管理多租户列隐藏和结果集映射 |
| `WallProvider` | 执行 SQL 检查，管理白名单/黑名单缓存和统计数据，按方言特化 |
| `WallConfig` | 安全规则配置，包括 DML/DDL 控制、注入检测、多租户设置 |
| `WallContext` | 线程级上下文，通过 ThreadLocal 传递当前 SQL 检查状态 |
| `WallVisitor` | AST 遍历器，按方言特化的规则检查器 |

#### 检查流程

与基于正则匹配的安全方案不同，AST 分析能精确理解 SQL 语义，大幅减少误报和漏报：

```
SQL → Parser → AST → WallVisitor(检查规则) → 允许/拒绝
```

WallProvider 内部维护高性能缓存加速检查：

1. **白名单缓存** — 已知安全 SQL 的 AST 摘要，命中后跳过完整检查
2. **黑名单缓存** — 已知危险 SQL 直接拦截
3. **合并 SQL 缓存** — 参数化后的 SQL 模板去重统计

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

#### WallFilter 级别配置

通过 JVM 系统属性或 `Properties` 对象设置：

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `druid.wall.logViolation` | false | 违规 SQL 是否输出到日志（ERROR 级别） |
| `druid.wall.throwException` | true | 违规 SQL 是否抛出 `SQLException` |

两者可组合使用：

| logViolation | throwException | 行为 |
|:---:|:---:|------|
| false | true | 抛异常，不写日志（默认） |
| true | true | 抛异常并写日志 |
| true | false | 写日志但放行 SQL |
| false | false | 静默放行（不推荐） |

> `logViolation` 和 `throwException` 为 `volatile` 字段，支持运行时通过 JMX 动态修改，无需重启。

#### WallConfig — 基本安全规则

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `selectAllow` | true | 是否允许 SELECT |
| `selectAllColumnAllow` | true | 是否允许 `SELECT *` |
| `insertAllow` | true | 是否允许 INSERT |
| `updateAllow` | true | 是否允许 UPDATE |
| `deleteAllow` | true | 是否允许 DELETE |
| `mergeAllow` | true | 是否允许 MERGE |
| `callAllow` | true | 是否允许存储过程调用 |

#### WallConfig — DDL 控制

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `createTableAllow` | true | 是否允许 CREATE TABLE |
| `alterTableAllow` | true | 是否允许 ALTER TABLE |
| `dropTableAllow` | true | 是否允许 DROP TABLE |
| `truncateAllow` | true | 是否允许 TRUNCATE |
| `commentAllow` | false | 是否允许 SQL 注释 |

#### WallConfig — 注入防护

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

#### WallConfig — 对象访问控制

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `tableCheck` | true | 检查表名是否在白名单中 |
| `schemaCheck` | true | 检查 Schema 名 |
| `functionCheck` | true | 检查函数调用 |
| `objectCheck` | true | 检查对象访问 |
| `variantCheck` | true | 检查变量使用 |
| `metadataAllow` | true | 是否允许 `Connection.getMetaData()` |
| `wrapAllow` | true | 是否允许 `isWrapperFor`/`unwrap` 操作 |

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

WallFilter 针对不同数据库方言提供了专门的 WallProvider，每种 Provider 使用对应方言的 SQL Parser 进行精准的语义分析。

| WallProvider | 覆盖的数据库 |
|-------------|------------|
| `MySqlWallProvider` | MySQL, MariaDB, OceanBase, DRDS, TiDB, H2, Lealone, Presto, Trino, SuperSQL, PolarDB-X |
| `OracleWallProvider` | Oracle, AliOracle, OceanBase Oracle 模式, PolarDB-O |
| `SQLServerWallProvider` | SQL Server, jTDS |
| `PGWallProvider` | PostgreSQL, EDB, PolarDB, Greenplum, GaussDB |
| `DB2WallProvider` | DB2 |
| `SQLiteWallProvider` | SQLite |
| `CKWallProvider` | ClickHouse |

> **说明：** 对于上表中未列出的数据库类型，WallFilter 会尝试通过 SPI 机制加载自定义 WallProvider。

#### SPI 扩展自定义方言

对于 Druid 未内置支持的数据库，可以通过实现 `WallProviderCreator` 接口注册自定义 WallProvider：

```java
public class MyDbWallProviderCreator implements WallProviderCreator {
    @Override
    public WallProvider createWallConfig(DataSourceProxy dataSource,
                                          WallConfig config, DbType dbType) {
        if (dbType == DbType.mydb) {
            if (config == null) {
                config = new WallConfig("META-INF/druid/wall/mydb");
            }
            return new MyDbWallProvider(config);
        }
        return null; // 返回 null 表示不处理，交给下一个 Creator
    }

    @Override
    public int getOrder() {
        return 100; // 数值越小优先级越高
    }
}
```

注册 SPI：在 `META-INF/services/com.alibaba.druid.wall.WallProviderCreator` 文件中添加实现类全限定名。

多个 Creator 按 `getOrder()` 升序排列，优先级高的先被调用，第一个返回非 null 的 WallProvider 生效。

### 多租户数据隔离

WallFilter 支持结果集级别的多租户数据隔离，可以在 SQL 查询结果返回给应用时自动隐藏租户标识列并进行租户过滤。

#### 基本配置

```java
WallConfig config = new WallConfig();

// 方式一：全局设置租户列名
config.setTenantColumn("tenant_id");

// 方式二：设置租户表匹配模式（支持 glob 通配符）
config.setTenantTablePattern("t_*"); // 仅对 t_ 前缀的表生效
```

#### TenantCallBack 回调

通过 `TenantCallBack` 可以实现精细化的多租户控制：

```java
config.setTenantCallBack(new TenantCallBack() {
    @Override
    public String getTenantColumn(StatementType statementType, String tableName) {
        // 按表名返回该表的租户列名
        return "tenant_id";
    }

    @Override
    public String getHiddenColumn(String tableName) {
        // 返回需要对应用隐藏的列名（结果集中不可见）
        return "tenant_id";
    }

    @Override
    public void filterResultsetTenantColumn(Object value) {
        // 每行数据返回时调用，可用于校验租户值
        String currentTenant = TenantContext.getCurrentTenant();
        if (!currentTenant.equals(String.valueOf(value))) {
            throw new SecurityException("Tenant mismatch");
        }
    }
});
```

#### 列隐藏机制

当配置了隐藏列后，WallFilter 在获取 ResultSet 时自动处理：

1. 扫描 ResultSetMetaData，识别需要隐藏的列
2. 建立逻辑列号 ↔ 物理列号的双向映射
3. 应用层通过 `resultSet.getXxx(columnIndex)` 访问时，自动转换为正确的物理列号
4. `ResultSetMetaData.getColumnCount()` 返回的列数自动减去隐藏列数
5. `resultSet.findColumn(hiddenColumnName)` 对隐藏列抛出 `SQLException`

### UPDATE 安全检查

WallFilter 支持对 UPDATE 操作进行值级别的安全检查，通过 `WallConfig.updateCheckHandler` 可以在执行前校验 SET 子句的赋值和 WHERE 子句的过滤条件：

```java
config.setUpdateCheckHandler(new WallUpdateCheckHandler() {
    @Override
    public boolean check(String tableName, String columnName,
                         Object setValue, List<Object> filterValues) {
        // tableName: 被更新的表名
        // columnName: 被更新的列名
        // setValue: SET 子句中的新值
        // filterValues: WHERE 子句中的过滤值列表
        //
        // 返回 true 允许更新，false 拒绝（抛出 SQLException）

        // 示例：禁止将 status 列设为 "deleted"
        if ("status".equals(columnName) && "deleted".equals(setValue)) {
            return false;
        }
        return true;
    }
});
```

该检查在 PreparedStatement 执行时触发，能够读取绑定参数的实际值（不仅是 SQL 文本），提供运行时的值级校验能力。

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

当违规 SQL 是语法错误（`SyntaxErrorViolation`）时，原始的解析异常会作为 `SQLException` 的 cause 附带抛出，便于诊断。

### JMX 监控与管理

WallFilter 实现了 `WallFilterMBean` 接口，注册为 JMX MBean 后可通过 JConsole 等工具进行运行时监控和管理。

#### MBean 操作

| 方法 | 说明 |
|------|------|
| `getDbType()` | 获取当前数据库方言 |
| `isLogViolation()` / `setLogViolation(boolean)` | 查询/设置是否记录违规日志 |
| `isThrowException()` / `setThrowException(boolean)` | 查询/设置是否抛出异常 |
| `getViolationCount()` | 获取累计违规次数 |
| `resetViolationCount()` | 重置违规计数器 |
| `check(String sql)` | 手动检查指定 SQL 是否合规 |
| `checkValid(String sql)` | 快速校验 SQL 是否合法（返回 boolean） |
| `getProviderWhiteList()` | 获取当前白名单中的 SQL 集合 |
| `clearProviderCache()` | 清空白名单/黑名单缓存 |
| `clearWhiteList()` | 清空白名单 |

#### 运行时调整示例

```java
// 获取 WallFilter 实例
WallFilter wallFilter = (WallFilter) dataSource.getProxyFilters().stream()
    .filter(f -> f instanceof WallFilter)
    .findFirst().orElse(null);

// 运行时开启违规日志（无需重启）
wallFilter.setLogViolation(true);

// 查看统计
long violations = wallFilter.getViolationCount();

// 清空缓存（规则变更后生效）
wallFilter.clearProviderCache();
```

### 特权模式

对于框架内部需要绕过 Wall 检查的场景（如连接池健康检查、内部元数据查询），WallProvider 提供了特权模式：

```java
WallProvider.doPrivileged(() -> {
    // 此代码块内的 SQL 不经过 Wall 检查
    connection.getMetaData();
});
```

特权模式通过 ThreadLocal 实现，仅对当前线程当前代码块生效，代码块结束后自动恢复检查。使用前需确保 `WallConfig.doPrivilegedAllow` 为 true。

> **安全提示：** 特权模式仅限框架内部使用，不应暴露给应用代码或用户输入。

---

## English

### Overview

Druid WallFilter is an AST-based SQL security component that protects against SQL injection attacks and dangerous SQL operations. Unlike regex-based approaches, AST analysis accurately understands SQL semantics, significantly reducing false positives and negatives.

### Architecture

WallFilter intercepts all JDBC operations as part of Druid's Filter-Chain, checking SQL before it reaches the database.

Core components:

| Component | Responsibility |
|-----------|---------------|
| `WallFilter` | Filter-Chain entry point; intercepts JDBC operations, manages tenant column hiding |
| `WallProvider` | Executes SQL checks, manages whitelist/blacklist caches and statistics |
| `WallConfig` | Security rule configuration: DML/DDL control, injection detection, tenant settings |
| `WallVisitor` | Dialect-specific AST visitor that enforces security rules |

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

Or via shorthand: `spring.datasource.druid.filters: wall`

### WallFilter-Level Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `druid.wall.logViolation` | false | Log violations at ERROR level |
| `druid.wall.throwException` | true | Throw `SQLException` on violation |

Both are `volatile` fields and can be changed at runtime via JMX without restart.

### Protection Capabilities

- **Stack injection** — Blocks multi-statement execution (`multiStatementAllow=false`)
- **UNION injection** — Detects UNION-based attacks (`selectUnionCheck=true`)
- **Tautology injection** — Blocks always-true conditions (`conditionAndAlwayTrueAllow=false`)
- **Comment injection** — Blocks SQL comments used to bypass checks (`commentAllow=false`)
- **DDL protection** — Blocks `DROP TABLE`, `TRUNCATE`, `ALTER TABLE`
- **Object access control** — Table/schema whitelist and blacklist
- **Metadata protection** — Controls access to `Connection.getMetaData()` (`metadataAllow`)

### Dialect Support

| WallProvider | Databases |
|-------------|-----------|
| `MySqlWallProvider` | MySQL, MariaDB, OceanBase, DRDS, TiDB, H2, Lealone, Presto, Trino, SuperSQL, PolarDB-X |
| `OracleWallProvider` | Oracle, AliOracle, OceanBase Oracle mode, PolarDB-O |
| `SQLServerWallProvider` | SQL Server, jTDS |
| `PGWallProvider` | PostgreSQL, EDB, PolarDB, Greenplum, GaussDB |
| `DB2WallProvider` | DB2 |
| `SQLiteWallProvider` | SQLite |
| `CKWallProvider` | ClickHouse |

For unlisted databases, implement the `WallProviderCreator` SPI interface:

```java
public class MyWallProviderCreator implements WallProviderCreator {
    @Override
    public WallProvider createWallConfig(DataSourceProxy ds, WallConfig config, DbType dbType) {
        if (dbType == DbType.mydb) {
            return new MyDbWallProvider(config != null ? config : new WallConfig("META-INF/druid/wall/mydb"));
        }
        return null;
    }

    @Override
    public int getOrder() { return 100; } // lower value = higher priority
}
```

Register in `META-INF/services/com.alibaba.druid.wall.WallProviderCreator`.

### Multi-Tenant Data Isolation

WallFilter supports result-set-level tenant isolation by automatically hiding tenant identifier columns and filtering tenant values.

```java
WallConfig config = new WallConfig();
config.setTenantColumn("tenant_id");
config.setTenantTablePattern("t_*"); // only apply to tables matching pattern

config.setTenantCallBack(new TenantCallBack() {
    @Override
    public String getTenantColumn(StatementType type, String tableName) {
        return "tenant_id";
    }

    @Override
    public String getHiddenColumn(String tableName) {
        return "tenant_id"; // hidden from application ResultSet
    }

    @Override
    public void filterResultsetTenantColumn(Object value) {
        // called for each row; validate tenant value here
    }
});
```

When hidden columns are configured:
- `ResultSetMetaData.getColumnCount()` excludes hidden columns
- `resultSet.getXxx(columnIndex)` transparently maps logical to physical column indices
- `resultSet.findColumn(hiddenColumnName)` throws `SQLException`

### UPDATE Value Check

WallFilter can validate UPDATE operations at the value level before execution:

```java
config.setUpdateCheckHandler((tableName, columnName, setValue, filterValues) -> {
    // return true to allow, false to block (throws SQLException)
    return !"deleted".equals(setValue);
});
```

This check reads actual bind parameter values from PreparedStatement, providing runtime value-level validation beyond static SQL analysis.

### JMX Monitoring

WallFilter implements `WallFilterMBean` for runtime monitoring:

| Method | Description |
|--------|-------------|
| `getViolationCount()` | Cumulative violation count |
| `check(String sql)` | Manually check SQL compliance |
| `checkValid(String sql)` | Quick boolean validation |
| `clearProviderCache()` | Clear whitelist/blacklist caches |
| `setLogViolation(boolean)` | Toggle violation logging at runtime |
| `setThrowException(boolean)` | Toggle exception throwing at runtime |

### Privileged Mode

For framework-internal operations that need to bypass Wall checks (e.g., health checks):

```java
WallProvider.doPrivileged(() -> {
    // SQL in this block bypasses Wall checks
    connection.getMetaData();
});
```

Requires `WallConfig.doPrivilegedAllow=true`. Scoped to current thread via ThreadLocal, automatically restored after the block completes.
