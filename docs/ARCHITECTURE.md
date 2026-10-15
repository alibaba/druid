# Alibaba Druid 架构文档

> 版本: 1.2.28-SNAPSHOT | Java 8+ | Apache License 2.0

## 1. 项目概览

Druid 是阿里巴巴开源的数据库中间件，核心提供两大功能：
- **JDBC 连接池**：高性能、可监控的数据库连接池实现
- **SQL 解析器**：支持 30+ 种数据库方言的完整 SQL 解析、AST 构建与访问器框架

### 1.1 代码规模

| 指标 | 数值 |
|------|------|
| 核心模块 Java 文件 | ~1641 个 |
| 核心模块代码行数 | ~312,000 行 |
| 支持数据库方言 | 29 种 |
| SQL AST 节点类型 | ~422 个（67 AST + 283 statement + 72 expr） |

## 2. 模块结构

```
druid-parent (pom)
├── core/                          # 核心模块（连接池 + SQL 解析器）
├── druid-spring-boot-starter/     # Spring Boot 2.x 自动配置
├── druid-spring-boot-3-starter/   # Spring Boot 3.x 自动配置
├── druid-spring-boot-4-starter/   # Spring Boot 4.x 自动配置
├── druid-wrapper/                 # 兼容层（C3P0/DBCP/Proxool 接口适配）
├── druid-admin/                   # 管理控制台
└── druid-demo-petclinic/          # 示例应用
```

### 2.1 核心模块 (core) 内部结构

```
com.alibaba.druid
├── DbType.java                 # 数据库类型枚举（30+ 种）
├── sql/
│   ├── SQLUtils.java           # SQL 工具入口（格式化、解析、转换）
│   ├── parser/                 # ★ SQL 解析器核心
│   ├── ast/                    # ★ 抽象语法树节点定义
│   ├── visitor/                # ★ AST 访问器（输出、统计、参数化）
│   ├── dialect/                # ★ 29 种数据库方言实现
│   ├── repository/             # Schema 仓库（元数据管理）
│   └── template/               # SQL 模板
├── pool/                       # JDBC 连接池
├── filter/                     # Filter-Chain 拦截器
├── wall/                       # SQL 防火墙（防注入）
├── stat/                       # 监控统计
├── proxy/                      # JDBC 代理层
└── support/                    # 基础支持（日志、JMX 等）
```

---

## 3. SQL 解析器架构（核心重点）

SQL 解析是 Druid 最核心的功能之一。整体采用 **手写递归下降解析器** 架构，分为四层：

```
┌──────────────────────────────────────────────────────┐
│                    用户 API 层                         │
│           SQLUtils / SQLParserUtils                   │
├──────────────────────────────────────────────────────┤
│                  Visitor 访问器层                       │
│  SQLASTOutputVisitor / SchemaStatVisitor / WallVisitor│
├──────────────────────────────────────────────────────┤
│              AST 抽象语法树层                           │
│   SQLStatement / SQLExpr / SQLTableSource / ...       │
├──────────────────────────────────────────────────────┤
│                  Parser 解析器层                        │
│  SQLStatementParser / SQLExprParser / SQLSelectParser  │
├──────────────────────────────────────────────────────┤
│                  Lexer 词法分析层                       │
│        Lexer / Token / Keywords / CharTypes            │
└──────────────────────────────────────────────────────┘
```

### 3.1 Lexer 词法分析器

**文件**: `sql/parser/Lexer.java` (~3,534 行)

Lexer 是整个解析器的底层，负责将 SQL 字符串切割为 Token 流。

#### 核心设计

```
SQL 字符串 → Lexer.nextToken() → Token 枚举值 + stringVal/numberVal
```

**关键字段：**
- `text` (String): 原始 SQL 文本
- `pos` (int): 当前扫描位置
- `ch` (char): 当前字符
- `token` (Token): 当前 Token 类型
- `stringVal` (String): 当前 Token 的字符串值
- `hashLCase` (long): 当前标识符的 FNV hash（用于快速比较）
- `keywords` (Keywords): 关键字表

**关键特性：**
- 使用 FNV-1a 64-bit hash 进行关键字和标识符的快速匹配
- `SavePoint` 机制支持词法级别的回溯（mark/reset）
- `DialectFeature` 位掩码控制方言差异行为
- 支持 `optimizedForParameterized` 模式进行参数化优化

#### Token 类型

**文件**: `sql/parser/Token.java` (~420 行)

定义了所有 SQL Token 类型：
- SQL 关键字: `SELECT`, `FROM`, `WHERE`, `JOIN`, ...
- 字面量: `LITERAL_INT`, `LITERAL_FLOAT`, `LITERAL_CHARS`, `LITERAL_ALIAS`
- 运算符: `PLUS`, `MINUS`, `STAR`, `SLASH`, `EQ`, `GT`, `LT`, ...
- 分隔符: `LPAREN`, `RPAREN`, `COMMA`, `DOT`, `SEMI`, ...

#### 方言 Lexer 继承体系

```
Lexer (基类)
├── MySqlLexer          # MySQL 词法（反引号标识符、特殊字符串）
├── OracleLexer         # Oracle 词法（Q-Quote、PL/SQL 语法）
├── PGLexer             # PostgreSQL 词法（$$字符串、类型转换）
├── SQLServerLexer      # SQL Server 词法（方括号标识符）
├── HiveLexer           # Hive 词法
├── OdpsLexer           # MaxCompute 词法
├── ClickHouse(CK)Lexer # ClickHouse 词法
├── DB2Lexer            # DB2 词法
├── SparkLexer          # Spark 词法
├── StarRocksLexer      # StarRocks 词法
├── DorisLexer          # Doris 词法
├── BigQueryLexer       # BigQuery 词法
├── ... (共 20+ 种方言 Lexer)
└── SnowflakeLexer      # Snowflake 词法
```

每个方言 Lexer 通过以下方式定制行为：
1. **静态 DialectFeature 实例**: 定义词法特性位掩码
2. **自定义 Keywords 表**: 添加方言特有关键字
3. **Override `scanString()`** 等方法: 处理特殊字符串语法

### 3.2 Parser 解析器

解析器层采用 **手写递归下降** 方式，核心类如下：

```
SQLParser (基类, ~940 行)
├── SQLExprParser (表达式解析, ~6,445 行)
│   ├── SQLStatementParser (语句解析, ~7,953 行)
│   └── SQLSelectParser (查询解析, ~2,485 行)
│       └── SQLCreateTableParser (建表解析)
│           └── SQLDDLParser (DDL 解析)
```

#### 3.2.1 SQLParser 基类

**文件**: `sql/parser/SQLParser.java` (~940 行)

提供基础功能：
- 持有 `Lexer` 实例
- `accept(Token)`: 期望并消费指定 Token
- `tableAlias()` / `as()` / `alias()`: 别名解析（含大量回溯逻辑）
- `dialectFeatureEnabled()`: 方言特性检查

#### 3.2.2 SQLExprParser 表达式解析器

**文件**: `sql/parser/SQLExprParser.java` (~6,445 行)

负责解析所有 SQL 表达式，是最复杂的解析器之一。

**核心方法调用链：**
```
expr()
  → primary()           # 解析主表达式（字面量、标识符、函数调用）
  → relational()        # 解析关系表达式（比较操作）
  → additive()          # 解析加减表达式
  → multiplicative()    # 解析乘除表达式
  → unary()             # 解析一元表达式
  → primaryRest()       # 处理后缀操作（.属性、[下标]、IS NULL 等）
  → relationalRest()    # 处理关系操作后续（AND、OR、BETWEEN 等）
```

**关键特性：**
- 使用 FNV hash 值进行快速函数名匹配
- 大量 `switch-case` 分支处理不同 Token
- `primary()` 方法单函数超过 1500 行

#### 3.2.3 SQLStatementParser 语句解析器

**文件**: `sql/parser/SQLStatementParser.java` (~7,953 行)

负责解析完整 SQL 语句。

**核心方法：**
- `parseStatementList()`: 解析语句列表（入口方法）
- `parseSelect()` / `parseInsert()` / `parseUpdate()` / `parseDelete()`
- `parseCreate()` / `parseAlter()` / `parseDrop()`
- `parseSet()` / `parseGrant()` / `parseRevoke()`

#### 3.2.4 SQLSelectParser 查询解析器

**文件**: `sql/parser/SQLSelectParser.java` (~2,485 行)

专门处理 SELECT 语句的解析。

**核心方法：**
- `select()`: 解析完整 SELECT（含 WITH、ORDER BY、LIMIT）
- `query()`: 解析查询体（UNION/INTERSECT/EXCEPT）
- `queryBlock()`: 解析单个查询块（SELECT...FROM...WHERE...）
- `parseTableSource()`: 解析表源（含 JOIN）
- `parseFrom()` / `parseWhere()` / `parseGroupBy()`

#### 3.2.5 方言 Parser 继承体系

以 MySQL 为例：

```
SQLExprParser → MySqlExprParser (~2,689 行)
  - 处理 MySQL 特有表达式（BINARY、MATCH AGAINST 等）

SQLStatementParser → MySqlStatementParser (~9,398 行)
  - 处理 MySQL 特有语句（SHOW、FLUSH、HANDLER 等）

SQLSelectParser → MySqlSelectParser
  - 处理 MySQL 特有查询语法（INTO OUTFILE、LOCK IN SHARE MODE 等）

SQLCreateTableParser → MySqlCreateTableParser
  - 处理 MySQL 特有建表语法（ENGINE、CHARSET 等）
```

#### 3.2.6 SQLParserUtils 工厂

**文件**: `sql/parser/SQLParserUtils.java` (~1,162 行)

根据 `DbType` 创建对应方言的 Parser：

```java
// 典型使用方式
SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
List<SQLStatement> stmts = parser.parseStatementList();
```

内部通过巨大的 `if-else if` 链匹配 29 种数据库类型并创建对应 Parser。

### 3.3 AST 抽象语法树

AST 是解析器的输出，也是 Visitor 的输入。

#### 核心接口/基类

```
SQLObject (接口)
└── SQLObjectImpl (抽象基类)
    ├── SQLExprImpl → SQLExpr 接口
    │   ├── SQLIdentifierExpr      # 标识符
    │   ├── SQLPropertyExpr        # 属性表达式 (a.b)
    │   ├── SQLBinaryOpExpr        # 二元操作表达式
    │   ├── SQLMethodInvokeExpr    # 函数调用
    │   ├── SQLCharExpr            # 字符串字面量
    │   ├── SQLIntegerExpr         # 整数字面量
    │   ├── SQLNumberExpr          # 数值字面量
    │   ├── SQLInListExpr          # IN 列表
    │   ├── SQLBetweenExpr         # BETWEEN
    │   ├── SQLCaseExpr            # CASE WHEN
    │   ├── SQLCastExpr            # CAST
    │   ├── SQLQueryExpr           # 子查询表达式
    │   └── ... (72 种表达式节点)
    │
    ├── SQLStatementImpl → SQLStatement 接口
    │   ├── SQLSelectStatement
    │   ├── SQLInsertStatement
    │   ├── SQLUpdateStatement
    │   ├── SQLDeleteStatement
    │   ├── SQLCreateTableStatement
    │   ├── SQLAlterTableStatement
    │   ├── SQLDropTableStatement
    │   └── ... (283 种语句节点)
    │
    ├── SQLSelectQueryBlock          # SELECT 查询块
    ├── SQLUnionQuery                # UNION 查询
    ├── SQLJoinTableSource           # JOIN 表源
    ├── SQLSubqueryTableSource       # 子查询表源
    ├── SQLExprTableSource           # 表达式表源
    ├── SQLOrderBy / SQLLimit        # ORDER BY / LIMIT
    ├── SQLOver                      # 窗口函数
    ├── SQLDataTypeImpl              # 数据类型
    └── SQLPartitionBy*              # 分区定义
```

**关键设计：**
- 每个 AST 节点都实现 `accept(SQLASTVisitor)` 方法
- `parent` 指针支持向上遍历
- `attributes` Map 支持扩展属性
- `sourceLine`/`sourceColumn` 记录源码位置

### 3.4 Visitor 访问器

采用经典的 **Visitor 模式**，对 AST 进行遍历和操作。

#### 核心 Visitor

```
SQLASTVisitor (接口, ~2,882 行)
  - 为每种 AST 节点定义 visit/endVisit 方法对
  - 使用 Java 8 default 方法提供默认空实现

SQLASTVisitorAdapter (适配器)
  - 所有方法返回 true（继续遍历）

SQLASTOutputVisitor (~12,623 行)
  - SQL 输出/格式化（AST → SQL 字符串）
  - 是最大的单文件

SchemaStatVisitor (~3,341 行)
  - 提取表名、列名、条件等 Schema 信息

ExportParameterVisitor
  - 提取 SQL 参数

SQLEvalVisitor
  - SQL 表达式求值
```

#### 方言 Visitor

```
SQLASTOutputVisitor (基类)
├── MySqlOutputVisitor   (~5,713 行)
├── OracleOutputVisitor  (~2,999 行)
├── PGOutputVisitor
├── SQLServerOutputVisitor
├── HiveOutputVisitor
├── StarRocksOutputVisitor
├── DorisOutputVisitor
├── ... (共 20+ 种方言 OutputVisitor)
```

每个方言 OutputVisitor 覆写基类方法以输出方言特定语法。

### 3.5 DialectFeature 机制

**文件**: `sql/parser/DialectFeature.java`

使用位掩码实现高效的方言特性开关：

```java
public class DialectFeature {
    private long lexerFeature;    // Lexer 层特性
    private long parserFeature;   // Parser 层特性
    
    enum LexerFeature implements Feature {
        ScanSQLTypeBlockComment,
        ScanString2PutDoubleBackslash,
        NextTokenPrefixN,
        // ... 20+ 特性
    }
    
    enum ParserFeature implements Feature {
        JoinRightTableWith,
        PostNaturalJoin,
        GroupByPostDesc,
        AcceptUnion,
        // ... 40+ 特性
    }
}
```

每个方言通过静态 `DialectFeature` 实例声明支持的特性组合。

---

## 4. 连接池架构

```
DruidAbstractDataSource (抽象基类, ~2,388 行)
└── DruidDataSource (~3,984 行)
    ├── 连接创建线程 (CreateConnectionThread)
    ├── 连接销毁线程 (DestroyConnectionThread)
    ├── 连接保活线程 (KeepAliveTask)
    └── 连接池数组 (DruidConnectionHolder[])
```

### 4.1 Filter-Chain 机制

```
Filter (接口)
├── FilterAdapter (空实现)
│   └── FilterEventAdapter (事件适配器)
│       ├── StatFilter         # 统计监控
│       ├── WallFilter         # SQL 防火墙
│       ├── LogFilter          # 日志
│       ├── EncodingFilter     # 编码转换
│       └── ConfigFilter       # 配置管理

FilterChain (接口)
└── FilterChainImpl           # 责任链实现
```

**FilterChain 调用流程：**
```
应用代码 → DruidDataSource.getConnection()
  → FilterChainImpl.connection_connect()
    → Filter1.connection_connect()
      → Filter2.connection_connect()
        → 实际 JDBC 连接
```

---

## 5. SQL 防火墙 (Wall)

```
WallFilter → WallProvider
  ├── WallConfig (规则配置)
  ├── WallVisitor (SQL 检查 Visitor)
  │   ├── MySqlWallVisitor
  │   ├── OracleWallVisitor
  │   └── PGWallVisitor
  └── WallCheckResult (检查结果)
```

利用 SQL 解析器的 AST 进行安全检查，防止 SQL 注入。

---

## 6. 监控统计

```
StatFilter → JdbcStatManager
  ├── JdbcDataSourceStat    # 数据源统计
  ├── JdbcConnectionStat    # 连接统计
  ├── JdbcStatementStat     # 语句统计
  ├── JdbcSqlStat           # SQL 统计
  └── JdbcResultSetStat     # 结果集统计

DruidStatService → HTTP API
DruidStatManagerFacade → JMX 暴露
```

---

## 7. Spring Boot 集成

```
DruidDataSourceAutoConfigure
├── DruidDataSourceWrapper         # DataSource 包装
├── DruidFilterConfiguration       # Filter 自动配置
├── DruidSpringAopConfiguration    # AOP 监控
├── DruidStatViewServletConfiguration  # 监控页面
└── DruidWebStatFilterConfiguration    # Web 统计
```

---

## 8. 数据流全景

```
                        ┌─────────────────────────┐
     SQL 字符串         │   SQLUtils.parseStatements()  │
         │              │   SQLParserUtils.createXxx()   │
         ▼              └─────────────────────────┘
    ┌──────────┐                    │
    │  Lexer   │  Token 流          │ 根据 DbType 选择方言
    └──────────┘                    │
         │                          ▼
         ▼              ┌──────────────────────┐
    ┌──────────┐        │  方言 StatementParser  │
    │  Parser  │───────▶│  方言 ExprParser       │
    └──────────┘        │  方言 SelectParser     │
         │              └──────────────────────┘
         ▼
    ┌──────────┐
    │   AST    │  抽象语法树
    └──────────┘
         │
    ┌────┼────┬────────┬──────────┐
    │         │        │          │
    ▼         ▼        ▼          ▼
 输出SQL   统计分析  防火墙检查  参数化
 (Output   (Schema  (Wall      (Parameter
  Visitor)  Stat)   Visitor)    ized)
```

---

## 9. 支持的数据库方言（29 种）

| 方言 | Lexer | ExprParser | StatementParser | OutputVisitor |
|------|-------|------------|-----------------|---------------|
| MySQL | MySqlLexer | MySqlExprParser | MySqlStatementParser | MySqlOutputVisitor |
| Oracle | OracleLexer | OracleExprParser | OracleStatementParser | OracleOutputVisitor |
| PostgreSQL | PGLexer | PGExprParser | PGSQLStatementParser | PGOutputVisitor |
| SQL Server | SQLServerLexer | SQLServerExprParser | SQLServerStatementParser | SQLServerOutputVisitor |
| DB2 | DB2Lexer | DB2ExprParser | DB2StatementParser | DB2OutputVisitor |
| Hive | HiveLexer | HiveExprParser | HiveStatementParser | HiveOutputVisitor |
| ClickHouse | CKLexer | CKExprParser | CKStatementParser | CKOutputVisitor |
| ODPS/MaxCompute | OdpsLexer | OdpsExprParser | OdpsStatementParser | OdpsOutputVisitor |
| Spark | SparkLexer | SparkExprParser | SparkStatementParser | SparkOutputASTVisitor |
| StarRocks | StarRocksLexer | StarRocksExprParser | StarRocksStatementParser | StarRocksOutputVisitor |
| Doris | DorisLexer | DorisExprParser | DorisStatementParser | DorisOutputVisitor |
| BigQuery | BigQueryLexer | BigQueryExprParser | BigQueryStatementParser | BigQueryOutputVisitor |
| Presto/Trino | PrestoLexer | PrestoExprParser | PrestoStatementParser | PrestoOutputVisitor |
| Redshift | RedshiftLexer | RedshiftExprParser | RedshiftStatementParser | RedshiftOutputVisitor |
| H2 | H2Lexer | H2ExprParser | H2StatementParser | H2OutputVisitor |
| GaussDB | GaussDbLexer | GaussDbExprParser | GaussDbStatementParser | GaussDbOutputVisitor |
| Hologres | HologresLexer | HologresExprParser | HologresStatementParser | HologresOutputVisitor |
| Oscar | OscarLexer | OscarExprParser | OscarStatementParser | OscarOutputVisitor |
| Phoenix | PhoenixLexer | PhoenixExprParser | PhoenixStatementParser | PhoenixOutputVisitor |
| Impala | ImpalaLexer | ImpalaExprParser | ImpalaStatementParser | ImpalaOutputVisitor |
| Snowflake | SnowflakeLexer | SnowflakeExprParser | SnowflakeStatementParser | - |
| Databricks | DatabricksLexer | DatabricksExprParser | DatabricksStatementParser | DatabricksOutputVisitor |
| Athena | AthenaLexer | AthenaExprParser | AthenaStatementParser | AthenaOutputVisitor |
| Teradata | TDLexer | TDExprParser | TDStatementParser | TDOutputVisitor |
| SuperSQL | SuperSqlLexer | SuperSqlExprParser | SuperSqlStatementParser | SuperSqlOutputVisitor |
| Synapse | SynapseLexer | SynapseExprParser | SynapseStatementParser | SynapseOutputVisitor |
| Blink | - | - | BlinkStatementParser | BlinkOutputVisitor |
| Informix | - | - | InformixStatementParser | InformixOutputVisitor |
| DM(达梦) | (使用通用 + DM Keywords) | - | - | - |

---

## 10. 关键设计模式

| 模式 | 应用位置 | 说明 |
|------|---------|------|
| **Visitor 模式** | AST 遍历 | 所有 AST 操作（输出、统计、检查）均通过 Visitor |
| **工厂方法** | SQLParserUtils | 根据 DbType 创建方言 Parser |
| **模板方法** | Parser 继承体系 | 基类定义解析骨架，子类覆写差异 |
| **责任链** | Filter-Chain | 多个 Filter 串联处理 JDBC 操作 |
| **策略模式** | DialectFeature | 位掩码控制不同方言行为 |
| **递归下降** | 所有 Parser | 手写递归下降解析器 |


结果对比（avgt，越小越好）

• 当前分支：2.244 ± 3.614 s/op
• origin/master：2.388 ± 3.622 s/op
• 差异：当前分支约 快 6.03%（(2.244-2.388)/2.388）
