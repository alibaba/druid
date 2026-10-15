## Why

Dialect parser dispatch in Druid is primarily bound to built-in `DbType` branches, which makes extension for custom or evolving SQL dialects costly and intrusive. A dialect registration mechanism is needed now to let integrators plug in dialect parser factories without forking core parser dispatch logic.

## What Changes

- Add an extension mechanism in `core` to register and resolve dialect parser providers by dialect key.
- Define a stable registration lifecycle: register, replace, unregister, and lookup with deterministic precedence.
- Integrate registration-aware resolution into parser entry points while preserving built-in dialect behavior as fallback.
- Add validation and thread-safety requirements for concurrent registration and parsing calls.
- Add tests for provider registration, precedence, fallback behavior, and concurrent access.

## Capabilities

### New Capabilities
- `dialect-registration`: Runtime registration contract for dialect parser providers, including lifecycle and conflict handling.

### Modified Capabilities
- `sql-parser-core`: extend dialect dispatch requirements to include registry-based provider resolution before built-in fallback.

## Impact

- Affected modules: `core`.
- Likely affected areas: `core/src/main/java/com/alibaba/druid/sql/parser/` and related parser factory/utility classes.
- Public API impact: new registration APIs for dialect parser providers; existing parser APIs remain compatible.
- Backward compatibility: existing built-in dialect parsing remains default behavior when no custom provider is registered.
- Dependencies: no new third-party dependencies expected.
