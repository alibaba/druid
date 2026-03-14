# SQL 解析器指南 | SQL Parser Guide

[English](#english) | [中文](#中文)

---

## 中文

Druid SQL 解析器是一个高性能、支持多方言的 SQL 解析引擎，可将 SQL 文本解析为 AST（抽象语法树），支持 SQL 格式化、Schema 分析、SQL 改写等多种场景。

### 快速入门

#### 解析 SQL

```java
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.DbType;

String sql = "SELECT u.id, u.name, o.amount "
           + "FROM users u JOIN orders o ON u.id = o.user_id "
           + "WHERE o.amount > 100 ORDER BY o.amount DESC";

// 解析为 AST
List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
SQLStatement stmt = stmts.get(0);
```

#### 格式化 SQL

```java
String ugly = "select id,name from users where age>18 and status='active' order by name";

// 格式化输出
String formatted = SQLUtils.format(ugly, DbType.mysql);
// 输出:
// SELECT id, name
// FROM users
// WHERE age > 18
//   AND status = 'active'
// ORDER BY name

// 带配置的格式化
SQLUtils.FormatOption option = new SQLUtils.FormatOption();
option.setUppCase(true);     // 关键字大写
option.setPrettyFormat(true); // 美化格式
String result = SQLUtils.format(ugly, DbType.mysql, option);
```

#### Schema 分析

```java
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

String sql = "SELECT u.name, o.total FROM users u "
           + "JOIN orders o ON u.id = o.user_id "
           + "WHERE u.age > 18";

List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
stmts.get(0).accept(visitor);

// 获取表信息
System.out.println("Tables: " + visitor.getTables());
// 输出: {users=Select, orders=Select}

// 获取列信息
System.out.println("Columns: " + visitor.getColumns());
// 输出: [users.name, orders.total, users.id, orders.user_id, users.age]

// 获取 WHERE 条件
System.out.println("Conditions: " + visitor.getConditions());
// 输出: [users.id = orders.user_id, users.age > 18]
```

### 核心 API

#### SQLUtils — 入口工具类

| 方法 | 说明 |
|------|------|
| `parseStatements(sql, dbType)` | 解析 SQL 文本为语句列表 |
| `parseSingleStatement(sql, dbType)` | 解析单条 SQL 语句 |
| `format(sql, dbType)` | 格式化 SQL |
| `format(sql, dbType, option)` | 带选项格式化 SQL |
| `toSQLString(sqlObject, dbType)` | AST 节点输出为 SQL 文本 |
| `createSchemaStatVisitor(dbType)` | 创建 Schema 统计访问者 |

#### AST 核心节点

| 节点类型 | 基类 | 说明 |
|---------|------|------|
| 语句 | `SQLStatement` | 一条完整的 SQL 语句（SELECT、INSERT、CREATE TABLE 等） |
| 表达式 | `SQLExpr` | 表达式节点（列引用、常量、函数调用、运算等） |
| 表源 | `SQLTableSource` | FROM 子句中的表引用 |
| 数据类型 | `SQLDataType` | 列的数据类型定义 |

**常见 SQLStatement 子类：**

| 类 | 对应 SQL |
|----|---------|
| `SQLSelectStatement` | SELECT |
| `SQLInsertStatement` | INSERT |
| `SQLUpdateStatement` | UPDATE |
| `SQLDeleteStatement` | DELETE |
| `SQLCreateTableStatement` | CREATE TABLE |
| `SQLAlterTableStatement` | ALTER TABLE |
| `SQLDropTableStatement` | DROP TABLE |
| `SQLCreateIndexStatement` | CREATE INDEX |

**常见 SQLExpr 子类：**

| 类 | 说明 | 示例 |
|----|------|------|
| `SQLIdentifierExpr` | 标识符 | `name`、`users` |
| `SQLPropertyExpr` | 属性表达式 | `u.name`、`db.table` |
| `SQLIntegerExpr` | 整数常量 | `42` |
| `SQLCharExpr` | 字符串常量 | `'hello'` |
| `SQLBinaryOpExpr` | 二元运算 | `a > 10`、`x + y` |
| `SQLMethodInvokeExpr` | 函数调用 | `COUNT(*)`、`NOW()` |
| `SQLInListExpr` | IN 列表 | `id IN (1, 2, 3)` |
| `SQLBetweenExpr` | BETWEEN | `age BETWEEN 18 AND 30` |

### Visitor 模式

Druid 使用 Visitor 模式遍历 AST。核心访问者接口：

| Visitor | 功能 |
|---------|------|
| `SQLASTVisitor` | 基础访问者接口 |
| `SQLASTOutputVisitor` | SQL 输出（AST → SQL 文本） |
| `SchemaStatVisitor` | Schema 统计（提取表、列、条件信息） |
| Dialect-specific Visitors | 各方言特化的访问者 |

#### 自定义 Visitor 示例

```java
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;

// 统计所有二元运算表达式
public class BinaryOpCounter extends SQLASTVisitorAdapter {
    private int count = 0;

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        count++;
        return true;  // 继续遍历子节点
    }

    public int getCount() { return count; }
}

// 使用
BinaryOpCounter counter = new BinaryOpCounter();
stmt.accept(counter);
System.out.println("Binary ops: " + counter.getCount());
```

### SQL 改写

通过操作 AST 实现 SQL 改写：

```java
// 添加 WHERE 条件
String sql = "SELECT * FROM users";
SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.mysql);
SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();

// 添加 tenant_id 条件
SQLBinaryOpExpr condition = new SQLBinaryOpExpr(
    new SQLIdentifierExpr("tenant_id"),
    SQLBinaryOperator.Equality,
    new SQLIntegerExpr(123)
);
queryBlock.addWhere(condition);

System.out.println(SQLUtils.toSQLString(stmt, DbType.mysql));
// 输出: SELECT * FROM users WHERE tenant_id = 123
```

### 方言特定解析

不同方言有特定的语法扩展。使用正确的 `DbType` 以获得最佳解析结果：

```java
// MySQL 特有语法
SQLUtils.parseStatements("SELECT * FROM t LIMIT 10, 20", DbType.mysql);

// PostgreSQL 特有语法
SQLUtils.parseStatements("SELECT * FROM t LIMIT 20 OFFSET 10", DbType.postgresql);

// Oracle 特有语法
SQLUtils.parseStatements("SELECT * FROM t WHERE ROWNUM <= 20", DbType.oracle);
```

### 性能建议

1. **重用 Parser** — 在高频场景中避免重复创建 Parser 对象
2. **选择正确的 DbType** — 使用精确的方言类型，避免使用通用解析模式
3. **按需遍历** — 使用 `return false` 跳过不需要遍历的子树

---

## English

The Druid SQL Parser is a high-performance, multi-dialect SQL parsing engine that converts SQL text into AST (Abstract Syntax Tree), supporting SQL formatting, schema analysis, SQL rewriting and more.

### Quick Start

```java
// Parse SQL
List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);

// Format SQL
String formatted = SQLUtils.format(sql, DbType.mysql);

// Schema analysis
SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
stmts.get(0).accept(visitor);
System.out.println("Tables: " + visitor.getTables());
System.out.println("Columns: " + visitor.getColumns());
```

### Core API

- `SQLUtils.parseStatements(sql, dbType)` — Parse SQL text into statement list
- `SQLUtils.format(sql, dbType)` — Format SQL
- `SQLUtils.toSQLString(ast, dbType)` — Convert AST back to SQL text
- `SQLUtils.createSchemaStatVisitor(dbType)` — Create schema analysis visitor

### AST Node Hierarchy

- `SQLStatement` — Complete SQL statements (SELECT, INSERT, CREATE TABLE, etc.)
- `SQLExpr` — Expressions (column refs, constants, function calls, operations)
- `SQLTableSource` — Table references in FROM clauses
- `SQLDataType` — Column data type definitions

### Visitor Pattern

Use the Visitor pattern to traverse and transform the AST:

- `SQLASTVisitor` — Base visitor interface
- `SQLASTOutputVisitor` — SQL output generation
- `SchemaStatVisitor` — Schema statistics extraction
- Custom visitors — Extend `SQLASTVisitorAdapter` for custom logic

### SQL Rewriting

Manipulate the AST to rewrite SQL:

```java
SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.mysql);
SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
queryBlock.addWhere(new SQLBinaryOpExpr(
    new SQLIdentifierExpr("tenant_id"),
    SQLBinaryOperator.Equality,
    new SQLIntegerExpr(123)
));
String rewritten = SQLUtils.toSQLString(stmt, DbType.mysql);
```
