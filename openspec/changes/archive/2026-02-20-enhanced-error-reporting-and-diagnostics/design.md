## Context

Error diagnostics in `core` lexer/parser paths are currently inconsistent in wording and context density across similar malformed-input branches. This causes avoidable debugging friction and weakens regression confidence for behavior-preserving refactors.  
This change focuses on improving diagnostic clarity and consistency while preserving parse acceptance/rejection behavior and token advancement semantics.

## Goals / Non-Goals

**Goals:**
- Improve consistency of parser/lexer error messages in touched branches.
- Preserve meaningful token/location context for malformed SQL diagnostics.
- Keep externally observable parse success/failure behavior unchanged.
- Add focused regression checks for diagnostic stability.

**Non-Goals:**
- No new SQL grammar support or dialect expansion.
- No full rewrite of lexer/parser error model.
- No public API contract changes outside diagnostic text quality in touched paths.

## Decisions

### Decision 1: Compatibility-first diagnostics refinement
- **Choice:** Refine diagnostics only in targeted branches with known inconsistency.
- **Why:** Minimizes behavior regression risk in shared parser infrastructure.
- **Alternative considered:** Broad message normalization across all parser code; rejected for scope/risk.

### Decision 2: Preserve failure boundaries as invariant
- **Choice:** Treat parse acceptance/rejection and token advancement as invariant; only improve message quality.
- **Why:** Existing consumers and tests depend on stable parser behavior.
- **Alternative considered:** Small behavior cleanups bundled with diagnostics; rejected to avoid conflating concerns.

### Decision 3: Validate with focused + full lanes
- **Choice:** Use focused malformed-input regression first, then full module and style gates.
- **Why:** Fast feedback during iteration plus release-level confidence.
- **Alternative considered:** Full-lane-only validation; rejected due to slower feedback loop.

## Risks / Trade-offs

- [Risk] Tight assertions on exact message text may make tests brittle.  
  -> Mitigation: Prefer assertions on required context segments instead of entire literal strings where appropriate.

- [Risk] Touching diagnostics in shared lexer code may affect multiple dialect tests.  
  -> Mitigation: Include representative lexer/parser suites spanning multiple dialect paths.

- [Trade-off] Incremental scope leaves some legacy diagnostic inconsistencies untouched.  
  -> Mitigation: Capture follow-up candidates in future focused changes.

## Migration Plan

1. Inventory inconsistent diagnostics in touched lexer/parser branches.
2. Apply localized message/context improvements with no parse behavior changes.
3. Add/extend regression tests for malformed input diagnostics.
4. Run focused suites, post-change perf/memory checks, full module tests, and style gate.
5. Roll back touched diagnostic branches if unexpected behavior regressions are found.

## Open Questions

- Should we define a shared parser-diagnostic message guideline for future changes?
- Do we want a dedicated diagnostics regression suite separate from behavior suites?
