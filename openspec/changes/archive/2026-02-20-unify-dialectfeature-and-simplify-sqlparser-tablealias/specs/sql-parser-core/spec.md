## ADDED Requirements

### Requirement: Consistent DialectFeature Gating in Parser Flow
Parser code paths touched by refactoring SHALL evaluate `DialectFeature` with a consistent gating pattern so that equivalent feature flags are checked under equivalent parse conditions.

#### Scenario: Keep feature-gated branch behavior equivalent
- **WHEN** parser branches are refactored to use unified `DialectFeature` checks
- **THEN** equivalent inputs SHALL take the same feature-gated branch outcomes as before refactoring
- **AND** no new acceptance or rejection behavior SHALL be introduced solely due to gate-style unification

#### Scenario: Preserve fallback behavior when feature is disabled
- **WHEN** a dialect feature is disabled for a given parser context
- **THEN** parser flow SHALL follow the same fallback path semantics as pre-refactor behavior
- **AND** token consumption order in fallback paths SHALL remain behavior-equivalent

### Requirement: Table Alias Parsing Maintainability with Behavior Preservation
`SQLParser.tableAlias()` MAY be decomposed into smaller helper units, but it SHALL preserve alias parsing semantics, token progression, and error locality.

#### Scenario: Preserve accepted table alias forms
- **WHEN** valid SQL inputs use existing supported table alias forms
- **THEN** parsing results SHALL remain semantically equivalent before and after `tableAlias()` decomposition
- **AND** alias-related AST structure SHALL remain compatible with existing parser expectations

#### Scenario: Preserve rejection behavior for invalid alias syntax
- **WHEN** malformed or unsupported alias syntax is parsed through `tableAlias()` paths
- **THEN** parser SHALL continue to reject those inputs consistently with baseline behavior
- **AND** diagnostics SHALL retain meaningful token/location context

#### Scenario: Preserve optional-branch token movement in alias parsing
- **WHEN** optional alias tokens or delimiters are present or absent in supported syntax
- **THEN** token advancement order and branch selection SHALL remain behavior-equivalent to baseline
- **AND** decomposition SHALL NOT consume additional tokens beyond previous behavior
