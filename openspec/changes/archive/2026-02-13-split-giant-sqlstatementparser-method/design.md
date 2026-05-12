## Context

`SQLStatementParser` contains giant parsing methods with many grammar branches packed in single blocks. This structure increases cognitive load, makes localized fixes risky, and complicates behavior-equivalence verification during refactoring.

The change focuses on method decomposition inside `core` parser internals while preserving externally observable parse behavior and error diagnostics.

## Goals / Non-Goals

**Goals:**
- Split oversized `SQLStatementParser` methods into smaller helper methods organized by grammar concern.
- Preserve SQL parsing output equivalence (AST structure/statement sequence) for existing supported input sets.
- Keep parser error locality and token-context diagnostics unchanged.
- Improve testability by enabling targeted unit/regression coverage around decomposed branches.

**Non-Goals:**
- No new SQL grammar feature introduction.
- No changes to public parser APIs or external caller contracts.
- No dialect-dispatch redesign in this change.
- No new dependencies or runtime extension mechanisms.

## Decisions

### Decision 1: Decompose by grammar boundary, not by line count
- **Options considered:** split mechanically by line count vs split along semantic grammar segments.
- **Decision:** decompose around grammar boundaries (e.g., DDL/DML branch helpers, shared clause helpers).
- **Rationale:** semantic boundaries produce maintainable helpers and lower regression risk.

### Decision 2: Preserve parse entry flow and token advancement contract
- **Options considered:** reorder parse flow for readability vs keep current branch order and token progression.
- **Decision:** keep entry flow/token advancement order stable; only extract equivalent helper calls.
- **Rationale:** minimizes subtle behavior drift in optional/ambiguous grammar paths.

### Decision 3: Use characterization tests before/after decomposition
- **Options considered:** rely on existing suite only vs add targeted characterization regression tests.
- **Decision:** add focused parser tests for representative giant-method branches and error cases.
- **Rationale:** provides explicit evidence that refactor did not alter behavior.

### Decision 4: Gate completion with perf/memory comparison
- **Options considered:** skip benchmark for refactor vs run baseline/post metrics.
- **Decision:** keep MySqlPerf and memory comparison in task checklist.
- **Rationale:** guards against accidental performance regressions from helper extraction.

## Risks / Trade-offs

- **[Risk] Hidden behavior drift in rare grammar branch** -> **Mitigation:** add branch-focused regression fixtures and compare AST/text outputs.
- **[Risk] Token movement side effects after extraction** -> **Mitigation:** preserve call ordering and add tests for optional clause presence/absence.
- **[Risk] Large diff size reduces review quality** -> **Mitigation:** refactor in logically grouped commits/tasks by method cluster.
- **[Risk] Minor perf overhead from extra method calls** -> **Mitigation:** benchmark before/after and avoid deep call chains in hot loops.

## Migration Plan

1. Establish baseline parser performance/memory metrics.
2. Identify top giant methods and extract helper methods incrementally by grammar boundary.
3. Add/extend regression tests for touched branches and parser error locality.
4. Run parser-focused suites plus perf/memory comparison.
5. Rollback strategy: revert affected helper extraction chunks if behavior/performance regresses.

## Open Questions

- Which `SQLStatementParser` methods exceed agreed complexity threshold and should be split in phase 1 vs phase 2?
- Should helper methods stay private in `SQLStatementParser` or be moved to dedicated parser support classes later?
- Is an internal guideline needed to cap parser method complexity for future contributions?
