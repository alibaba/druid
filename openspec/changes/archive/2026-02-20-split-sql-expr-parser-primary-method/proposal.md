## Why

`SQLExprParser.primary()` currently mixes many parsing branches in a single oversized method, which increases change risk and makes parser diagnostics regressions harder to avoid during maintenance. Splitting it now improves readability and reviewability while preserving externally observable parser behavior.

## What Changes

- Refactor `core` module parser internals by decomposing `SQLExprParser.primary()` into focused helper paths without changing grammar acceptance rules.
- Preserve token advancement order, AST construction semantics, and parser error locality for existing inputs.
- Add regression coverage for representative expression forms and malformed inputs touched by the decomposition.
- Keep public parsing entry points and call contracts unchanged (no API migration required).

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `sql-parser-core`: Extend behavior-preserving refactor requirement coverage to include `SQLExprParser.primary()` decomposition and its scenario-level guarantees.

## Impact

- Affected code: `core/src/main/java/com/alibaba/druid/sql/parser/SQLExprParser.java` and nearby parser helpers in `core`.
- Affected tests: parser/BVT suites under `core/src/test/java/com/alibaba/druid/bvt/sql/`.
- Public APIs: no changes.
- Dependencies/build: no new runtime dependencies; existing Maven/test and checkstyle gates remain.
