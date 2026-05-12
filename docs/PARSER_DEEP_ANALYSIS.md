# Druid SQL 解析器深度分析：多级继承架构与 Feature 命名优化

---

## 第一部分：多级继承架构分析

### 1. 当前继承体系全景图

#### 1.1 Lexer 继承树

```
Lexer (3,534 行)
├── MySqlLexer                      # MySQL / MariaDB / TiDB / ...
├── OracleLexer                     # Oracle
├── PGLexer                         # PostgreSQL / EDB / Greenplum
│   └── GaussDbLexer                # GaussDB (继承 PG)
├── SQLServerLexer                  # SQL Server
│   └── SynapseLexer                # Synapse (继承 SQLServer)
├── HiveLexer                       # Hive
│   ├── OdpsLexer                   # ODPS/MaxCompute (继承 Hive)
│   ├── ImpalaLexer                 # Impala (继承 Hive)
│   └── SparkLexer                  # Spark (继承 Hive)
│       └── DatabricksLexer         # Databricks (继承 Spark)
├── PrestoLexer                     # Presto / Trino
│   ├── AthenaLexer                 # Athena (继承 Presto)
│   └── SuperSqlLexer               # SuperSQL (继承 Presto)
├── DB2Lexer                        # DB2
├── CKLexer                         # ClickHouse
├── H2Lexer                         # H2
├── OscarLexer                      # Oscar
├── PhoenixLexer                    # Phoenix
├── BigQueryLexer                   # BigQuery
├── StarRocksLexer                  # StarRocks
│   └── DorisLexer                  # Doris (继承 StarRocks)
├── TDLexer                         # Teradata
├── SnowflakeLexer                  # Snowflake
├── BlinkLexer                      # Blink
├── HologresLexer                   # Hologres (注意：直接继承 Lexer，未继承 PGLexer)
└── RedshiftLexer                   # Redshift (注意：直接继承 Lexer，未继承 PGLexer)
```

#### 1.2 ExprParser 继承树

```
SQLExprParser (6,445 行)
├── MySqlExprParser                 # MySQL
├── OracleExprParser                # Oracle
├── PGExprParser                    # PostgreSQL
│   ├── GaussDbExprParser           # GaussDB (继承 PG)
│   ├── HologresExprParser          # Hologres (继承 PG)
│   └── RedshiftExprParser          # Redshift (继承 PG)
├── SQLServerExprParser             # SQL Server
│   └── SynapseExprParser           # Synapse (继承 SQLServer)
├── HiveExprParser                  # Hive
│   ├── OdpsExprParser              # ODPS (继承 Hive)
│   ├── ImpalaExprParser            # Impala (继承 Hive)
│   └── SparkExprParser             # Spark (继承 Hive)
│       └── DatabricksExprParser    # Databricks (继承 Spark)
├── PrestoExprParser                # Presto
│   ├── AthenaExprParser            # Athena (继承 Presto)
│   └── SuperSqlExprParser          # SuperSQL (继承 Presto)
├── DB2ExprParser                   # DB2
├── CKExprParser                    # ClickHouse
├── H2ExprParser                    # H2
├── OscarExprParser                 # Oscar
├── PhoenixExprParser               # Phoenix
├── BigQueryExprParser              # BigQuery
├── StarRocksExprParser             # StarRocks
│   └── DorisExprParser             # Doris (继承 StarRocks)
├── BlinkExprParser                 # Blink
├── TDExprParser                    # Teradata
└── SnowflakeExprParser             # Snowflake
```

#### 1.3 StatementParser 继承树

```
SQLStatementParser (7,953 行)
├── MySqlStatementParser (9,398 行!)  # MySQL
├── OracleStatementParser             # Oracle
├── PGSQLStatementParser              # PostgreSQL
│   └── GaussDbStatementParser        # GaussDB (继承 PG)
├── SQLServerStatementParser          # SQL Server
│   └── SynapseStatementParser        # Synapse (继承 SQLServer)
├── HiveStatementParser               # Hive
│   ├── OdpsStatementParser           # ODPS (继承 Hive)
│   ├── ImpalaStatementParser         # Impala (继承 Hive)
│   └── SparkStatementParser          # Spark (继承 Hive)
│       └── DatabricksStatementParser # Databricks (继承 Spark)
├── PrestoStatementParser             # Presto
│   ├── AthenaStatementParser         # Athena (继承 Presto)
│   └── SuperSqlStatementParser       # SuperSQL (继承 Presto)
├── DB2StatementParser                # DB2
├── CKStatementParser                 # ClickHouse
├── H2StatementParser                 # H2
├── OscarStatementParser              # Oscar (注意：在 visitor 包中！)
├── PhoenixStatementParser            # Phoenix
├── BigQueryStatementParser           # BigQuery
├── StarRocksStatementParser          # StarRocks
│   └── DorisStatementParser          # Doris (继承 StarRocks)
├── BlinkStatementParser              # Blink
├── TDStatementParser                 # Teradata
├── SnowflakeStatementParser          # Snowflake
├── InformixStatementParser           # Informix
├── HologresStatementParser           # Hologres (注意：直接继承基类，未继承 PG)
└── RedshiftStatementParser           # Redshift (注意：直接继承基类，未继承 PG)
```

---

### 2. 继承体系中发现的问题

#### 问题 2.1：继承树不一致

同一方言的三组件（Lexer、ExprParser、StatementParser）继承路径不一致：

| 方言 | Lexer 继承 | ExprParser 继承 | StatementParser 继承 | 问题 |
|------|-----------|-----------------|---------------------|------|
| **Hologres** | Lexer (直接) | **PGExprParser** | SQLStatementParser (直接) | Lexer 和 StmtParser 没继承 PG 系列，但 ExprParser 继承了 PG |
| **Redshift** | Lexer (直接) | **PGExprParser** | SQLStatementParser (直接) | 同上 |
| **Oscar** | Lexer (直接) | SQLExprParser (直接) | SQLStatementParser (直接) | Oscar 是 PG 兼容数据库，但未继承 PG 体系 |

这导致同一个方言在不同解析层级表现出不同的语法能力。例如，Hologres 的 ExprParser 继承了 PG 的表达式解析特性，但 StatementParser 却没有继承 PG 的语句解析能力。

#### 问题 2.2：OscarStatementParser 放在 visitor 包中

`OscarStatementParser` 位于 `sql/dialect/oscar/visitor/` 包下，而非按惯例放在 `sql/dialect/oscar/parser/` 包中。这是明显的包归属错误。

```
oscar/
├── parser/
│   ├── OscarLexer.java
│   ├── OscarExprParser.java
│   ├── OscarSelectParser.java
│   └── OscarCreateTableParser.java
└── visitor/
    ├── OscarStatementParser.java   ← 应该在 parser/ 包中
    ├── OscarOutputVisitor.java
    └── OscarASTVisitor.java
```

#### 问题 2.3：多级继承导致的钻石型语义风险

继承链最深达 4 级：

```
Lexer → HiveLexer → SparkLexer → DatabricksLexer
SQLExprParser → HiveExprParser → SparkExprParser → DatabricksExprParser
```

DatabricksLexer/ExprParser 本身几乎没有代码（仅 15-17 行），只是修改了 DbType。这说明继承被过度使用——应该通过配置（DialectFeature + Keywords）而非继承来区分方言。

类似的"空壳子类"还有：
- `DorisExprParser` (20 行) — 仅改 DbType 和 Lexer 类型
- `HologresExprParser` (21 行) — 仅改 DbType
- `AthenaExprParser` (很少改动) — 仅改 DbType 和 Lexer 类型

#### 问题 2.4：DialectFeature 与继承的二元机制冲突

方言差异通过两种机制表达：
1. **DialectFeature 位掩码** — 在 Lexer 中声明，通过 `dialectFeatureEnabled()` 检查
2. **类继承 + 方法 override** — 子类覆写父类方法

这两种机制同时存在，没有清晰的边界：
- 有些差异用 Feature（如 `ScanString2PutDoubleBackslash`）
- 有些差异用 override（如 `HiveLexer.scanString()`）
- 有些差异两者都用

**建议**: 建立明确的规则——简单的开关行为用 Feature，需要完全不同逻辑流的用 override。

#### 问题 2.5：基类 SQLStatementParser 包含方言特有逻辑

`SQLStatementParser` (7,953 行) 中直接导入和使用了方言特有类：

```java
// 基类中的方言引用
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveMsckRepairStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.FullTextType;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
```

这破坏了分层原则：通用基类不应依赖特定方言实现。`parseStatementList()` 开头甚至硬编码了 MySQL 的快速路径：

```java
if (lexer.token == Token.SELECT) {
    String[] words = lexer.text.split("\\s+");
    if (words.length == 2
            && "select".equalsIgnoreCase(words[0])
            && "@@session.tx_read_only".equalsIgnoreCase(words[1])) {
        // MySQL 特有的快速解析路径
    }
}
```

#### 问题 2.6：createSQLSelectParser() 的创建时机不统一

不同方言的 StatementParser 中，`createSQLSelectParser()` 的返回类型和创建方式各不相同，但基类没有强制约束，导致有些方言忘记覆写此方法。

---

### 3. 继承体系优化建议

#### 建议 3.1：统一方言组件继承路径

对于 PG 系列方言，确保三组件一致继承：

| 方言 | Lexer | ExprParser | StatementParser |
|------|-------|------------|-----------------|
| Hologres | **PGLexer** (改) | PGExprParser (已是) | **PGSQLStatementParser** (改) |
| Redshift | **PGLexer** (改) | PGExprParser (已是) | **PGSQLStatementParser** (改) |
| Oscar | **PGLexer** (改) | PGExprParser (改) | **PGSQLStatementParser** (改) |

#### 建议 3.2：消除空壳子类，改用配置

将仅修改 DbType 的空壳子类（如 `DatabricksExprParser`、`DorisExprParser`）替换为父类 + 配置参数：

```java
// Before: 4 级继承链仅为改 DbType
// SQLExprParser → HiveExprParser → SparkExprParser → DatabricksExprParser

// After: 通过工厂方法创建，传入不同配置
SparkExprParser parser = new SparkExprParser(sql, DbType.databricks, features);
```

#### 建议 3.3：修复 OscarStatementParser 包归属

将 `OscarStatementParser` 从 `oscar/visitor/` 移动到 `oscar/parser/`。

#### 建议 3.4：将基类中的方言逻辑下沉

将 `SQLStatementParser` 中的 MySQL/Hive 特有代码迁移到对应方言子类，基类只保留真正通用的解析逻辑。

---

## 第二部分：Feature 命名分析与修改建议

### 1. DialectFeature.LexerFeature（共 18 个）

#### 1.1 ScanSQLType 系列 — 用于 `Lexer.scanSQLType()` 快速判断 SQL 类型

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `ScanSQLTypeBlockComment` | 名称不清楚"ScanSQLType"是什么意思，不了解内部实现的人无法理解 | `Lexer.java:717` — 在 scanSQLType 中遇到 `/* */` 注释时特殊处理 | 扫描 SQL 类型时，允许跳过块注释继续判断 | `SqlTypeDetectSkipBlockComment` |
| `ScanSQLTypeWithSemi` | "With"含义模糊——是"支持分号"还是"遇到分号" | `Lexer.java:730` — 扫描 SQL 类型时跳过前导分号 | 扫描 SQL 类型时，跳过前导的多个分号 | `SqlTypeDetectSkipLeadingSemicolons` |
| `ScanSQLTypeWithFrom` | "WithFrom"含义模糊 | `Lexer.java:987` — 遇到 FROM 关键字识别为 INSERT_MULTI 类型 | 扫描 SQL 类型时，将以 FROM 开头的语句识别为多路插入 | `SqlTypeDetectFromAsMultiInsert` |
| `ScanSQLTypeWithFunction` | 同上模式 | `Lexer.java:993` — 遇到 FUNCTION 关键字识别为 SCRIPT 类型 | 扫描 SQL 类型时，将 FUNCTION 关键字识别为脚本类型 | `SqlTypeDetectFunctionAsScript` |
| `ScanSQLTypeWithBegin` | 同上模式 | `Lexer.java:995` — 遇到 BEGIN 关键字识别为 SCRIPT 类型 | 同上，BEGIN 识别为脚本 | `SqlTypeDetectBeginAsScript` |
| `ScanSQLTypeWithAt` | 同上模式 | `Lexer.java:999` — 遇到 @变量 的特殊处理 | 扫描 SQL 类型时，处理 @ 变量前缀 | `SqlTypeDetectAtVariable` |

**整体问题**: `ScanSQLType*` 前缀与实际行为关联度低，应统一为 `SqlTypeDetect*`，表明是"SQL 类型探测"阶段的特性。

#### 1.2 扫描相关 Feature

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `NextTokenColon` | "NextToken"是方法名片段，不是语义描述；不清楚"Colon"是输入还是输出 | `Lexer.java:1377` — 在 nextToken 中遇到单独的冒号 `:` 时，将其作为 COLON token 返回（而非报错） | 允许单独的冒号作为合法 Token | `AllowColonAsToken` |
| `NextTokenPrefixN` | 同上问题；"PrefixN"含义不清 | `Lexer.java:1502` — 在 nextToken 中遇到 `\N` 序列时特殊处理（MySQL 的 NULL 表示） | 允许 `\N` 作为 NULL 的快捷写法 | `AllowBackslashNAsNull` |
| `ScanString2PutDoubleBackslash` | 名称极不清晰："String2"是什么？"PutDoubleBackslash"描述的是实现细节而非语义 | `Lexer.java:1893,1899,2071,2077` — 扫描字符串时对 `%` 和 `_` 前加反斜杠转义 | 扫描字符串时，对通配符（% 和 _）添加转义反斜杠 | `StringEscapeWildcardChars` |
| `ScanAliasU` | "U"含义不明——指的是 Unicode 转义 `\uXXXX` | `Lexer.java:2227` — 扫描别名时支持 `\uXXXX` Unicode 转义序列 | 别名扫描中支持 Unicode 转义 | `AliasUnicodeEscape` |
| `ScanNumberPrefixB` | "PrefixB"不够明确 | `Lexer.java:2890` — 扫描数字时支持 `0b` 前缀（二进制字面量） | 支持 `0b` 二进制数字字面量 | `NumberBinaryLiteralPrefix` |
| `ScanNumberCommonProcess` | 名称极其模糊——"Common Process"毫无语义 | `Lexer.java:2991` — 扫描数字时遇到 `0b` 后面跟非数字字符的处理 | 数字扫描中对 `0b` 后续字符的通用处理 | `NumberBinaryLiteralPostProcess` |
| `ScanVariableAt` | 可以理解但不够精确 | `Lexer.java:2279` — 允许 `@` 作为变量前缀 | 允许 `@` 符号作为变量标识符前缀 | `VariablePrefixAt` |
| `ScanVariableGreaterThan` | 不清楚"GreaterThan"指什么 | `Lexer.java:2287` — 将 `@>` 识别为 `MONKEYS_AT_GT` token | 识别 `@>` 操作符（ODPS 特有） | `VariableAtGreaterThanOp` |
| `ScanVariableSkipIdentifiers` | "Skip"在什么情况下？ | `Lexer.java:2378` — 扫描变量时继续读取后续标识符字符 | 变量扫描时允许扩展读取标识符字符 | `VariableExtendIdentifierChars` |
| `ScanVariableMoveToSemi` | "MoveToSemi"描述的是实现行为 | `Lexer.java:2344` — 扫描变量时一直读到分号或中文分号为止 | 变量扫描时读取到分号为止 | `VariableReadUntilSemicolon` |
| `ScanHiveCommentDoubleSpace` | 名称中包含方言名"Hive"，不应在通用 Feature 中出现 | `HiveLexer.java:294` — 处理 Hive 风格的注释（`-- ` 需要两个空格后的特殊处理） | 双横线注释后需要空格才生效 | `CommentDoubleHyphenRequiresSpace` |
| `ScanSubAsIdentifier` | "Sub"指减号，但不够明确 | `Lexer.java:2691,2824` — 允许标识符中包含减号 `-`（如 `my-table`） | 允许减号作为标识符的一部分 | `IdentifierAllowHyphen` |

#### 1.3 LexerFeature 命名问题汇总

**最严重的命名问题** (完全无法从名称理解语义):
1. `ScanString2PutDoubleBackslash` — 实现细节暴露
2. `ScanNumberCommonProcess` — 含义完全模糊
3. `ScanSQLTypeBlockComment` — "ScanSQLType"是内部方法名

**包含方言名的问题**:
1. `ScanHiveCommentDoubleSpace` — Feature 命名不应包含方言名

**动词前缀不一致**:
- 有 `Scan*`、`NextToken*` 两种前缀，应统一为描述性名称

---

### 2. DialectFeature.ParserFeature（共 56 个）

#### 2.1 JOIN 相关（8 个）

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `AcceptUnion` | 过于笼统——不是"是否接受 UNION"，而是特定场景下的行为 | `SQLSelectParser.java:1214` — 括号内的子查询后是否允许接 UNION | 括号子查询后允许 UNION 操作 | `ParenthesizedQueryAllowUnion` |
| `QueryRestSemi` | "Rest"含义不清，"Semi"指分号但行为是什么? | `SQLSelectParser.java:174` — 在 queryRest 中遇到分号时停止解析 | 查询解析遇到分号时停止 | `QueryStopAtSemicolon` |
| `AsofJoin` | 驼峰不一致应为 `AsOfJoin`，但语义清楚 | `SQLSelectParser.java:1661` — 支持 ASOF JOIN 语法 | `AsOfJoin` (仅修正驼峰) |
| `GlobalJoin` | 语义清楚 | `SQLSelectParser.java:1679` — 支持 GLOBAL JOIN 语法 | 保持不变 |
| `JoinAt` | "At"含义不清——指 `join@hint` 语法 | `SQLSelectParser.java:1710` — 支持 `join@xxx` hint 语法 | `JoinAtHintSyntax` |
| `JoinRightTableWith` | 名称模糊——指 JOIN 右表可以是 WITH 子句 | `SQLSelectParser.java:1807` — JOIN 后的右表允许接 WITH 子查询 | `JoinRightSideAllowWith` |
| `JoinRightTableFrom` | 同上模式 | `SQLSelectParser.java:1808` — JOIN 后的右表允许接 FROM 子查询 | `JoinRightSideAllowFrom` |
| `JoinRightTableAlias` | 指 JOIN 右表在没有 ON 的情况下设置别名 | `SQLSelectParser.java:1948` — 无 ON 子句时强制右表需要 AS 别名 | `JoinRightSideRequireAliasWithoutOn` |
| `PostNaturalJoin` | "Post"含义模糊 | `SQLSelectParser.java:2015` — 在解析到 NATURAL 别名后回退并识别为 NATURAL JOIN | `NaturalJoinAfterAliasFallback` |
| `MultipleJoinOn` | 语义清楚 | `SQLSelectParser.java:2037` — 允许多个 ON 子句 | 保持不变 |
| `UDJ` | 缩写——User Defined Join，但没有注释说明 | `SQLSelectParser.java:2044` — 支持用户自定义 JOIN（ODPS 特有） | `UserDefinedJoin` |
| `TwoConsecutiveUnion` | 语义较清楚但可优化 | `SQLSelectParser.java:199` — 跳过连续的两个 UNION 关键字 | `SkipConsecutiveDuplicateUnion` |

#### 2.2 查询/SELECT 相关

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `QueryTable` | 过于模糊——指 `SELECT TABLE` 语法 | `SQLSelectParser.java:471` — 支持 `TABLE tableName` 作为查询语法 | `AllowTableAsQuery` |
| `GroupByAll` | 语义较清楚 | `SQLSelectParser.java:904` — 支持 GROUP BY ALL 语法 | 保持不变 |
| `RewriteGroupByCubeRollupToFunction` | 名称太长但语义清楚——将 GROUP BY CUBE/ROLLUP 改写为函数调用 | `SQLSelectParser.java:961` | `GroupByCubeRollupAsFunction` |
| `GroupByPostDesc` | "Post"不够精确 | `SQLSelectParser.java:1026` — GROUP BY 后允许 DESC 关键字（并跳过） | `GroupByIgnoreDesc` |
| `GroupByItemOrder` | 与 GroupByPostDesc 容易混淆 | `SQLSelectParser.java:1108` — GROUP BY 项后允许 DESC 关键字 | `GroupByItemIgnoreOrder` |
| `ParseSelectItemPrefixX` | 名称不清——指 `x'hex'` 或 `X'hex'` 十六进制字面量 | `SQLExprParser.java:5725` — 在 SELECT 项中不将 `x'...'` 作为标识符 | `SelectItemHexStringPrefix` |
| `ParseLimitBy` | 较清楚 | `SQLExprParser.java:6246` — 支持 `LIMIT ... BY ...` 语法（ClickHouse 特有） | `LimitByClause` |

#### 2.3 表达式解析相关

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `SQLDateExpr` | 名称与 AST 类名 `SQLDateExpr` 完全相同，极易混淆——这是 Feature 而非类 | `SQLExprParser.java:547` — 支持 `DATE '2024-01-01'` 字面量语法 | `DateLiteralExpression` |
| `SQLTimestampExpr` | 同上问题 | `SQLExprParser.java:555` — 支持 `TIMESTAMP '...'` 字面量语法 | `TimestampLiteralExpression` |
| `PrimaryVariantColon` | "Primary"指 `primary()` 方法，"VariantColon"指变量冒号——纯实现术语 | `SQLExprParser.java:654` — 支持 `:变量名` 语法（Oracle 绑定变量） | `BindVariableColonPrefix` |
| `PrimaryBangBangSupport` | "BangBang"指 `!!`，"Primary"是内部方法名 | `SQLExprParser.java:1060` — 支持 `!!` 操作符 | `DoubleBangOperator` |
| `PrimaryTwoConsecutiveSet` | 极其晦涩——"TwoConsecutiveSet"指 `@@SET` | `SQLExprParser.java:1127` — 在 `@@` 之后允许 SET 关键字 | `DoubleAtAllowSetKeyword` |
| `PrimaryLbraceOdbcEscape` | "Lbrace"指左花括号，"OdbcEscape"较清楚 | `SQLExprParser.java:1168` — 支持 `{expr}` ODBC 转义语法 | `OdbcBraceEscapeSyntax` |
| `ParseAllIdentifier` | "All"含义不清 | `SQLExprParser.java:1359` — 允许 ALL 关键字作为标识符解析 | `AllowAllAsIdentifier` |
| `PrimaryRestCommaAfterLparen` | 名称暴露实现细节（方法名 + 位置描述） | `SQLExprParser.java:1600` — 函数调用括号内允许前导逗号 | `FunctionCallAllowLeadingComma` |
| `InRestSpecificOperation` | "Specific Operation"完全无法理解 | `SQLExprParser.java:3212,3220` — IN 列表中连续字符串字面量的特殊处理 | `InListSkipConsecutiveStringLiterals` |
| `AdditiveRestPipesAsConcat` | 较清楚但 "Rest" 是实现术语 | `SQLExprParser.java:3366` — 将 `||` 视为字符串拼接操作符 | `PipesAsConcatOperator` |

#### 2.4 赋值项解析（parseAssignItem）

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `ParseAssignItemRparenCommaSetReturn` | 极长 + 暴露方法名和 Token 序列 | `SQLExprParser.java:5328` — 解析赋值项时，遇到 `)` 或 `,` 或 `SET` 则返回 | `AssignItemStopAtRParenCommaSet` |
| `ParseAssignItemEqSemiReturn` | 同上问题 | `SQLExprParser.java:5333` — 赋值项等号后遇分号则返回 | `AssignItemStopAtSemiAfterEq` |
| `ParseAssignItemSkip` | "Skip"什么不清楚 | `SQLExprParser.java:5336` — 赋值项中跳过某些 token | `AssignItemSkipUnexpectedTokens` |
| `ParseAssignItemEqeq` | "Eqeq"不直观——指 `==` | `SQLExprParser.java:5343` — 赋值项中允许 `==` 替代 `=` | `AssignItemAllowDoubleEquals` |

#### 2.5 语句列表解析（parseStatementList）

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `ParseStatementListWhen` | "When"指什么不清楚——与 CASE WHEN 无关 | `SQLStatementParser.java:167` — 在语句列表中遇到 END 时，检查是否为 IF/BLOCK 结束 | `StmtListEndTokenCheckIfBlock` |
| `ParseStatementListSelectUnsupportedSyntax` | 名称过长但含义也不准确 | `SQLStatementParser.java:228` — 连续两条 SELECT 之间没有分号则报错 | `StmtListRequireSemiBetweenSelects` |
| `ParseStatementListUpdatePlanCache` | 包含 MySQL 特有概念 "PlanCache" | `SQLStatementParser.java:254` — 支持 `UPDATE PLANCACHE` 语法 | `UpdatePlanCacheSyntax` |
| `ParseStatementListRollbackReturn` | "Return"指什么不清楚 | `SQLStatementParser.java:550` — 在 BLOCK 语句中遇到 ROLLBACK 后返回（结束块解析） | `BlockStmtStopAtRollback` |
| `ParseStatementListCommitReturn` | 同上 | `SQLStatementParser.java:570` — 在 BLOCK 语句中遇到 COMMIT 后返回 | `BlockStmtStopAtCommit` |
| `ParseStatementListLparenContinue` | "LparenContinue"——左括号后继续 | `SQLStatementParser.java:617` — 遇到左括号包裹的非语句时，跳过右括号继续 | `StmtListSkipParenthesizedNonStmt` |

#### 2.6 DDL 相关

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `ParseRevokeFromUser` | 较清楚 | `SQLStatementParser.java:2041` — REVOKE 语句中支持 FROM USER 语法 | `RevokeAllowFromUser` |
| `ParseDropTableTables` | "Tables"含义不清 | `SQLStatementParser.java:3317` — DROP 语句支持 `TABLES` 关键字替代 `TABLE` | `DropAllowTablesKeyword` |
| `ParseCreateSql` | "Sql"不清楚指什么 | `SQLStatementParser.java:4153` — CREATE 语句中支持 `SQL` 关键字（作为 CREATE SQL FUNCTION） | `CreateSqlFunction` |
| `CreateTableBodySupplemental` | 较清楚 | `SQLCreateTableParser.java:120` — 建表语句中支持 SUPPLEMENTAL LOGGING（Oracle 特有） | `CreateTableSupplementalLogging` |

#### 2.7 别名解析相关（tableAlias / as）

| 当前名称 | 问题 | 使用位置 | 实际语义 | 建议新名称 |
|---------|------|---------|---------|-----------|
| `TableAliasConnectWhere` | 不清楚 "ConnectWhere" 组合的含义 | `SQLParser.java:97` — WHERE 关键字不能作为表别名（CONNECT BY 场景中） | `TableAliasDisallowWhere` |
| `TableAliasAsof` | 驼峰应为 `TableAliasAsOf` | `SQLParser.java:185` — ASOF 关键字不能作为表别名（ClickHouse ASOF JOIN） | `TableAliasDisallowAsOf` |
| `TableAliasLock` | 含义模糊——LOCK 能否作为别名 | `SQLParser.java:257` — 允许 LOCK 等关键字作为表别名 | `TableAliasAllowLockKeyword` |
| `TableAliasPartition` | 含义模糊 | `SQLParser.java:264` — PARTITION 关键字在非函数调用时可作为别名 | `TableAliasAllowPartitionKeyword` |
| `TableAliasTable` | "Table"本身不是好名称 | `SQLParser.java:276` — TABLE 关键字在特定上下文可作为别名 | `TableAliasAllowTableKeyword` |
| `TableAliasBetween` | 含义模糊 | `SQLParser.java:336` — BETWEEN/LIMIT/MINUS/EXCEPT 可作为别名 | `TableAliasAllowBetweenKeyword` |
| `TableAliasRest` | "Rest"完全没有意义 | `SQLParser.java:385` — 在 must=true 时，额外的关键字别名处理 | `TableAliasFallbackKeywords` |
| `AsCommaFrom` | 含义极度不清 | `SQLParser.java:465` — AS 后遇到逗号或 FROM 返回 null（Oracle 特有行为） | `AsClauseNullOnCommaOrFrom` |
| `AsSkip` | "Skip"什么不清楚 | `SQLParser.java:495` — AS 别名中跳过 TBLPROPERTIES 关键字（Hive 特有） | `AsSkipTblProperties` |
| `AsSequence` | 含义模糊——SEQUENCE 能否做别名 | `SQLParser.java:579` — 允许 CLOSE/SEQUENCE 关键字作为列别名 | `AsAllowSequenceKeyword` |
| `AsDatabase` | 含义模糊 | `SQLParser.java:599` — 允许 DATABASE/RIGHT/LEFT/NULL 等作为列别名 | `AsAllowDatabaseKeyword` |
| `AsDefault` | 含义模糊 | `SQLParser.java:609` — 允许 GROUP/ORDER/DEFAULT/DISTRIBUTE 作为别名（除非后跟 BY） | `AsAllowDefaultKeyword` |
| `AliasLiteralFloat` | 较清楚 | `SQLParser.java:653` — 允许浮点数字面量作为别名 | `AliasAllowFloatLiteral` |

---

### 3. SQLParserFeature 命名分析（共 28 个）

| 当前名称 | 问题 | 建议新名称 |
|---------|------|-----------|
| `KeepInsertValueClauseOriginalString` | 可以但偏长 | 保持不变 |
| `KeepSelectListOriginalString` | 可以 | 保持不变 |
| `UseInsertColumnsCache` | 清楚 | 保持不变 |
| `EnableSQLBinaryOpExprGroup` | 前缀 "SQL" 多余 | `EnableBinaryOpExprGroup` |
| `OptimizedForParameterized` | 清楚 | 保持不变 |
| `OptimizedForForParameterizedSkipValue` | **拼写错误**: "ForFor" 有两个 For | `OptimizedForParameterizedSkipValue` |
| `KeepComments` | 清楚 | 保持不变 |
| `SkipComments` | 清楚 | 保持不变 |
| `StrictForWall` | 清楚 | 保持不变 |
| `TDDLHint` | 缩写不清——TDDL 是阿里内部中间件 | `TddlHintSupport` |
| `DRDSAsyncDDL` | 缩写不清——DRDS 是阿里内部分布式数据库 | `DrdsAsyncDdlSupport` |
| `DRDSBaseline` | 同上 | `DrdsBaselineSupport` |
| `InsertReader` | 含义模糊——做什么的 Reader? | `InsertStreamReader` |
| `IgnoreNameQuotes` | 清楚 | 保持不变 |
| `KeepNameQuotes` | 清楚 | 保持不变 |
| `SelectItemGenerateAlias` | 清楚 | 保持不变 |
| `PipesAsConcat` | 清楚 | 保持不变 |
| `InsertValueCheckType` | 含义模糊——检查什么类型? | `InsertValueTypeValidation` |
| `InsertValueNative` | 含义模糊——"Native"指什么? | `InsertValueNativeHandler` |
| `EnableCurrentTimeExpr` | 清楚 | 保持不变 |
| `EnableCurrentUserExpr` | 清楚 | 保持不变 |
| `KeepSourceLocation` | 清楚 | 保持不变 |
| `SupportUnicodeCodePoint` | 清楚 | 保持不变 |
| `PrintSQLWhileParsingFailed` | "While"有歧义（when/during） | `PrintSqlOnParseFailure` |
| `EnableMultiUnion` | 清楚 | 保持不变 |
| `Spark` | **太通用**——不清楚启用什么行为 | `SparkDialectMode` |
| `Presto` | **太通用**——同上 | `PrestoDialectMode` |
| `MySQLSupportStandardComment` | 清楚但大小写不一致 | `MysqlStandardCommentSupport` |
| `Template` | **太通用** | `SqlTemplateMode` |

---

## 第三部分：Feature 命名规范建议

### 命名规则

建议所有 Feature 遵循以下命名规范：

```
[领域][动作/属性][对象]
```

**领域前缀规则**:

| 前缀 | 含义 | 示例 |
|------|------|------|
| `SqlTypeDetect*` | SQL 类型快速探测 | `SqlTypeDetectSkipBlockComment` |
| `String*` / `Number*` | 字符串/数字字面量扫描 | `StringEscapeWildcardChars` |
| `Variable*` | 变量扫描 | `VariablePrefixAt` |
| `Identifier*` | 标识符扫描 | `IdentifierAllowHyphen` |
| `Alias*` / `As*` / `TableAlias*` | 别名解析 | `AliasAllowFloatLiteral` |
| `Join*` | JOIN 解析 | `JoinRightSideAllowWith` |
| `GroupBy*` | GROUP BY 解析 | `GroupByIgnoreDesc` |
| `Query*` | 查询解析 | `QueryStopAtSemicolon` |
| `StmtList*` / `BlockStmt*` | 语句列表解析 | `StmtListRequireSemiBetweenSelects` |
| `AssignItem*` | 赋值项解析 | `AssignItemStopAtSemiAfterEq` |
| `Create*` / `Drop*` / `Revoke*` | DDL/DCL 解析 | `CreateTableSupplementalLogging` |

**动作/属性关键词**:

| 关键词 | 含义 | 示例 |
|--------|------|------|
| `Allow*` | 允许某种语法 | `AllowColonAsToken` |
| `Disallow*` | 禁止某种语法 | `TableAliasDisallowWhere` |
| `Skip*` / `Ignore*` | 跳过/忽略 | `GroupByIgnoreDesc` |
| `StopAt*` | 遇到某处停止 | `QueryStopAtSemicolon` |
| `Require*` | 要求某种语法 | `JoinRightSideRequireAliasWithoutOn` |
| `*AsX` | 将 A 识别为 X | `SqlTypeDetectFromAsMultiInsert` |

### 禁止事项

1. **不要用内部方法名做前缀**: ~~`Primary*`~~, ~~`ParseStatementList*`~~, ~~`NextToken*`~~
2. **不要暴露实现细节**: ~~`ScanString2PutDoubleBackslash`~~
3. **不要用方言名**: ~~`ScanHiveCommentDoubleSpace`~~
4. **不要用单字母缩写**: ~~`ScanAliasU`~~
5. **不要与类名重复**: ~~`SQLDateExpr`~~ (与 AST 类同名)
6. **不要用模糊词**: ~~`*Rest`~~, ~~`*Common*`~~, ~~`*Specific*`~~

---

## 第四部分：完整对照表（旧名 → 新名）

### LexerFeature（18 个）

| # | 旧名 | 新名 | 改动理由 |
|---|------|------|---------|
| 1 | `ScanSQLTypeBlockComment` | `SqlTypeDetectSkipBlockComment` | 明确为 SQL 类型探测场景 |
| 2 | `ScanSQLTypeWithSemi` | `SqlTypeDetectSkipLeadingSemicolons` | 明确行为——跳过前导分号 |
| 3 | `ScanSQLTypeWithFrom` | `SqlTypeDetectFromAsMultiInsert` | 明确语义——FROM 识别为多路插入 |
| 4 | `ScanSQLTypeWithFunction` | `SqlTypeDetectFunctionAsScript` | 明确语义——FUNCTION 识别为脚本 |
| 5 | `ScanSQLTypeWithBegin` | `SqlTypeDetectBeginAsScript` | 同上 |
| 6 | `ScanSQLTypeWithAt` | `SqlTypeDetectAtVariable` | 明确——@ 变量的处理 |
| 7 | `NextTokenColon` | `AllowColonAsToken` | 去掉方法名前缀 |
| 8 | `NextTokenPrefixN` | `AllowBackslashNAsNull` | 明确实际语义 |
| 9 | `ScanString2PutDoubleBackslash` | `StringEscapeWildcardChars` | 用语义替代实现细节 |
| 10 | `ScanAliasU` | `AliasUnicodeEscape` | 去掉单字母缩写 |
| 11 | `ScanNumberPrefixB` | `NumberBinaryLiteralPrefix` | 更具描述性 |
| 12 | `ScanNumberCommonProcess` | `NumberBinaryLiteralPostProcess` | 消除"Common"模糊词 |
| 13 | `ScanVariableAt` | `VariablePrefixAt` | 更简洁清晰 |
| 14 | `ScanVariableGreaterThan` | `VariableAtGreaterThanOp` | 明确操作符 |
| 15 | `ScanVariableSkipIdentifiers` | `VariableExtendIdentifierChars` | 明确行为 |
| 16 | `ScanVariableMoveToSemi` | `VariableReadUntilSemicolon` | 去掉"Move"实现术语 |
| 17 | `ScanHiveCommentDoubleSpace` | `CommentDoubleHyphenRequiresSpace` | 去掉方言名 |
| 18 | `ScanSubAsIdentifier` | `IdentifierAllowHyphen` | 用"Hyphen"替代"Sub" |

### ParserFeature（56 个）

| # | 旧名 | 新名 | 改动理由 |
|---|------|------|---------|
| 1 | `AcceptUnion` | `ParenthesizedQueryAllowUnion` | 明确场景 |
| 2 | `QueryRestSemi` | `QueryStopAtSemicolon` | 去掉"Rest" |
| 3 | `AsofJoin` | `AsOfJoin` | 修正驼峰 |
| 4 | `GlobalJoin` | `GlobalJoin` | ✓ 保持不变 |
| 5 | `JoinAt` | `JoinAtHintSyntax` | 明确语义 |
| 6 | `JoinRightTableWith` | `JoinRightSideAllowWith` | 更清晰 |
| 7 | `JoinRightTableFrom` | `JoinRightSideAllowFrom` | 更清晰 |
| 8 | `JoinRightTableAlias` | `JoinRightSideRequireAliasWithoutOn` | 明确条件 |
| 9 | `PostNaturalJoin` | `NaturalJoinAfterAliasFallback` | 明确行为 |
| 10 | `MultipleJoinOn` | `MultipleJoinOn` | ✓ 保持不变 |
| 11 | `UDJ` | `UserDefinedJoin` | 展开缩写 |
| 12 | `TwoConsecutiveUnion` | `SkipConsecutiveDuplicateUnion` | 明确行为 |
| 13 | `QueryTable` | `AllowTableAsQuery` | 明确语义 |
| 14 | `GroupByAll` | `GroupByAll` | ✓ 保持不变 |
| 15 | `RewriteGroupByCubeRollupToFunction` | `GroupByCubeRollupAsFunction` | 精简 |
| 16 | `GroupByPostDesc` | `GroupByIgnoreDesc` | 去掉"Post"，用"Ignore" |
| 17 | `GroupByItemOrder` | `GroupByItemIgnoreOrder` | 同上 |
| 18 | `SQLDateExpr` | `DateLiteralExpression` | 避免与类名冲突 |
| 19 | `SQLTimestampExpr` | `TimestampLiteralExpression` | 同上 |
| 20 | `PrimaryVariantColon` | `BindVariableColonPrefix` | 去掉方法名前缀 |
| 21 | `PrimaryBangBangSupport` | `DoubleBangOperator` | 去掉方法名前缀 |
| 22 | `PrimaryTwoConsecutiveSet` | `DoubleAtAllowSetKeyword` | 明确语义 |
| 23 | `PrimaryLbraceOdbcEscape` | `OdbcBraceEscapeSyntax` | 去掉方法名前缀 |
| 24 | `ParseAllIdentifier` | `AllowAllAsIdentifier` | 去掉"Parse"前缀 |
| 25 | `PrimaryRestCommaAfterLparen` | `FunctionCallAllowLeadingComma` | 明确语义 |
| 26 | `InRestSpecificOperation` | `InListSkipConsecutiveStringLiterals` | 消除模糊词 |
| 27 | `AdditiveRestPipesAsConcat` | `PipesAsConcatOperator` | 精简去掉"Rest" |
| 28 | `ParseAssignItemRparenCommaSetReturn` | `AssignItemStopAtRParenCommaSet` | 精简 |
| 29 | `ParseAssignItemEqSemiReturn` | `AssignItemStopAtSemiAfterEq` | 精简 |
| 30 | `ParseAssignItemSkip` | `AssignItemSkipUnexpectedTokens` | 明确行为 |
| 31 | `ParseAssignItemEqeq` | `AssignItemAllowDoubleEquals` | 明确语义 |
| 32 | `ParseSelectItemPrefixX` | `SelectItemHexStringPrefix` | 明确语义 |
| 33 | `ParseLimitBy` | `LimitByClause` | 精简 |
| 34 | `ParseStatementListWhen` | `StmtListEndTokenCheckIfBlock` | 明确行为 |
| 35 | `ParseStatementListSelectUnsupportedSyntax` | `StmtListRequireSemiBetweenSelects` | 明确语义 |
| 36 | `ParseStatementListUpdatePlanCache` | `UpdatePlanCacheSyntax` | 精简 |
| 37 | `ParseStatementListRollbackReturn` | `BlockStmtStopAtRollback` | 精简 |
| 38 | `ParseStatementListCommitReturn` | `BlockStmtStopAtCommit` | 精简 |
| 39 | `ParseStatementListLparenContinue` | `StmtListSkipParenthesizedNonStmt` | 明确行为 |
| 40 | `ParseRevokeFromUser` | `RevokeAllowFromUser` | 精简 |
| 41 | `ParseDropTableTables` | `DropAllowTablesKeyword` | 精简 |
| 42 | `ParseCreateSql` | `CreateSqlFunction` | 明确语义 |
| 43 | `CreateTableBodySupplemental` | `CreateTableSupplementalLogging` | 明确对象 |
| 44 | `TableAliasConnectWhere` | `TableAliasDisallowWhere` | 明确行为 |
| 45 | `TableAliasAsof` | `TableAliasDisallowAsOf` | 修正驼峰+明确行为 |
| 46 | `TableAliasLock` | `TableAliasAllowLockKeyword` | 统一命名模式 |
| 47 | `TableAliasPartition` | `TableAliasAllowPartitionKeyword` | 统一命名模式 |
| 48 | `TableAliasTable` | `TableAliasAllowTableKeyword` | 统一命名模式 |
| 49 | `TableAliasBetween` | `TableAliasAllowBetweenKeyword` | 统一命名模式 |
| 50 | `TableAliasRest` | `TableAliasFallbackKeywords` | 消除"Rest"模糊词 |
| 51 | `AsCommaFrom` | `AsClauseNullOnCommaOrFrom` | 明确行为 |
| 52 | `AsSkip` | `AsSkipTblProperties` | 明确对象 |
| 53 | `AsSequence` | `AsAllowSequenceKeyword` | 统一命名模式 |
| 54 | `AsDatabase` | `AsAllowDatabaseKeyword` | 统一命名模式 |
| 55 | `AsDefault` | `AsAllowDefaultKeyword` | 统一命名模式 |
| 56 | `AliasLiteralFloat` | `AliasAllowFloatLiteral` | 统一命名模式 |

### SQLParserFeature（需修改的 8 个）

| # | 旧名 | 新名 | 改动理由 |
|---|------|------|---------|
| 1 | `OptimizedForForParameterizedSkipValue` | `OptimizedForParameterizedSkipValue` | 修复 "ForFor" 拼写错误 |
| 2 | `EnableSQLBinaryOpExprGroup` | `EnableBinaryOpExprGroup` | 去掉多余"SQL" |
| 3 | `TDDLHint` | `TddlHintSupport` | 大小写规范化 |
| 4 | `DRDSAsyncDDL` | `DrdsAsyncDdlSupport` | 大小写规范化 |
| 5 | `DRDSBaseline` | `DrdsBaselineSupport` | 大小写规范化 |
| 6 | `PrintSQLWhileParsingFailed` | `PrintSqlOnParseFailure` | 消除歧义 |
| 7 | `Spark` | `SparkDialectMode` | 避免过于通用 |
| 8 | `Presto` | `PrestoDialectMode` | 同上 |

---

## 第五部分：重命名执行计划

**原则**: Feature 重命名影响面广，需要分批执行。

### 批次 1：修复明显错误（低风险）
- 修复 `OptimizedForForParameterizedSkipValue` 拼写错误
- 移动 `OscarStatementParser` 到正确的包

### 批次 2：消除最混淆的命名（中风险）
- `SQLDateExpr` → `DateLiteralExpression` (与 AST 类名冲突)
- `SQLTimestampExpr` → `TimestampLiteralExpression`
- `ScanString2PutDoubleBackslash` → `StringEscapeWildcardChars`
- `ScanNumberCommonProcess` → `NumberBinaryLiteralPostProcess`
- `UDJ` → `UserDefinedJoin`

### 批次 3：统一 Primary*/Parse*/NextToken* 前缀
- 所有 `Primary*` → 语义化命名
- 所有 `ParseStatementList*` → `StmtList*` 或 `BlockStmt*`
- 所有 `ParseAssignItem*` → `AssignItem*`
- 所有 `NextToken*` → `Allow*`

### 批次 4：统一 TableAlias*/As* 命名模式
- 统一为 `TableAliasAllow*Keyword` / `TableAliasDisallow*`
- 统一为 `AsAllow*Keyword`

### 批次 5：统一 Scan* 前缀
- `ScanSQLType*` → `SqlTypeDetect*`
- `ScanVariable*` → `Variable*`
- `ScanAlias*` → `Alias*`
- `ScanSub*` → `Identifier*`

**每批次执行方式**: 旧名添加 `@Deprecated` 注解，新名作为别名指向同一 mask，保留一个版本后移除旧名。
