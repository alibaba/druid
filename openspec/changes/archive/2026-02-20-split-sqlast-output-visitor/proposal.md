## Why

`SQLASTOutputVisitor` 体量持续增长，单类承担过多输出分支逻辑，导致阅读、定位问题和安全重构成本都很高。现在需要在不改变外部 SQL 输出行为的前提下进行拆分，以降低维护复杂度并提升后续迭代效率。

## What Changes

- 在 `core` 模块中将 `SQLASTOutputVisitor` 的巨型实现按职责拆分为更小的内部组件/辅助方法集合。
- 保持现有 visitor 对外 API 与输出语义不变，确保格式化和方言输出行为兼容。
- 为拆分后的关键路径补充回归测试，覆盖典型语句输出与边界分支。
- 统一拆分后的调用边界，减少跨分支共享状态导致的隐式耦合。

## Capabilities

### New Capabilities
- 无

### Modified Capabilities
- `sql-parser-core`: 扩展“重构期间行为保持”约束，增加 `SQLASTOutputVisitor` 拆分场景下的输出行为等价性要求。

## Impact

- 受影响模块：`core`（`com.alibaba.druid.sql.visitor` 相关输出访问器实现）。
- 公共 API：预计无新增或破坏性变更；既有 visitor 使用方式保持不变。
- 兼容性：保持向后兼容，输出文本语义与格式规则维持既有基线。
- 依赖与系统：无新增外部依赖；主要影响内部结构、回归测试与可维护性。
