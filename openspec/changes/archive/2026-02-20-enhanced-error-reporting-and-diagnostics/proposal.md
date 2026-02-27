## Why

Current parser error messages and diagnostics context are uneven across lexer/parser failure paths, which makes debugging malformed SQL slower and less predictable for maintainers and users. We should improve diagnostic consistency now to reduce triage cost while keeping parsing behavior and failure boundaries compatible.

## What Changes

- Enhance error reporting in touched lexer/parser branches to include clearer token/location context with consistent message structure.
- Preserve existing acceptance/rejection behavior while improving diagnostics clarity for malformed inputs.
- Add focused regression checks that validate diagnostic stability for representative malformed SQL cases.
- Define structured baseline vs post-change evidence expectations for diagnostics-focused refactors.
- No intentional SQL grammar expansion and no **BREAKING** API/contract change.

## Capabilities

### New Capabilities
- `<none>`: No new capability is introduced; this change improves diagnostics within existing parser capability boundaries.

### Modified Capabilities
- `sql-parser-core`: Strengthen requirements for parser/lexer diagnostic consistency and error-locality preservation in behavior-preserving refactors.

## Impact

- **Affected modules**: Primarily `core` (`com.alibaba.druid.sql.parser` lexer/parser and related tests).
- **Change type**: Enhancement / maintainability improvement (diagnostic quality).
- **Backward compatibility**: Backward compatible for parse results; only diagnostic quality/consistency is improved.
- **Public APIs**: No public API changes expected.
- **Dependencies/systems**: No new runtime dependencies.
