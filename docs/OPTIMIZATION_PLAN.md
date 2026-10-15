# Alibaba Druid SQL 解析器 分步优化计划

> 本文档基于对 Druid 1.2.28-SNAPSHOT 代码库的深入分析，提出可分步执行的优化方案。
> 每个阶段聚焦一个明确的问题域，避免一次性大规模改动。

---

## 问题总览

通过架构分析，识别出以下核心问题：

| # | 问题 | 严重程度 | 影响范围 |
|---|------|---------|---------|
| P1 | 巨型文件（God Class）| 高 | 可维护性、可测试性 |
| P2 | 基类与方言的循环依赖 | 高 | 架构清晰度、可扩展性 |
| P3 | 工厂方法中的大型 switch-case | 中 | 新增方言的复杂度 |
| P4 | SQLASTVisitor 接口膨胀 | 中 | API 稳定性、编译速度 |
| P5 | Lexer 中大量重复模式代码 | 中 | 代码冗余 |
| P6 | Parser 方法过长 | 中 | 可读性、可测试性 |
| P7 | 硬编码的方言特殊处理 | 中 | 可维护性 |
| P8 | SQLUtils 工具类过于庞大 | 低 | 可维护性 |
| P9 | 缺少方言注册机制 | 低 | 可扩展性 |

---

## 阶段 1：引入方言注册机制（消除工厂方法中的大型 switch-case）

**目标**: 将 `SQLParserUtils` 中 3 个重复的巨型 switch-case（`createSQLStatementParser`、`createExprParser`、`createLexer`）替换为注册表模式。

**影响文件**:
- `sql/parser/SQLParserUtils.java` (~1,162 行)
- 新增 `sql/parser/DialectRegistry.java`

**具体步骤**:

### 1.1 定义方言提供者接口

```java
// 新增文件: sql/parser/SQLDialectProvider.java
public interface SQLDialectProvider {
    DbType[] supportedDbTypes();
    Lexer createLexer(String sql, SQLParserFeature... features);
    SQLExprParser createExprParser(String sql, SQLParserFeature... features);
    SQLStatementParser createStatementParser(String sql, SQLParserFeature... features);
    SQLASTOutputVisitor createOutputVisitor(StringBuilder out);
    // 可选
    default SchemaStatVisitor createSchemaStatVisitor() { return new SchemaStatVisitor(); }
    default SQLSelectQueryBlock createSelectQueryBlock() { return new SQLSelectQueryBlock(); }
}
```

### 1.2 创建注册表

```java
// 新增文件: sql/parser/DialectRegistry.java
public class DialectRegistry {
    private static final Map<DbType, SQLDialectProvider> providers = new ConcurrentHashMap<>();
    
    public static void register(SQLDialectProvider provider) {
        for (DbType dbType : provider.supportedDbTypes()) {
            providers.put(dbType, provider);
        }
    }
    
    public static SQLDialectProvider get(DbType dbType) {
        return providers.get(dbType);
    }
}
```

### 1.3 为每个方言实现 Provider

以 MySQL 为例：
```java
// 新增文件: sql/dialect/mysql/MySqlDialectProvider.java
public class MySqlDialectProvider implements SQLDialectProvider {
    @Override
    public DbType[] supportedDbTypes() {
        return new DbType[]{DbType.mysql, DbType.tidb, DbType.mariadb, DbType.goldendb, 
                            DbType.oceanbase, DbType.drds, DbType.polardbx};
    }
    // ... 实现各方法
}
```

### 1.4 使用 ServiceLoader 或静态初始化注册

```java
// 在 DialectRegistry 静态块中注册所有内置方言
static {
    register(new MySqlDialectProvider());
    register(new OracleDialectProvider());
    // ...
}
```

### 1.5 重构 SQLParserUtils

将 switch-case 替换为注册表查找：
```java
public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType, SQLParserFeature... features) {
    SQLDialectProvider provider = DialectRegistry.get(dbType);
    if (provider != null) {
        return provider.createStatementParser(sql, features);
    }
    return new SQLStatementParser(sql, dbType, features);
}
```

**预期收益**:
- `SQLParserUtils` 从 1162 行减少到 ~300 行
- 新增方言只需实现 Provider 并注册，无需修改核心代码
- 消除 3 处重复的 switch-case

**风险**: 低。纯重构，不改变任何行为。

**验证**: 全量单元测试通过。

---

## 阶段 2：拆分 SQLStatementParser 巨型方法

**目标**: 将 `SQLStatementParser.parseStatementList()` 及相关巨型方法按语句类型拆分为独立的解析方法类。

**影响文件**:
- `sql/parser/SQLStatementParser.java` (~7,953 行)

**具体步骤**:

### 2.1 提取 DDL 解析逻辑

将 `parseCreate()`、`parseAlter()`、`parseDrop()` 等 DDL 语句解析逻辑提取到已有的 `SQLDDLParser`：

```java
// 增强现有 SQLDDLParser
public class SQLDDLParser {
    // 从 SQLStatementParser 迁移：
    // parseCreate() → 约 800 行
    // parseAlter() → 约 600 行  
    // parseDrop() → 约 300 行
}
```

### 2.2 提取 DML 解析逻辑

```java
// 新增文件或增强现有类
// 从 SQLStatementParser 迁移：
// parseInsert() → 约 400 行
// parseUpdateStatement() → 约 300 行
// parseDeleteStatement() → 约 200 行
```

### 2.3 提取 DCL/TCL 解析逻辑

```java
// parseGrant() / parseRevoke() → 约 200 行
// parseCommit() / parseRollback() → 约 100 行
// parseSet() → 约 300 行
```

### 2.4 简化 parseStatementList

重构后 `parseStatementList()` 只保留调度逻辑（switch-case 分发到子方法），不包含具体解析实现。

**预期收益**:
- `SQLStatementParser` 从 7953 行减少到 ~3000 行
- 每个语句类型的解析逻辑可独立测试
- 方言子类覆写更加精准

**风险**: 中。需要保证方言子类（如 `MySqlStatementParser`）的 override 行为不变。

**验证**: 全量单元测试 + 方言特定测试。

---

## 阶段 3：消除基类对方言类型的直接依赖

**目标**: 消除 `SQLStatementParser`、`SQLExprParser`、`SQLASTOutputVisitor` 等基类中对方言 AST 类的直接 import。

**当前问题**:
- `SQLStatementParser` import 了 `MySqlStatement*`、`HiveInsert*` 等方言类
- `SQLExprParser` import 了 `MySqlCharExpr`、`OracleArgumentExpr`
- `SQLASTOutputVisitor` import 了 MySQL/Oracle/Hive/ODPS 的多个方言类型

这违反了分层架构原则（基类不应依赖子类所属的方言包）。

**影响文件**:
- `sql/parser/SQLStatementParser.java`
- `sql/parser/SQLExprParser.java`
- `sql/visitor/SQLASTOutputVisitor.java`

**具体步骤**:

### 3.1 分析并归类方言依赖

当前基类中的方言引用：

| 基类 | 依赖的方言类 | 引用次数 |
|------|------------|---------|
| SQLStatementParser | MySql*Statement (7类), Hive*Statement (4类), OracleExprParser | 多处 |
| SQLExprParser | MySqlCharExpr, OracleArgumentExpr | 各1处 |
| SQLASTOutputVisitor | MySql*(4类), Oracle*(4类), Hive*(1类), Odps*(1类) | 43处DbType引用 |

### 3.2 将通用 AST 节点提升到公共包

对于真正跨方言使用的 AST 节点（如 `HiveInsert` 被多个方言共用），提升到 `sql/ast/statement/` 公共包。

例如：
- `HiveInsert` → `SQLMultiInsert`（通用多路插入）
- `HiveInsertStatement` → `SQLMultiInsertStatement`
- `MySqlCharExpr` 中的通用部分 → `SQLCharExpr` 增强

### 3.3 使用方言特性检查替代 instanceof

将 `SQLASTOutputVisitor` 中的 `DbType == DbType.mysql` 检查迁移到方言子类的 override：

```java
// Before (基类中):
if (dbType == DbType.mysql) {
    // MySQL 特殊处理
}

// After (MySqlOutputVisitor 中 override):
@Override
public boolean visit(SQLColumnDefinition x) {
    // MySQL 特殊处理
    super.visit(x);
}
```

### 3.4 引入 AST 节点方言标记

对于无法完全分离的场景，使用 AST 节点上的 `dbType` 字段做运行时判断，而非基类 import 方言类。

**预期收益**:
- 基类不再依赖方言包，依赖方向单向（方言 → 基类）
- 新增方言无需修改基类代码
- 架构层次清晰

**风险**: 中高。涉及 AST 类重命名和迁移，需要仔细处理向后兼容。

**验证**: 全量测试 + 编译依赖分析确认无反向依赖。

---/

## 阶段 4：拆分 SQLASTOutputVisitor（12,623 行）

**目标**: 将最大的单文件 `SQLASTOutputVisitor` 按功能域拆分为多个组合类。

**影响文件**:
- `sql/visitor/SQLASTOutputVisitor.java` (~12,623 行)

**具体步骤**:

### 4.1 按 AST 类型分组

```
SQLASTOutputVisitor (保留核心框架 + 基础输出方法, ~2000 行)
├── SQLExprOutputHelper        # 表达式输出 (SQLBinaryOpExpr, SQLMethodInvoke 等)
├── SQLStatementOutputHelper   # 语句输出 (SELECT, INSERT, UPDATE, DELETE)
├── SQLDDLOutputHelper         # DDL 输出 (CREATE TABLE, ALTER TABLE)
├── SQLDataTypeOutputHelper    # 数据类型输出
└── SQLMiscOutputHelper        # 其他 (Hint, Comment, Partition 等)
```

### 4.2 使用委托模式

```java
public class SQLASTOutputVisitor {
    private final SQLExprOutputHelper exprHelper;
    private final SQLStatementOutputHelper stmtHelper;
    // ...
    
    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        return exprHelper.visitBinaryOpExpr(this, x);
    }
}
```

### 4.3 逐步迁移

每次迁移一类 visit 方法（如先迁移所有表达式相关），确保测试通过后再迁移下一类。

**预期收益**:
- 主文件从 12623 行减少到 ~2000 行
- 各 Helper 类可独立测试
- 方言 OutputVisitor 可以更精准地 override

**风险**: 中。需要保证方言子类的 override 行为不变。

**验证**: 全量格式化输出对比测试。

---

## 阶段 5：拆分 SQLExprParser.primary() 超长方法

**目标**: 将 `SQLExprParser.primary()` 方法（估计 1500+ 行的单方法）按 Token 类型拆分为多个子方法。

**影响文件**:
- `sql/parser/SQLExprParser.java` (~6,445 行)

**具体步骤**:

### 5.1 分析 primary() 的分支结构

`primary()` 方法处理所有初始表达式解析，按 Token 类型 switch：

```java
// 当前结构（伪代码）
public SQLExpr primary() {
    switch (lexer.token) {
        case IDENTIFIER:      // ~300 行（含函数名匹配）
        case LITERAL_INT:     // ~30 行
        case LITERAL_FLOAT:   // ~30 行
        case LITERAL_CHARS:   // ~50 行
        case LPAREN:          // ~200 行（子查询、元组、优先级）
        case CASE:            // ~80 行
        case CAST:            // ~50 行
        case NOT:             // ~40 行
        case NULL:            // ~10 行
        // ... 更多 Token
    }
}
```

### 5.2 提取为子方法

```java
public SQLExpr primary() {
    switch (lexer.token) {
        case IDENTIFIER: return parsePrimaryIdentifier();
        case LITERAL_INT: return parsePrimaryLiteralInt();
        case LPAREN: return parsePrimaryParenthesized();
        case CASE: return parseCaseExpr();
        case CAST: return parseCastExpr();
        // ...
    }
}
```

### 5.3 同步处理方言子类

确保 `MySqlExprParser`、`OracleExprParser` 等子类中 override 的 `primary()` 方法也同步调整。

**预期收益**:
- `primary()` 从 1500+ 行减少到 ~100 行（仅分发）
- 每个子方法可独立测试和理解
- 方言子类可以更精准地覆写特定分支

**风险**: 低。纯方法提取重构。

**验证**: 全量表达式解析测试。

---

## 阶段 6：统一 DialectFeature 使用，减少 SQLParser.tableAlias() 复杂度

**目标**: 简化 `SQLParser.tableAlias()` 和 `SQLParser.as()` 方法中的复杂分支逻辑。

**当前问题**:
- `tableAlias()` 方法约 360 行，包含大量 Token 和 hash 值的硬编码判断
- `as()` 方法约 190 行，同样包含大量特殊处理
- 很多分支已经使用 `dialectFeatureEnabled()` 但还有遗留的硬编码

**影响文件**:
- `sql/parser/SQLParser.java` (~940 行)
- `sql/parser/DialectFeature.java`

**具体步骤**:

### 6.1 扩展 DialectFeature.ParserFeature

将 `tableAlias()` 中的硬编码逻辑提取为新的 ParserFeature：

```java
// 新增特性
AliasKeywordAsIdentifier,   // 允许关键字作为别名
AliasQualifySupport,        // QUALIFY 关键字别名处理
// ...
```

### 6.2 将 Token 判断逻辑数据化

```java
// Before: 硬编码的 Token 列表
case LEFT: case RIGHT: case INNER: case FULL: ...

// After: 使用 EnumSet 或 BitSet 配置
private static final EnumSet<Token> ALIAS_TOKENS = EnumSet.of(LEFT, RIGHT, INNER, FULL, ...);
```

### 6.3 将 hash 值判断迁移到方言层

将 `FnvHash.Constants.START`, `CONNECT`, `NATURAL` 等的特殊处理迁移到对应方言的 Parser 子类中。

**预期收益**:
- `tableAlias()` 从 360 行减少到 ~100 行
- 方言差异通过 Feature 位掩码清晰表达

**风险**: 低。渐进式重构。

**验证**: 别名解析相关测试用例。

---

## 阶段 7：优化 SQLASTVisitor 接口

**目标**: 减少 `SQLASTVisitor` 接口的方法数量，提升 API 的可扩展性。

**当前问题**:
- `SQLASTVisitor` 接口有 ~2,882 行，约 600+ 个 default 方法
- 每新增一个 AST 节点都需要修改此接口
- 方言特有节点的 visit 方法混在通用接口中

**影响文件**:
- `sql/visitor/SQLASTVisitor.java` (~2,882 行)
- 所有方言的 Visitor 接口

**具体步骤**:

### 7.1 分离方言 Visitor 方法

将方言特有 AST 节点的 visit 方法从 `SQLASTVisitor` 移到各方言的 Visitor 接口：

```java
// Before: SQLASTVisitor 中包含
default boolean visit(MySqlKillStatement x) { return true; }

// After: 仅在 MySqlASTVisitor 中定义
// SQLASTVisitor 不包含任何方言特有方法
```

### 7.2 引入泛型 visit 兜底方法

```java
// 在 SQLASTVisitor 中添加
default boolean visitUnknown(SQLObject x) { return true; }
default void endVisitUnknown(SQLObject x) {}
```

当 AST 节点没有对应的 visit 方法时，回退到泛型方法。

### 7.3 使用接口继承分组

```java
SQLASTVisitor (核心方法: ~100 个)
├── SQLExprVisitor (表达式 visit: ~70 个)
├── SQLStatementVisitor (语句 visit: ~200 个)
└── SQLDDLVisitor (DDL visit: ~100 个)
```

`SQLASTVisitor` 继承以上所有子接口（保持向后兼容）。

**预期收益**:
- 核心接口更精简
- 新增 AST 节点不一定需要修改核心接口
- 方言边界更清晰

**风险**: 中。需要处理向后兼容。

**验证**: 全量编译 + 所有 Visitor 实现类编译通过。

---

## 阶段 8：Lexer 优化 - 消除重复扫描模式

**目标**: 减少各方言 Lexer 中的重复代码。

**当前问题**:
- 20+ 个方言 Lexer 中有大量类似的 `scanString()`、`scanNumber()` 等方法实现
- Keywords 初始化代码高度相似

**影响文件**:
- `sql/parser/Lexer.java`
- 所有方言 Lexer 文件

**具体步骤**:

### 8.1 将 Keywords 构建提取为 Builder

```java
public class KeywordsBuilder {
    private final Map<String, Token> keywords;
    
    public KeywordsBuilder() {
        this.keywords = new HashMap<>(Keywords.DEFAULT_KEYWORDS.getKeywords());
    }
    
    public KeywordsBuilder add(String keyword, Token token) {
        keywords.put(keyword, token);
        return this;
    }
    
    public Keywords build() {
        return new Keywords(keywords);
    }
}
```

### 8.2 将 DialectFeature 作为 Lexer 唯一的方言差异配置

确保所有方言差异都通过 `DialectFeature` 的位掩码控制，而非方法 override：

```java
// 理想状态: 大多数方言 Lexer 只需配置
public class XxxLexer extends Lexer {
    public static final DialectFeature XXX_FEATURE = new DialectFeature(...);
    public static final Keywords XXX_KEYWORDS = new KeywordsBuilder()
        .add("xxx_keyword", Token.IDENTIFIER)
        .build();
    
    public XxxLexer(String sql, SQLParserFeature... features) {
        super(sql);
        this.dialectFeature = XXX_FEATURE;
        this.keywords = XXX_KEYWORDS;
        // ...
    }
}
```

### 8.3 将 scanString 差异抽象为 Feature

```java
enum LexerFeature {
    // 已有
    ScanString2PutDoubleBackslash,  // MySQL 风格转义
    // 新增
    ScanStringDollarQuoted,          // PostgreSQL $$ 字符串
    ScanStringQQuoted,               // Oracle Q-Quote
    ScanStringBracketIdentifier,     // SQL Server [] 标识符
}
```

**预期收益**:
- 方言 Lexer 代码量减少 50%+
- 新增方言 Lexer 只需配置 Feature + Keywords

**风险**: 低。渐进式提取。

**验证**: 各方言词法扫描测试。

---

## 阶段 9：增强错误报告和诊断

**目标**: 改进 SQL 解析错误的报告质量。

**当前问题**:
- 错误消息仅包含位置和期望的 Token，缺少上下文
- 没有错误恢复机制（遇错即停）
- `ParserException` 不包含结构化的源位置信息

**影响文件**:
- `sql/parser/ParserException.java`
- `sql/parser/SQLParser.java`
- `sql/parser/Lexer.java`

**具体步骤**:

### 9.1 增强 ParserException

```java
public class ParserException extends RuntimeException {
    private int line;
    private int column;
    private String sqlFragment;   // 错误周围的 SQL 片段
    private Token expected;       // 期望的 Token
    private Token actual;         // 实际的 Token
    
    public String getFormattedMessage() {
        // 格式化输出带行号和下划线标记的错误信息
    }
}
```

### 9.2 改进 Lexer 位置追踪

确保每个 Token 都准确记录行号和列号，而不仅仅是字符偏移量。

### 9.3 添加错误恢复点

在关键解析位置添加恢复点，允许跳过错误语句继续解析后续语句。

**预期收益**:
- 用户收到更友好的错误信息
- 批量解析时不因单条 SQL 错误而中断

**风险**: 低。增量改进。

**验证**: 错误 SQL 测试用例。

---

## 阶段 10：测试基础设施改进

**目标**: 建立自动化的回归测试框架，为后续重构保驾护航。

**具体步骤**:

### 10.1 建立 SQL 解析黄金文件测试

```
test/resources/sql/
├── mysql/
│   ├── select_001.sql          # 输入 SQL
│   ├── select_001.ast.json     # 期望的 AST 结构
│   └── select_001.output.sql   # 期望的格式化输出
├── oracle/
├── postgresql/
└── ...
```

### 10.2 添加方言兼容性矩阵测试

确保同一条 SQL 在声明支持的所有方言中都能正确解析。

### 10.3 添加性能基准测试

使用 JMH 建立核心场景的性能基线：
- 简单 SELECT 解析
- 复杂 JOIN 查询解析
- 大批量 INSERT 解析
- AST → SQL 输出

**预期收益**:
- 重构安全网
- 性能回归自动检测

**风险**: 无。纯增量。

---

## 执行顺序建议

```
阶段 10 (测试基础) → 先建立安全网
    ↓
阶段 1 (方言注册) → 最小风险的架构改进
    ↓
阶段 5 (primary()拆分) → 最小风险的代码改进
    ↓
阶段 6 (tableAlias优化) → 低风险改进
    ↓
阶段 2 (StatementParser拆分) → 中等风险改进
    ↓
阶段 8 (Lexer优化) → 低风险改进
    ↓
阶段 4 (OutputVisitor拆分) → 中等风险改进
    ↓
阶段 3 (消除反向依赖) → 较高风险改进
    ↓
阶段 7 (Visitor接口优化) → 需要处理兼容性
    ↓
阶段 9 (错误报告) → 增强功能
```

**核心原则**:
1. 先建测试，后做重构
2. 每阶段提交独立的 PR，包含完整测试
3. 低风险优先，高风险后置
4. 每阶段完成后进行性能回归验证

---

## 各阶段工时估算

| 阶段 | 预计工时 | 风险等级 | 依赖 |
|------|---------|---------|------|
| 阶段 10: 测试基础 | 3-5 天 | 无 | 无 |
| 阶段 1: 方言注册 | 2-3 天 | 低 | 无 |
| 阶段 5: primary() 拆分 | 1-2 天 | 低 | 阶段 10 |
| 阶段 6: tableAlias 优化 | 1-2 天 | 低 | 阶段 10 |
| 阶段 2: StatementParser 拆分 | 3-5 天 | 中 | 阶段 10 |
| 阶段 8: Lexer 优化 | 2-3 天 | 低 | 阶段 1 |
| 阶段 4: OutputVisitor 拆分 | 3-5 天 | 中 | 阶段 10 |
| 阶段 3: 消除反向依赖 | 5-8 天 | 中高 | 阶段 1, 2, 4 |
| 阶段 7: Visitor 接口优化 | 3-5 天 | 中 | 阶段 3, 4 |
| 阶段 9: 错误报告 | 2-3 天 | 低 | 无 |
| **总计** | **25-41 天** | | |
