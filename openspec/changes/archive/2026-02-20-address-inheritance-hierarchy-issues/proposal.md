## Why

Recent parser refactors exposed fragility in parts of the SQL AST inheritance hierarchy: similar node families do not consistently share traversal contracts, which increases maintenance risk and makes behavior-preserving refactors harder to validate. We should address this now to reduce future regressions while the related parser/visitor code paths are still actively being improved.

## What Changes

- Standardize inheritance contracts for selected SQL AST base/derived node families in `core` to reduce duplicated or divergent behavior.
- Clarify and align visitor dispatch expectations across those inheritance branches so equivalent nodes follow consistent traversal semantics.
- Keep parse success/failure behavior and external SQL output compatibility unchanged; this is a compatibility-first structural cleanup.
- Add focused regression coverage around inheritance-sensitive traversal/dispatch paths to prevent behavioral drift.
- No **BREAKING** API change is intended.

## Capabilities

### New Capabilities
- `<none>`: No new capability is introduced; this change refines existing parser-core behavior contracts.

### Modified Capabilities
- `sql-parser-core`: Tighten requirements for inheritance-hierarchy consistency and visitor/dispatch behavior preservation during structural refactoring.

## Impact

- **Affected modules**: Primarily `core` (`com.alibaba.druid.sql.ast`, `com.alibaba.druid.sql.visitor`, and related parser/visitor tests).
- **Change type**: Refactoring + maintainability enhancement.
- **Backward compatibility**: Intended to be backward compatible for parsing outcomes and generated SQL text.
- **Public APIs**: No intentional public API additions/removals; behavior-level contracts are clarified and regression-tested.
- **Dependencies/systems**: No new runtime dependencies.
