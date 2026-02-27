## Context

`core` 模块中的 `Lexer` 承担大量关键 token 扫描路径，不同 scan mode 在历史演进中出现了重复分支与近似逻辑。这些重复实现提高了维护成本，也增加了后续 parser/lexer 重构时的行为漂移风险。  
本次变更聚焦 `com.alibaba.druid.sql.parser` 范围内的 lexer 内部实现，目标是在不改变外部可观察行为的前提下收敛重复 scan mode 逻辑，并提供结构化基线/变更后验证证据。

## Goals / Non-Goals

**Goals:**
- 消除 lexer 中重复 scan mode 分支，提炼共享路径提升可读性与可维护性。
- 保持 token 分类、token 推进顺序、异常定位与诊断语义的兼容性。
- 在受影响 parser 路径上补充回归验证，确保行为等价。
- 通过 focused + full 验证路径提供性能与内存对比证据。

**Non-Goals:**
- 不引入新的 SQL 语法能力或方言行为扩展。
- 不进行 lexer 的全量重写或跨模块架构迁移。
- 不改变已有公开 API 或要求调用方迁移。

## Decisions

### Decision 1: 渐进式去重，兼容优先
- **Choice:** 对重复 scan mode 分支做小步提炼，优先复用现有判定与推进顺序。
- **Why:** lexer 是高频共享路径，渐进式重构回归风险更可控。
- **Alternative considered:** 一次性重写 scan 流程；因风险和验证成本过高而放弃。

### Decision 2: 以“可观察行为”等价为首要契约
- **Choice:** 将 token 序列、分支选择、异常上下文视为等价性基准。
- **Why:** parser 与格式化链路依赖 lexer 细粒度行为稳定性。
- **Alternative considered:** 允许轻微行为变化以换取更大清理空间；因兼容风险拒绝。

### Decision 3: 使用分层验证策略
- **Choice:** 先跑 focused lexer/parser 回归，再跑 `core` 全量测试与样式门禁。
- **Why:** 在迭代速度和安全性之间取得平衡。
- **Alternative considered:** 仅跑全量测试；反馈慢，不利于快速定位问题。

## Risks / Trade-offs

- [Risk] 去重后共享分支可能在边界 token 上改变推进顺序。  
  -> Mitigation: 增加 edge/malformed 输入回归断言，覆盖 token 推进与异常上下文。

- [Risk] 某些方言路径依赖历史分支细节。  
  -> Mitigation: 选择代表性方言回归集合，验证 parser/output 行为等价。

- [Trade-off] 渐进式方案可能保留一部分历史复杂度。  
  -> Mitigation: 先收敛高重复区域，后续在独立 change 持续优化。

## Migration Plan

1. 盘点并标记重复 scan mode 分支及影响范围。  
2. 提炼共享逻辑并替换重复实现，保持判定条件与推进顺序。  
3. 增补/更新 lexer 相关回归测试（含异常与边界场景）。  
4. 执行 focused 回归、性能/内存基线对比、`core` 全量测试与样式检查。  
5. 若出现行为漂移，按最小粒度回滚到上一步并重新验证。

## Open Questions

- 是否需要在后续版本中引入更明确的 scan-mode 内部契约文档，供新增方言复用？
- 是否应补充长期的 lexer 微基准集合，以持续追踪去重后的性能趋势？
