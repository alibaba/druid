## Why

The current SQL parser and visitor inheritance structure has grown organically, making extension points hard to reason about and increasing regression risk during refactors. We need to formalize and simplify the hierarchy now to improve maintainability while preserving dialect behavior and output compatibility.

## What Changes

- Refactor inheritance and responsibility boundaries in SQL parser/visitor code under `core` to reduce hierarchy coupling and duplicated behavior paths.
- Clarify base-vs-dialect extension contracts for parser and visitor flows so feature evolution remains predictable.
- Add/adjust regression tests to validate parser dispatch, AST visitor compatibility, and output behavior after hierarchy optimization.
- Keep runtime behavior and SQL compatibility aligned with existing dialect expectations; no intentional parser feature removals.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `sql-parser-core`: Strengthen requirements around parser/visitor inheritance contracts, including stable extension points, deterministic dispatch behavior, and compatibility-preserving refactor boundaries.

## Impact

- **Type**: Refactoring/enhancement (internal architecture optimization).
- **Affected modules**: Primarily `core` (`com.alibaba.druid.sql.parser` and `com.alibaba.druid.sql.visitor`), with test coverage updates in `core/src/test/java`.
- **Backward compatibility**: No intended breaking change in public SQL parsing/formatting behavior; existing dialect parsing expectations should remain compatible.
- **Public APIs**: No new public API planned; existing parser/visitor entry points remain available.
- **Dependencies/systems**: No new external dependencies; Maven module structure remains unchanged.
