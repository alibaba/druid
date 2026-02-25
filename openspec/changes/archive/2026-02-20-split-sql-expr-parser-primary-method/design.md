## Context

`SQLExprParser.primary()` in `core` has accumulated many parsing branches and local control-flow decisions in one method. The current shape increases review cost and makes behavior-preserving edits difficult, especially for token advancement ordering and parser error locality guarantees.

This change is a refactoring in the `core` module only. It must preserve parser behavior and remain compatible with existing entry points and downstream dialect/parser integrations.

## Goals / Non-Goals

**Goals:**
- Decompose `SQLExprParser.primary()` into smaller helpers grouped by expression family/branch responsibility.
- Preserve externally observable behavior: grammar acceptance, AST semantics, token progression, and diagnostics locality.
- Keep the call contract of `primary()` stable for existing callers.
- Add regression tests for representative expression paths and malformed input paths touched by the split.

**Non-Goals:**
- No grammar expansion or intentional parser behavior changes.
- No public API change in parser utilities.
- No cross-module changes outside parser-related code and tests in `core`.

## Decisions

### Decision 1: Keep `primary()` as orchestration entry point
- **Choice:** Retain `primary()` as the top-level dispatch method and extract branch-specific logic into private helpers.
- **Why:** Preserves call sites and minimizes blast radius while improving local readability.
- **Alternatives considered:**
  - Replace `primary()` entirely with a new public method: rejected because it introduces unnecessary API churn risk.
  - Leave method as-is and only add comments: rejected because complexity remains high and future edits stay risky.

### Decision 2: Preserve token movement semantics with branch-local invariants
- **Choice:** Helper extraction follows existing token consumption points exactly, with no reordering of `lexer.nextToken()`-equivalent transitions.
- **Why:** Parser behavior in optional branches is highly sensitive to token timing.
- **Alternatives considered:**
  - Normalize token handling across helpers during split: rejected because it blends refactor and behavior change.

### Decision 3: Validate with focused expression-path regression tests
- **Choice:** Add tests covering literal/identifier/function-call style branches and malformed expressions that should preserve error locality.
- **Why:** These branches are high-frequency and likely to reveal accidental semantic drift.
- **Alternatives considered:**
  - Rely only on existing full-suite tests: rejected because targeted regressions improve signal for this refactor.

## Risks / Trade-offs

- [Risk] Helper extraction may accidentally reorder token advancement in optional branches.  
  -> Mitigation: keep one-to-one branch mapping and add targeted tests for present/absent optional tokens.

- [Risk] AST shape differences may appear in edge expressions.  
  -> Mitigation: add equivalence-focused regression cases for representative primary-expression forms.

- [Risk] Parser error messages could lose specific token context after decomposition.  
  -> Mitigation: include malformed input tests that assert token/location context remains meaningful.

- [Trade-off] More helper methods increase file size in exchange for lower per-method cognitive complexity.

## Migration Plan

1. Extract helpers from `primary()` in small, behavior-preserving commits or commit-equivalent steps.
2. Run parser/BVT tests after each extraction stage.
3. Run full module tests before merge.
4. Rollback strategy: revert refactor commit(s); no data/schema migration is involved.

## Open Questions

- Should we introduce package-private helper test hooks, or keep coverage through public parser entry paths only? (default: keep public-path testing only)
- Do any dialect-specific overrides assume incidental internal ordering in `primary()` beyond current contract? (to validate during implementation review)
