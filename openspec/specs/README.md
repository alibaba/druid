# Druid Architecture Capability Specs

This directory contains baseline capability specs for the current Druid architecture.

## Capability Map

- `sql-parser-core`: Lexer/Parser/AST pipeline and dialect dispatch behavior.
- `connection-pool-core`: Pool capacity, lifecycle tasks, and concurrency safety.
- `filter-chain`: Ordered interception and extensible filter integration.
- `wall-security`: AST-based SQL security validation with dialect awareness.
- `monitoring-stat`: Runtime statistics collection and exposure channels.

## How to Evolve

1. Create a change under `openspec/changes/<change-name>/`.
2. Add delta specs using `openspec/changes/<change-name>/specs/<capability>/spec.md`.
3. Run sync workflow (for example, `/opsx:sync`) to merge deltas into these main specs.
