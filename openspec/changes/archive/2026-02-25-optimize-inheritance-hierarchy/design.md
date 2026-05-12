## Context

The `core` module SQL parser/visitor stack currently carries deep and partially overlapping inheritance paths across `Lexer`, parser layers, and AST visitor implementations. This increases cognitive load for maintenance and makes behavior-preserving refactors difficult, especially where dialect-specific classes override shared logic with subtle differences.

Recent parser optimizations and visitor split work indicate a need to make inheritance boundaries explicit and enforce consistent extension contracts. The change must preserve existing SQL parse/output behavior and avoid public API breakage.

## Goals / Non-Goals

**Goals:**
- Simplify parser/visitor inheritance so base responsibilities are explicit and reusable.
- Preserve dialect dispatch and output compatibility while reducing duplicate override paths.
- Define stable extension points for future dialect-specific behavior without reintroducing hierarchy coupling.
- Add focused regression coverage for hierarchy-related behavior contracts.

**Non-Goals:**
- Introducing new SQL grammar features or dialect capabilities.
- Replacing the existing parser architecture with a new parsing engine.
- Adding new external dependencies or changing module boundaries.

## Decisions

1. **Normalize base contracts before dialect overrides**
   - Decision: tighten default behavior in base parser/visitor abstractions, then keep dialect classes focused on true specialization.
   - Rationale: avoids repeated "copy-then-tweak" overrides and reduces accidental divergence.
   - Alternative considered: flattening more behavior directly into dialect classes; rejected due to long-term duplication and harder testing.

2. **Split high-complexity methods into role-focused helpers**
   - Decision: break large inheritance-sensitive methods into smaller, named helper flows where each helper has one responsibility boundary.
   - Rationale: clearer override points and easier targeted regression tests.
   - Alternative considered: keeping monolithic methods and relying on comments; rejected because readability and extension safety remain weak.

3. **Preserve visitor compatibility via adapter/support layer**
   - Decision: maintain existing visitor entry behavior while introducing support classes for binary-op and inheritance-related dispatch.
   - Rationale: enables internal cleanup without changing public traversal expectations.
   - Alternative considered: direct visitor interface redesign without compatibility bridge; rejected due to high regression risk for custom visitors.

4. **Validate behavior with hierarchy regression suites**
   - Decision: codify contract-level tests for parser dispatch, alias parsing, diagnostics, and visitor inheritance behavior.
   - Rationale: refactor safety depends on explicit compatibility assertions.
   - Alternative considered: only smoke tests; rejected as insufficient for inheritance regressions.

## Risks / Trade-offs

- **[Risk] Hidden dialect behavior relies on implicit inheritance side-effects** -> **Mitigation**: add dialect-focused regression cases before/after refactor and keep fallback paths in base classes.
- **[Risk] Visitor dispatch order changes could affect SQL formatting output** -> **Mitigation**: add output visitor regression tests for representative binary expressions and ensure deterministic traversal.
- **[Risk] Refactor adds short-term indirection** -> **Mitigation**: constrain helper splits to clear responsibility boundaries and document invariants in code comments where needed.

## Migration Plan

1. Establish baseline via existing parser/visitor and dialect regression tests in `core`.
2. Apply inheritance boundary refactors in parser and visitor layers with behavior-preserving checkpoints.
3. Introduce/adjust compatibility support classes and keep existing entry points stable.
4. Expand regression suites for hierarchy contracts, parser error diagnostics, and output compatibility.
5. Validate no public API breakage and no dependency changes; merge once all tests pass.

Rollback strategy: revert the change set at module level if parser/visitor compatibility regressions appear; no schema/data migration is involved.

## Open Questions

- Are there any downstream custom visitor implementations that implicitly depend on undocumented traversal ordering?
- Should we formalize additional extension contract notes in `openspec/specs/sql-parser-core/spec.md` for future refactors?

## Validation Plan

- Functional regression: run parser and visitor related unit/BVT suites in `core/src/test/java`.
- Compatibility checks: verify representative SQL parse-tree and output formatting invariants.
- Performance and memory: run available parser-related micro/perf tests (including `MySqlPerf*` where applicable) and compare before/after baseline to ensure no material regression.
