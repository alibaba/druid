# Design: ODPS 方言优化（toString 与 clone）

## Context

- **现状**：ODPS 方言在 `core` 模块中由 `OdpsOutputVisitor`（及 ODPS 相关 AST）负责序列化，由 `SQLSelectQueryBlock` / `OdpsSelectQueryBlock` 负责 clone。当前存在：（1）toString 时 `not` 括号丢失或错乱、多括号被吞、子查询表达式未加括号；（2）clone 后 QUALIFY 子句丢失（根因是 `OdpsSelectQueryBlock.accept0(OdpsASTVisitor)` 未把 `qualify` 交给 visitor，导致输出与遍历均未处理 qualify）。
- **约束**：仅改 core，不新增对外 API；遵循现有 Checkstyle 与测试惯例；保持与现有 ODPS 解析/序列化行为兼容，仅修正错误用例。
- **相关方**：依赖 Druid 做 ODPS SQL 解析、序列化或 clone 的下游。

## Goals / Non-Goals

**Goals:**

- 修复 ODPS 下 AST 转 SQL 时 `not` 的括号、多括号、子查询表达式括号，使 `SQLUtils.toOdpsString(...)` 与原始 SQL 语义一致。
- 修复 ODPS Select 在 clone 后保留 QUALIFY 子句（含输出时正确打印 QUALIFY）。
- 通过 `docs/odps_fix.md` 中的用例（或等价单测）可验证上述行为。

**Non-Goals:**

- 不改动其他方言（如 Hive、MySQL）的 output 或 clone 行为。
- 不改变 ODPS 解析规则或词法；不新增可选语法。

## Decisions

1. **toString 括号与 not**
   - **选型**：在 `OdpsOutputVisitor`（及必要时其调用的公共逻辑）中，根据表达式上下文与 `parenthesized` 等标记，正确输出 `not` 的括号、多括号以及子查询表达式外层括号。
   - **理由**：问题集中在 ODPS 输出访客的表达式打印规则；与现有 `SQLNotExpr`、括号标记逻辑一致，便于单测覆盖。
   - **备选**：在更底层统一“表达式是否需要括号”的逻辑——涉及面大，本变更仅做 ODPS 侧修正。

2. **QUALIFY 输出与 clone**
   - **选型**：在 `OdpsSelectQueryBlock.accept0(OdpsASTVisitor)` 中显式 `acceptChild(visitor, this.qualify)`，使 OdpsOutputVisitor 能访问并打印 QUALIFY；clone 继续依赖基类 `cloneTo` 对 `qualify` 的复制，若实测仍丢失则在本类 `cloneTo` 中显式复制 `qualify`。
   - **理由**：当前 ODPS 的 accept0 未把 qualify 纳入遍历，导致输出与遍历都看不到 QUALIFY；基类已具备 qualify 的 clone 逻辑，优先保证 accept 路径一致即可修复输出，必要时再补 clone 路径。

3. **子查询表达式括号**
   - **选型**：在 ODPS 输出访客中，当子查询作为二元（或其它）表达式的操作数时，为其增加外层括号的打印逻辑（与现有 `SQLQueryExpr` 的 parenthesized 或 visitor 上下文一致）。
   - **理由**：与 `docs/odps_fix.md` 中“query 表达式 toString 时不会自动添加括号”的修复目标一致，且不改变解析结果。

4. **测试**
   - **选型**：沿用或补充 `docs/odps_fix.md` 中的用例（如 `TestToStringNotErrorBracket`、`TestToStringNotMissBracket`、`TestToStringMultiBracket`、`TestToStringSubQueryExpr`、`TestCloneQualify`），放在 `core/src/test` 下现有 ODPS 测试结构中。
   - **理由**：可直接对应规格与需求，便于回归。

## Risks / Trade-offs

- **[Risk]** 修改 `not` 与括号规则可能影响少数依赖“当前错误输出”的脚本。
  - **Mitigation**：视为 bug fix，不提供兼容旧错误输出的开关；若有实际依赖可再讨论兼容策略。

- **[Risk]** 在 ODPS accept0 中增加 qualify 的 acceptChild 可能影响其他依赖该遍历顺序的 visitor。
  - **Mitigation**：qualify 在语义上属于 Select 的一部分，与基类 `SQLSelectQueryBlock` 的 accept 顺序一致，仅补全遗漏；若有回归再按用例调整顺序。

## Migration Plan

- 无数据或部署迁移；仅发布新版本 core。
- 若下游依赖错误的 toString/clone 结果，需按正确语义调整断言或脚本；建议在发布说明中注明为 ODPS 方言 bug 修复。

## Open Questions

- 无；若在实现中发现其他 ODPS 输出边界用例，可一并在本变更内补充单测与修正。
