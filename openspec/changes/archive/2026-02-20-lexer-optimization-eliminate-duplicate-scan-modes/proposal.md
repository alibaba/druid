## Why

`Lexer` in `core` still carries duplicated scan-mode branches that implement equivalent tokenization decisions with slightly different control flow. This increases maintenance cost and makes behavior-preserving refactors riskier; now is a good time to consolidate these paths while keeping parser-observable behavior stable.

## What Changes

- Refactor duplicated lexer scan-mode branches into shared helper paths with compatibility-first behavior.
- Keep token classification, token advancement order, and error locality behavior-equivalent for existing parser flows.
- Add focused regression coverage for lexer paths touched by scan-mode deduplication.
- Record baseline and post-change validation evidence (focused tests plus perf/memory spot checks).
- No intentional SQL syntax support change and no **BREAKING** runtime contract change.

## Capabilities

### New Capabilities
- `<none>`: No new capability is introduced; this change optimizes existing lexer internals.

### Modified Capabilities
- `sql-parser-core`: Extend behavior-preserving refactor expectations to explicitly cover lexer scan-mode deduplication compatibility.

## Impact

- **Affected modules**: `core` (`com.alibaba.druid.sql.parser` lexer code and related tests).
- **Change type**: Internal refactoring / maintainability enhancement.
- **Backward compatibility**: Expected backward compatible; no intended parser-visible behavior change.
- **Public APIs**: No public API change expected.
- **Dependencies/systems**: No new runtime dependencies.
