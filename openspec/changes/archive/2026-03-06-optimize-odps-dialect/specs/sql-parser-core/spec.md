# Delta: sql-parser-core — ODPS 方言输出与 clone 正确性

## ADDED Requirements

### Requirement: ODPS 方言 AST 序列化括号正确性

在 ODPS 方言下，AST 转 SQL 字符串（output visitor）时，SHALL 正确输出括号，使得序列化结果与解析前的 SQL 语义一致。

#### Scenario: 修复用例必须优先以方言 resourceTest 覆盖
- **WHEN** 修复的是方言级别的序列化/输出行为（如括号、关键字位置、子句缺失等）
- **THEN** 回归测试 SHALL 使用该方言的 resourceTest 作为首选（例如 `com.alibaba.druid.bvt.sql.odps.OdpsResourceTest` + `core/src/test/resources/bvt/parser/odps/*.txt`）来定义输入/期望输出对
- **AND** 每个修复用例 SHALL 在对应方言的 resource 文件中新增一段输入/期望输出对，以 pretty format 作为黄金输出
- **AND** 只有在 resourceTest 无法表达的行为（例如 clone 语义或纯 API 行为）上，才 MAY 通过额外的单测补充

#### Scenario: not 表达式括号不丢失
- **WHEN** 表达式为 `not (a) and (not b) and (c)` 经解析后再序列化为 ODPS SQL
- **THEN** 输出 SHALL 为 `not (a) and (not b) and (c)`（或等价格式）
- **AND** 不得输出为 `not a and not b and (c)`

#### Scenario: not 表达式括号不错乱
- **WHEN** 表达式为 `(not a) != b` 经解析后再序列化为 ODPS SQL
- **THEN** 输出 SHALL 为 `(not a) != b`（或等价格式）
- **AND** 不得将括号错误地扩展到整个比较式（如 `(NOT a != b)`）

#### Scenario: 多括号保留
- **WHEN** 表达式为 `((a = 1))` 经解析后再序列化为 ODPS SQL
- **THEN** 输出 SHALL 保留双层括号为 `((a = 1))`
- **AND** 不得输出为单层括号 `(a = 1)`

#### Scenario: 子查询表达式作为操作数时自动加括号
- **WHEN** 以子查询（SQLQueryExpr）作为二元表达式（如等值比较）的操作数并序列化为 ODPS SQL
- **THEN** 子查询 SHALL 被输出为带括号的形式（如 `(SELECT ...)`）
- **AND** 生成的 SQL 在语法与语义上 SHALL 正确

### Requirement: ODPS Select clone 与输出保留 QUALIFY

ODPS Select 语句（OdpsSelectQueryBlock）在 clone 与序列化时 SHALL 保留 QUALIFY 子句。

#### Scenario: clone 后保留 QUALIFY
- **WHEN** 对包含 `QUALIFY ROW_NUMBER() OVER (PARTITION BY c ORDER BY d DESC) = 1` 的 ODPS Select 语句执行 clone
- **THEN** clone 得到的语句 SHALL 仍包含相同的 QUALIFY 子句
- **AND** 对该 clone 结果再次序列化 SHALL 输出包含 QUALIFY 的完整 SQL

#### Scenario: 序列化时输出 QUALIFY
- **WHEN** 对包含 qualify 表达式的 ODPS Select 语句调用 toOdpsString（或等价 output visitor）
- **THEN** 输出 SHALL 包含 `QUALIFY` 关键字及其表达式
- **AND** 顺序 SHALL 与标准 ODPS 语义一致（如 WHERE 之后、ORDER BY 之前）
