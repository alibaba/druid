## Why

`LexerFeature` and `ParserFeature` naming currently mixes inconsistent prefixes and semantics, which makes feature gates harder to read and increases review cost when tracing parser behavior. Standardizing naming now improves maintainability and reduces mistakes when adding or evaluating feature flags.

## What Changes

- Define a unified naming convention for `LexerFeature` and `ParserFeature` constants, including prefix pattern, polarity, and scope wording.
- Align feature names across lexer/parser flows so equivalent gates use predictable terms.
- Add migration guidance for incremental rename and compatibility-preserving fallback handling during refactor.
- Add verification expectations to ensure behavior remains unchanged while names are normalized.
- No runtime behavior changes are intended; this is a refactoring and clarity enhancement.

## Capabilities

### New Capabilities
- `feature-gate-naming`: Naming and consistency contract for parser-related feature flags.

### Modified Capabilities
- `sql-parser-core`: Refine requirement language for deterministic and maintainable feature-gate naming in lexer/parser paths while preserving behavior.

## Impact

- Affected modules: `core` (`com.alibaba.druid.sql.parser` and related parser feature-gate paths), plus `openspec` artifacts.
- Public API impact: none expected (internal naming and maintainability focus).
- Dependency impact: none.
- Backward compatibility: parser acceptance/rejection behavior remains unchanged; rename work should preserve existing functional outcomes and diagnostics.
