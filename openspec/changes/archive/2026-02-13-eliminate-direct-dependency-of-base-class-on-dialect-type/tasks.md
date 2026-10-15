## 1. Baseline and Scope Lock

- [x] 1.1 运行架构改造前基线测试（`MySqlPerfTest` 与内存测试），记录吞吐、延迟与内存指标
- [x] 1.2 梳理并冻结当前 `SQLStatementParser` 方言分发入口与调用链，标注将被替换的直接方言分支点

## 2. Core Parser Refactor

- [x] 2.1 在 `core` 中收敛统一方言解析提供者入口，使基类通过抽象入口获取方言 parser
- [x] 2.2 移除/替换基类中的直接方言类型分支判断，保留未注册场景的内建 `DbType` 回退路径
- [x] 2.3 校验并发路径下注册、替换、注销与查找的可见性语义，避免基类使用陈旧绑定

## 3. Regression and Compatibility Tests

- [x] 3.1 增加单元测试覆盖“已注册方言优先”分发场景
- [x] 3.2 增加单元测试覆盖“未注册回退内建分发”场景
- [x] 3.3 增加单元测试覆盖“注销后恢复回退且无陈旧引用”场景
- [x] 3.4 执行现有 parser 回归/BVT，确认外部解析 API 行为与 AST 语义无破坏性变化

## 4. Verification and Quality Gates

- [x] 4.1 运行改造后 `MySqlPerfTest` 与内存测试，并与基线结果对比输出结论
- [x] 4.2 运行 `mvn test` 验证受影响模块测试通过
- [x] 4.3 运行 `mvn checkstyle:check` 并修复所有违规
