## MODIFIED Requirements

### Requirement: Dialect-Specific Parser Dispatch
The parser framework SHALL dispatch to dialect-specific parser implementations using a deterministic two-step strategy: resolve a registered dialect provider first, and fallback to built-in `DbType` parser dispatch when no provider is registered.

**Previous Behavior:**
Parser creation relied on built-in `DbType` dispatch paths only.

**New Behavior:**
Parser creation first consults the dialect registration mechanism for a matching provider. If present, the registered provider is used. If absent, parser creation falls back to existing built-in `DbType` dispatch behavior.

**Migration Notes:**
No migration is required for existing callers. Systems that do not register custom providers retain current built-in behavior.

#### Scenario: Use registered provider when available
- **WHEN** caller parses SQL for a dialect key with a registered provider
- **THEN** parser creation SHALL use the registered provider
- **AND** built-in dispatch for that key SHALL NOT be selected for that call

#### Scenario: Fallback to built-in dispatch when provider missing
- **WHEN** caller parses SQL for a dialect key without a registered provider
- **THEN** parser creation SHALL use the existing built-in `DbType` dispatch path
- **AND** parse behavior SHALL remain equivalent to pre-registration baseline

#### Scenario: Resume built-in behavior after unregister
- **WHEN** a previously registered dialect key is unregistered and parsing is invoked again
- **THEN** parser creation SHALL fallback to built-in `DbType` dispatch
- **AND** no stale provider reference SHALL be used
