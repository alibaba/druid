## Context

`core` test code has grown organically across parser unit tests, BVT suites, and benchmark-style checks. This has led to repeated fixture setup patterns, uneven assertion style, and inconsistent evidence capture for behavior-preserving refactors. The change targets test infrastructure only (primarily `core/src/test/java` and related docs/process), with no production runtime contract change.

## Goals / Non-Goals

**Goals:**
- Define a reusable test-infrastructure baseline for parser and BVT-oriented tests in `core`.
- Improve consistency of regression tests for refactors, including stable patterns for behavior-equivalence evidence.
- Keep local and CI validation efficient by clarifying focused-vs-full test execution strategy.
- Make test additions simpler and less error-prone through shared helper conventions.

**Non-Goals:**
- No new runtime features in datasource, parser, or filter production paths.
- No replacement of JUnit framework or build toolchain.
- No immediate rewrite of every historical test file in one change.

## Decisions

### Decision 1: Introduce incremental test infrastructure conventions instead of a one-shot migration
- **Choice:** Add conventions and helper patterns that can be adopted gradually by touched tests.
- **Why:** Limits migration risk and avoids large noisy refactors.
- **Alternative considered:** Bulk conversion of all existing tests; rejected due to high churn and review cost.

### Decision 2: Standardize parser/BVT regression evidence structure
- **Choice:** Require behavior-sensitive refactors to capture baseline and post-change evidence using consistent command scopes and result summaries.
- **Why:** Improves confidence in refactors and simplifies reviews/archival checks.
- **Alternative considered:** Leave evidence format ad hoc; rejected because comparability remains weak.

### Decision 3: Define focused verification lanes plus full-lane fallback
- **Choice:** Document a small targeted lane for fast iteration and a broader lane (`mvn test`/selected suites) for final validation.
- **Why:** Balances developer velocity and release safety.
- **Alternative considered:** Always run full test matrix; rejected for slower local feedback.

### Decision 4: Keep all changes within existing module/dependency boundaries
- **Choice:** Implement using existing test framework and utilities, without introducing required runtime dependencies.
- **Why:** Maintains compatibility and keeps adoption low-friction.
- **Alternative considered:** Add new third-party testing stack; rejected for scope creep.

## Risks / Trade-offs

- [Risk] Convention-only guidance may be applied inconsistently across teams.  
  -> Mitigation: encode expectations in delta specs/tasks and require evidence notes for affected refactor changes.

- [Risk] Focused test lanes may miss unrelated regressions if used alone.  
  -> Mitigation: keep full-lane validation as required quality gate before completion.

- [Trade-off] Incremental adoption improves safety but delays full uniformity of legacy tests.

## Migration Plan

1. Define capability-level requirements for test infrastructure and parser-refactor evidence quality.
2. Land helper/convention updates in `core` test scope with representative conversions.
3. Validate using focused suites first, then full affected-module tests and style gates.
4. Rollback strategy: revert test-infrastructure commits; production behavior remains unaffected.

## Open Questions

- Should common test helpers remain package-local to parser/BVT domains or be centralized under a shared test utility package?
- Do we need an additional CI job that enforces evidence-note completeness for specific refactor labels?
