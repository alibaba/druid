# Tasks: ODPS 方言优化

## 1. ODPS 输出访客 — not 与括号

- [x] 1.1 修复 OdpsOutputVisitor 中 SQLNotExpr 的括号逻辑：保证 `not (a)`、`(not b)` 等按原语义输出，避免括号丢失或错乱
- [x] 1.2 修复多括号输出：当表达式带 parenthesized 或等效多层括号时，序列化保留 `((a = 1))` 形式
- [x] 1.3 修复子查询表达式作为操作数时的括号：SQLQueryExpr 在二元表达式一侧时输出为 `(SELECT ...)` 形式

## 2. ODPS Select — QUALIFY 输出与 clone

- [x] 2.1 在 OdpsSelectQueryBlock.accept0(OdpsASTVisitor) 中增加对 this.qualify 的 acceptChild，使 OdpsOutputVisitor 能访问并打印 QUALIFY
- [x] 2.2 若 clone 后 qualify 仍丢失，在 OdpsSelectQueryBlock 的 clone/cloneTo 路径中显式复制 qualify 并验证

## 3. 测试与规范

- [x] 3.1 补充或沿用 docs/odps_fix.md 中的单测：not 括号、多括号、子查询表达式括号、clone QUALIFY，放入 core 的 ODPS 测试目录
- [x] 3.2 运行现有 ODPS 相关测试与 checkstyle，确认无回归
