## 1. Baseline and Scope Control

- [x] 1.1 运行拆分前基线测试（`MySqlPerfTest` 与内存测试）并记录结果
- [x] 1.2 梳理 `SQLASTOutputVisitor` 当前职责分区与高风险输出分支，确定迁移顺序

## 2. Core Refactoring

- [x] 2.1 设计并落地 `SQLASTOutputVisitor` 的拆分结构（主协调层 + 职责子单元）
- [x] 2.2 迁移通用输出逻辑并保持 token 顺序、空格、换行语义等价
- [x] 2.3 迁移方言相关输出分支并保持既有兼容行为
- [x] 2.4 收敛共享状态访问边界，避免新增跨单元隐式耦合

## 3. Regression Coverage

- [x] 3.1 增加单元测试覆盖拆分后通用 SQL 输出等价性
- [x] 3.2 增加单元测试覆盖方言输出关键路径等价性
- [x] 3.3 增加回归测试覆盖边界分支（复杂 DDL/DML/表达式输出）
- [x] 3.4 执行 parser/visitor 相关 BVT，确认外部 API 与输出语义无破坏性变化

## 4. Verification and Quality Gates

- [x] 4.1 运行拆分后 `MySqlPerfTest` 与内存测试，并与基线结果对比结论
- [x] 4.2 运行 `mvn test` 验证受影响模块测试通过
- [x] 4.3 运行 `mvn checkstyle:check`（或项目等效门禁命令）并修复违规
