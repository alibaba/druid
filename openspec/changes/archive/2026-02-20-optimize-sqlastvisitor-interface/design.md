## Context

`SQLASTVisitor` is widely implemented across generic and dialect-specific visitor stacks in `core`. Over time, interface surface and default handling paths have become harder to reason about, especially when refactoring output/visit logic. This change focuses on interface-level maintainability and compatibility safety, without introducing intentional parser/formatting behavior changes.

## Goals / Non-Goals

**Goals:**
- Reduce `SQLASTVisitor` interface complexity in touched paths while keeping traversal semantics stable.
- Improve consistency of visitor contract usage across core and dialect-oriented implementations.
- Preserve compatibility expectations for existing visitor implementations used by parser/output flows.
- Add targeted regression checks to ensure no behavioral drift from interface optimization.

**Non-Goals:**
- No new SQL grammar or parser feature behavior.
- No large-scale rewrite of all visitor implementations in one pass.
- No mandatory migration requiring consumers to change runtime behavior immediately.

## Decisions

### Decision 1: Keep interface optimization incremental and compatibility-first
- **Choice:** Optimize the interface with localized, reviewable adjustments and compatibility-oriented defaults in touched paths.
- **Why:** Lowers regression risk in a highly shared abstraction.
- **Alternative considered:** Full interface redesign in one change; rejected due to migration and compatibility risk.

### Decision 2: Preserve observable visitor behavior as primary contract
- **Choice:** Treat emitted SQL/traversal outcomes as invariant and validate with focused regression tests.
- **Why:** Existing parser/output pipelines depend on stable visitor dispatch behavior.
- **Alternative considered:** Accept minor behavior shifts for cleanup; rejected as too risky for broad downstream impact.

### Decision 3: Validate through focused + full quality lanes
- **Choice:** Use focused visitor/parser regression lanes for fast signal, then full affected-module validation for completion.
- **Why:** Balances development speed and safety for shared interface refactors.
- **Alternative considered:** Full-lane-only validation; rejected due to slower iteration.

## Risks / Trade-offs

- [Risk] Interface-level changes may subtly impact dialect-specific visitor implementations.  
  -> Mitigation: include representative dialect/path regressions and preserve compatibility behavior in touched methods.

- [Risk] Cleanup could accidentally alter method dispatch ordering in edge cases.  
  -> Mitigation: preserve dispatch order where behavior-sensitive and add regression assertions around affected paths.

- [Trade-off] Incremental optimization leaves some legacy complexity in place but reduces migration burden.

## Migration Plan

1. Identify high-friction `SQLASTVisitor` interface and implementation touchpoints.
2. Apply compatibility-first interface optimization in small scoped edits.
3. Update/extend regression tests for affected visitor/parser/output paths.
4. Run focused + full module checks; rollback by reverting touched interface/visitor commits if needed.

## Open Questions

- Should deprecated compatibility methods be kept for one release cycle before broader cleanup?
- Is additional guidance needed for third-party custom visitor implementers that depend on legacy method patterns?
