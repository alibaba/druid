## Why

ODPS 方言在 AST 转 SQL 字符串（toString）以及 clone 时存在多处行为错误：`not` 表达式括号丢失或错乱、多余括号被吞掉、子查询表达式未加括号导致生成错误 SQL、clone 时 QUALIFY 子句丢失。这些问题会导致解析-序列化往返或 clone 后的 SQL 与预期不一致，影响依赖 Druid 做 ODPS SQL 处理的场景。按 `docs/odps_fix.md` 的用例修复可提升 ODPS 方言的可靠性和一致性。

## What Changes

- **Bug fix (core)**：修复 ODPS 输出访客（OdpsOutputVisitor / 相关 toString 逻辑）中：
  - `not` 表达式括号丢失：如 `not (a) and (not b) and (c)` 被错误输出为 `not a and not b and (c)`。
  - `not` 表达式括号错乱：如 `(not a) != b` 被错误输出为 `(NOT a != b)`。
  - 多括号不打印：如 `((a = 1))` 被错误输出为 `(a = 1)`。
  - 以子查询为操作数的表达式在 toString 时未自动加括号，导致生成错误 SQL。
- **Bug fix (core)**：修复 ODPS Select 语句 clone 时 QUALIFY 子句丢失（clone 后应保留 `QUALIFY ROW_NUMBER() OVER (...)= 1` 等）。
- 补充或沿用与上述用例对应的单测（参考 `docs/odps_fix.md` 中的测试思路）。

无 **BREAKING** 变更；修正的是当前错误行为，使输出与语义一致。

## Capabilities

### New Capabilities

- 无新增能力规格；本变更为对现有 ODPS 方言行为的修正。

### Modified Capabilities

- **sql-parser-core**：在 ODPS 方言下，AST 序列化（output visitor）与 Select 克隆行为需满足：
  - 序列化时保留 `not` 的括号、多括号以及子查询表达式所需的外层括号；
  - clone 后的 Select 保留 QUALIFY 子句。本变更通过 delta spec 说明上述可验证行为。

## Impact

- **受影响模块**：`core`（`com.alibaba.druid.sql.dialect.odps` 及与 ODPS Select/AST 输出、clone 相关的 AST 节点）。
- **受影响 API**：无新增或移除公开 API；`SQLUtils.toOdpsString(...)` 及 `SQLSelectStatement.clone()` 在 ODPS 场景下的输出/克隆结果将更正确。
- **兼容性**：修正错误行为，不改变合法 SQL 的解析结果；仅修正此前错误序列化或错误 clone 的用例，视为向后兼容的 bug fix。
