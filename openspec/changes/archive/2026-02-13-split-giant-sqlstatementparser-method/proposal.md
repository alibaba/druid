## Why

`SQLStatementParser` currently contains oversized methods that mix multiple grammar branches, making behavior-preserving changes risky and review cost high. Splitting giant methods now improves maintainability and testability while keeping parser behavior stable for existing dialects.

## What Changes

- Refactor `core` parser internals by splitting large `SQLStatementParser` methods into smaller cohesive helpers with clear responsibilities.
- Keep parse semantics unchanged for existing SQL inputs and dialect flows.
- Preserve parser error reporting locality and token context after method decomposition.
- Add regression tests to verify behavior equivalence before and after decomposition.

## Capabilities

### New Capabilities
<!-- No new capability is introduced. -->

### Modified Capabilities
- `sql-parser-core`: refine refactoring-related requirements so large-method decomposition in `SQLStatementParser` must preserve dispatch, AST output, and error behavior.

## Impact

- Affected modules: `core`.
- Primary code scope: `core/src/main/java/com/alibaba/druid/sql/parser/SQLStatementParser.java` and related parser tests.
- Public API impact: none expected (internal refactor only).
- Backward compatibility: fully backward compatible; no behavior changes are intended.
- Dependencies: no new external dependencies.
