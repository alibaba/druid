## MODIFIED Requirements

### Requirement: Dialect Provider Registration Lifecycle

The parser extension mechanism SHALL provide registration lifecycle operations for dialect parser providers, including register, replace, unregister, and lookup. These lifecycle operations SHALL remain the authoritative source used by the base parser dispatch path after decoupling from direct dialect type branching.

#### Scenario: Register provider for a new dialect key
- **WHEN** a caller registers a provider with a valid dialect key that has no existing provider
- **THEN** the registry SHALL store the provider and make it discoverable for subsequent parser resolution

#### Scenario: Replace existing provider atomically
- **WHEN** a caller registers a provider for a dialect key that already has a provider
- **THEN** the registry SHALL atomically replace the provider with the new one
- **AND** subsequent lookups SHALL resolve to the newly registered provider

#### Scenario: Unregister provider
- **WHEN** a caller unregisters a previously registered dialect key
- **THEN** the registry SHALL remove the provider binding
- **AND** lookups for that key SHALL return no registered provider

#### Scenario: Base parser observes registration lifecycle transitions
- **WHEN** registration lifecycle operations occur before or between parse calls
- **THEN** base parser dispatch SHALL observe the current registry state on each parse resolution
- **AND** dispatch SHALL NOT rely on stale dialect-to-parser bindings cached in base parser branches
