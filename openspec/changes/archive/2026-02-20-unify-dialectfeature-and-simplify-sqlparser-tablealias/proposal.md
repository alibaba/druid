## Why

`DialectFeature` usage is currently inconsistent across parser code paths, and `SQLParser.tableAlias()` carries dense branching that is hard to reason about and maintain safely. Unifying feature-gating patterns and reducing `tableAlias()` complexity now lowers refactor risk while preserving parser behavior and compatibility.

## What Changes

- Refactor parser internals in `core` to use a consistent `DialectFeature` access pattern in affected SQL parser paths.
- Decompose and simplify `SQLParser.tableAlias()` into clearer branch units while preserving token-consumption order and alias parsing outcomes.
- Add regression coverage for table-alias forms and feature-gated branches to ensure behavior equivalence.
- Keep existing parser public APIs and dialect dispatch contracts unchanged.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `sql-parser-core`: Extend parser refactor behavior-preservation requirements to cover unified `DialectFeature` usage and `SQLParser.tableAlias()` decomposition guarantees.

## Impact

- Affected modules: `core` only.
- Affected code: parser implementation under `core/src/main/java/com/alibaba/druid/sql/parser/`, primarily `SQLParser` and related feature-gated parsing paths.
- Affected tests: parser/BVT tests under `core/src/test/java/com/alibaba/druid/bvt/sql/` and parser unit tests.
- Public APIs: no changes expected.
- Backward compatibility: no intentional behavior changes; compatibility preserved via regression validation.
