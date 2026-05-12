## Context

`SQLParser.tableAlias()` and nearby parser flows currently contain dense conditional logic and mixed feature-gating styles. Different call sites check `DialectFeature` in slightly different ways, which makes the parser harder to maintain and increases the chance of accidental behavior drift during refactoring.

This change is scoped to `core` parser internals. It must preserve alias parsing behavior, token movement semantics, and existing compatibility expectations across dialects.

## Goals / Non-Goals

**Goals:**
- Standardize `DialectFeature` usage patterns in the affected parser code paths.
- Decompose `SQLParser.tableAlias()` into smaller helper units while keeping external behavior unchanged.
- Preserve token consumption order and accepted/rejected alias forms for existing inputs.
- Add regression tests for table-alias parsing paths and feature-gated branches.

**Non-Goals:**
- No intentional grammar expansion for alias syntax.
- No public API changes in parser entry points.
- No cross-module changes outside parser/test scope in `core`.

## Decisions

### Decision 1: Keep `tableAlias()` as orchestrator and extract branch helpers
- **Choice:** Keep `tableAlias()` as the stable entry point, move branch-specific logic into private helpers.
- **Why:** Minimizes caller impact and supports incremental, reviewable refactoring.
- **Alternative considered:** Full method rewrite in one pass; rejected due to elevated regression risk.

### Decision 2: Introduce a unified `DialectFeature` check pattern
- **Choice:** Use a consistent helper-oriented pattern for feature gates in touched paths.
- **Why:** Reduces duplicated conditional forms and makes behavior intent easier to audit.
- **Alternative considered:** Leave existing heterogeneous checks unchanged; rejected because readability debt remains.

### Decision 3: Validate behavior with focused parser regressions
- **Choice:** Add/extend tests for alias edge forms and feature-gated token branches.
- **Why:** These areas are sensitive to token-order regressions and are directly impacted by decomposition.
- **Alternative considered:** Depend only on broad suites; rejected for weaker localization of regressions.

## Risks / Trade-offs

- [Risk] Helper extraction may alter token consumption timing in optional alias branches.  
  -> Mitigation: preserve original decision order and validate with targeted alias-form tests.

- [Risk] Unified `DialectFeature` checks may unintentionally shift gate boundaries.  
  -> Mitigation: keep feature predicates semantically identical and verify with before/after regression inputs.

- [Trade-off] More helper methods increase method count but significantly reduce per-method cognitive load.

## Migration Plan

1. Refactor `tableAlias()` and adjacent feature-gated branches in small, behavior-preserving steps.
2. Add parser tests for representative alias forms and gated branches.
3. Run parser/BVT tests plus quality gates (`mvn test`, checkstyle).
4. Rollback strategy: revert refactor commits; no data migration required.

## Open Questions

- Should unified `DialectFeature` helpers remain local to parser classes or be centralized for broader reuse?
- Are there dialect-specific alias edge cases currently covered only by integration tests that need explicit unit coverage?
