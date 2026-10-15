## Context

`LexerFeature` and `ParserFeature` are used as behavior gates in parser control flow, but naming has accumulated inconsistencies in scope wording and semantic polarity. These inconsistencies increase maintenance cost, make code review slower, and can cause confusion when checking whether lexer/parser gates represent equivalent intent.

The change is a behavior-preserving refactor in the `core` SQL parser area and OpenSpec artifacts. It must keep parser acceptance/rejection semantics unchanged and remain backward compatible at runtime.

## Goals / Non-Goals

**Goals:**
- Define deterministic naming rules for `LexerFeature` and `ParserFeature` constants.
- Align equivalent lexer/parser feature-gate names to reduce ambiguity.
- Preserve parser behavior while improving readability and consistency.
- Provide migration and verification guidance for incremental adoption.

**Non-Goals:**
- Redesigning parser grammar or changing dialect dispatch behavior.
- Introducing new external dependencies or configuration properties.
- Renaming historical change directories or unrelated feature flags.

## Decisions

### Decision 1: Canonical naming contract for parser feature gates
- **Choice:** Use a canonical naming format based on consistent scope and intent wording across lexer and parser features.
- **Rationale:** A single naming contract improves readability and reduces accidental mismatches between related gates.
- **Alternative considered:** Keep existing mixed names and document exceptions. Rejected because exception-heavy guidance is hard to enforce.

### Decision 2: Behavior-preserving rename strategy
- **Choice:** Treat this as a naming refactor only, with explicit requirement that acceptance/rejection boundaries and token progression remain equivalent.
- **Rationale:** Feature-gate naming cleanup should not alter parser behavior.
- **Alternative considered:** Bundling naming cleanup with functional gate logic changes. Rejected to avoid coupling and risk.

### Decision 3: Validate consistency with scenario-based checks
- **Choice:** Add spec scenarios for naming consistency and gate parity expectations.
- **Rationale:** Scenarios create clear review and test targets for future changes.
- **Alternative considered:** Rely only on manual review without formal scenarios. Rejected due to lower repeatability.

## Risks / Trade-offs

- **[Risk] Renames can create temporary mixed naming in partial migrations** -> **Mitigation:** define incremental migration guidance and require review checks for parity.
- **[Risk] Contributors may interpret naming guidance differently** -> **Mitigation:** define normative wording with concrete examples and anti-patterns.
- **[Risk] Refactor touches broad parser files** -> **Mitigation:** keep changes scoped to naming and verify behavior equivalence through existing parser regression suites.

## Migration Plan

1. Add a dedicated capability for feature-gate naming requirements.
2. Update `sql-parser-core` requirement language to include naming consistency constraints.
3. Implement naming normalization in small, reviewable steps across touched parser paths.
4. Run parser regression checks to confirm behavior parity.

Rollback strategy: revert naming refactor commits; no data migration and no runtime API rollback required.

## Open Questions

- Should naming examples be centralized in one shared contributor document in addition to specs?
- Should future tooling enforce canonical naming automatically or keep it as review-time validation?
