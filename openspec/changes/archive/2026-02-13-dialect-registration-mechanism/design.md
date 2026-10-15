## Context

Druid SQL parsing currently resolves dialect behavior primarily through built-in `DbType` dispatch paths. Extending parser support for private or emerging dialects often requires modifying core switch/if branches, which couples extension delivery to core code changes and release cycles.

This change introduces a registration-based extension point in `core` so custom dialect parser providers can be plugged in at runtime while preserving existing built-in behavior.

## Goals / Non-Goals

**Goals:**
- Provide a deterministic registry for dialect parser providers keyed by dialect identity.
- Keep parser entry APIs backward compatible and preserve current behavior when no provider is registered.
- Define concurrency-safe registration and lookup behavior for multi-threaded parse workloads.
- Enable targeted tests for registration lifecycle, precedence, fallback, and error paths.

**Non-Goals:**
- No redesign of existing lexer/parser class hierarchy.
- No mandatory migration for existing built-in dialect flows.
- No new external dependency or plugin framework.
- No dynamic class loading protocol in this iteration.

## Decisions

### Decision 1: Add a dedicated dialect registry abstraction in `core`

- **Alternatives considered**
  - Reuse ad-hoc static maps in parser utility classes.
  - Add a dedicated registry component with typed provider contracts.
- **Decision**
  - Use a dedicated registry abstraction to centralize registration lifecycle and validation.
- **Rationale**
  - Reduces duplication and makes thread-safety and precedence rules explicit.

### Decision 2: Resolve custom provider first, then fallback to built-in dispatch

- **Alternatives considered**
  - Keep built-in dispatch first and only consult registry on miss.
  - Use registry-first, then built-in fallback.
- **Decision**
  - Use registry-first resolution with deterministic fallback to current built-in dispatch.
- **Rationale**
  - Supports overrides and private dialect variants without altering upstream dispatch code.

### Decision 3: Use atomic replace semantics for duplicate registration

- **Alternatives considered**
  - Reject duplicate registration.
  - Replace existing provider atomically and return previous provider.
- **Decision**
  - Replace atomically to simplify operational updates and rollback.
- **Rationale**
  - Keeps behavior predictable under hot updates and avoids out-of-band cleanup requirements.

### Decision 4: Use concurrent map + immutable provider contracts for thread safety

- **Alternatives considered**
  - Synchronized global lock for all registry operations.
  - Concurrent map operations with minimal synchronization.
- **Decision**
  - Use concurrent map operations and require provider contract to be thread-safe for concurrent parser creation.
- **Rationale**
  - Fits high-concurrency parser usage while avoiding coarse lock contention.

## Risks / Trade-offs

- **[Risk] Registry misuse through invalid dialect keys** -> **Mitigation:** validate keys and fail fast with explicit exceptions.
- **[Risk] Provider override changes parse behavior unexpectedly** -> **Mitigation:** document precedence clearly and require regression tests for overrides.
- **[Risk] Concurrency bugs during register/unregister under load** -> **Mitigation:** define atomic semantics and add multi-threaded tests.
- **[Risk] Performance regression on parser creation path** -> **Mitigation:** benchmark `MySqlPerfTest` baseline vs post-change and keep lookup O(1).

## Migration Plan

1. Introduce registry APIs and no-op integration path (no behavior change without registrations).
2. Wire parser entry resolution to consult registry before built-in dispatch.
3. Add unit/concurrency tests for lifecycle and fallback guarantees.
4. Validate baseline vs post-change performance/memory metrics for parser hot paths.
5. Rollback strategy: disable custom registrations or remove registry integration branch to return to pure built-in dispatch.

## Open Questions

- Should registration keys be strictly `DbType`-aligned or allow custom string namespaces for private dialects?
- Should registration APIs expose read-only snapshot/inspection methods for diagnostics?
- Is explicit priority ordering needed beyond single-provider-per-key replacement semantics?
