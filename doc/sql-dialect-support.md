# SQL 方言支持 | SQL Dialect Support

[English](#english) | [中文](#中文)

---

## 中文

Druid SQL 解析器当前支持 30 种数据库方言。每种方言都提供完整的 Lexer（词法分析器）、Parser（语法分析器）、AST（抽象语法树节点）和 Visitor（访问者）实现。

### 方言支持矩阵

| 方言 | DbType | Lexer | Parser | OutputVisitor | SchemaStatVisitor | WallProvider |
|------|--------|:-----:|:------:|:-------------:|:-----------------:|:------------:|
| MySQL | `mysql` | Y | Y | Y | Y | Y |
| PostgreSQL | `postgresql` | Y | Y | Y | Y | Y |
| Oracle | `oracle` | Y | Y | Y | Y | Y |
| SQL Server | `sqlserver` | Y | Y | Y | Y | Y |
| DB2 | `db2` | Y | Y | Y | Y | Y |
| H2 | `h2` | Y | Y | Y | Y | - |
| Informix | `informix` | Y | Y | Y | Y | - |
| 达梦 (DM) | `dm` | Y | Y | Y | Y | - |
| Oscar | `oscar` | Y | Y | Y | Y | - |
| GaussDB | `gaussdb` | Y | Y | Y | Y | - |
| ClickHouse | `clickhouse` | Y | Y | Y | Y | - |
| Doris | `doris` | Y | Y | Y | Y | - |
| StarRocks | `starrocks` | Y | Y | Y | Y | - |
| Teradata | `teradata` | Y | Y | Y | Y | - |
| Redshift | `redshift` | Y | Y | Y | Y | - |
| BigQuery | `bigquery` | Y | Y | Y | Y | - |
| Snowflake | `snowflake` | Y | Y | Y | Y | - |
| Synapse | `synapse` | Y | Y | Y | Y | - |
| Hologres | `hologres` | Y | Y | Y | Y | - |
| ODPS (MaxCompute) | `odps` | Y | Y | Y | Y | - |
| Hive | `hive` | Y | Y | Y | Y | - |
| Spark | `spark` | Y | Y | Y | Y | - |
| Presto | `presto` | Y | Y | Y | Y | - |
| Impala | `impala` | Y | Y | Y | Y | - |
| Athena | `athena` | Y | Y | Y | Y | - |
| Blink | `blink` | Y | Y | Y | Y | - |
| Databricks | `databricks` | Y | Y | Y | Y | - |
| Phoenix | `phoenix` | Y | Y | Y | Y | - |
| SuperSQL | `supersql` | Y | Y | Y | Y | - |
| Transact-SQL | `transact` | Y | Y | Y | Y | - |

### 各方言特性

#### MySQL

- 全面支持 MySQL 5.x / 8.x 语法
- `SHOW`、`DESCRIBE`、`EXPLAIN` 等管理语句
- `ON DUPLICATE KEY UPDATE` INSERT 冲突处理
- `LIMIT offset, count` 分页语法
- `FORCE INDEX`、`USE INDEX` 索引提示
- MySQL 特有函数（`GROUP_CONCAT`、`JSON_EXTRACT` 等）

#### PostgreSQL

- 支持 PostgreSQL 10+ 语法
- `RETURNING` 子句
- `ON CONFLICT` (UPSERT) 语法
- `LIMIT ... OFFSET` 分页
- 数组类型和操作符
- `COPY` 语句
- Window Functions 完整支持

#### Oracle

- PL/SQL 块解析
- `CONNECT BY` 层次查询
- `ROWNUM` 伪列
- `MERGE INTO` 语句
- `FLASHBACK` 查询
- Package Body 解析

#### SQL Server

- T-SQL 语法支持
- `TOP` 限制
- `WITH (NOLOCK)` 提示
- CTE（`WITH ... AS`）
- `CROSS APPLY` / `OUTER APPLY`
- `ALTER PROCEDURE`、`ALTER VIEW`

#### 达梦 (DM)

- 兼容 Oracle 语法特性
- `CONNECT BY` 层次查询
- DM 特有系统函数
- PL/SQL 兼容语法

#### ClickHouse

- `ENGINE = MergeTree()` 等建表引擎
- `PARTITION BY` 分区语法
- `SAMPLE` 采样查询
- ClickHouse 特有函数

#### BigQuery

- `EXPORT DATA` 语句
- `STRUCT` 和 `ARRAY` 类型
- 反引号标识符（`` ` ``）
- BigQuery 特有函数

### 使用方法

```java
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;

// 指定方言解析
List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);

// 指定方言格式化
String formatted = SQLUtils.format(sql, DbType.postgresql);

// 指定方言创建 Schema 统计
SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
```

### 方言注册机制

每种方言通过 `META-INF/druid/parser/<dialect>/` 下的配置文件注册：

```
META-INF/druid/parser/<dialect>/
├── dialect.properties    # 方言属性配置（引号字符等）
└── builtin_datatypes     # 内置数据类型列表
```

**dialect.properties 示例：**
```properties
# 引号字符
quoteChars=`,",[
```

**builtin_datatypes 示例：**
```
INTEGER
VARCHAR
TEXT
BLOB
REAL
```

### 添加新方言

参见 [贡献指南](../CONTRIBUTING.md#添加新的-sql-方言) 了解如何添加新的 SQL 方言支持。

---

## English

The Druid SQL parser currently supports 30 database dialects. Each dialect provides complete Lexer, Parser, AST and Visitor implementations.

### Dialect Support Matrix

See the Chinese section above for the complete support matrix table.

### Usage

```java
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;

// Parse with specific dialect
List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);

// Format with specific dialect
String formatted = SQLUtils.format(sql, DbType.postgresql);

// Create dialect-specific schema visitor
SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
```

### Per-Dialect Highlights

- **MySQL** — Full 5.x/8.x support including `SHOW`, `LIMIT`, index hints, JSON functions
- **PostgreSQL** — `RETURNING`, `ON CONFLICT`, array types, `COPY`, window functions
- **Oracle** — PL/SQL, `CONNECT BY`, `MERGE INTO`, `FLASHBACK`, package bodies
- **SQL Server** — T-SQL, `TOP`, `WITH (NOLOCK)`, `CROSS APPLY`, CTEs
- **ClickHouse** — Table engines, `PARTITION BY`, `SAMPLE`, ClickHouse functions
- **BigQuery** — `EXPORT DATA`, `STRUCT`/`ARRAY` types, backtick identifiers

### Dialect Registration

Each dialect registers via configuration files in `META-INF/druid/parser/<dialect>/`:
- `dialect.properties` — Dialect properties (quote characters, etc.)
- `builtin_datatypes` — Built-in data type list
