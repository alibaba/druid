## MODIFIED Requirements

### Requirement: Consistent DialectFeature Gating in Parser Flow
Parser code paths touched by refactoring SHALL evaluate `DialectFeature` with a consistent gating pattern so that equivalent feature flags are checked under equivalent parse conditions. Refactors that rename or align `LexerFeature` and `ParserFeature` gates SHALL preserve deterministic naming semantics for equivalent parser behaviors.

#### Scenario: Keep feature-gated branch behavior equivalent
- **WHEN** parser branches are refactored to use unified `DialectFeature` checks
- **THEN** equivalent inputs SHALL take the same feature-gated branch outcomes as before refactoring
- **AND** no new acceptance or rejection behavior SHALL be introduced solely due to gate-style unification

#### Scenario: Preserve fallback behavior when feature is disabled
- **WHEN** a dialect feature is disabled for a given parser context
- **THEN** parser flow SHALL follow the same fallback path semantics as pre-refactor behavior
- **AND** token consumption order in fallback paths SHALL remain behavior-equivalent

#### Scenario: Keep lexer and parser gate naming aligned for equivalent intent
- **WHEN** a refactor introduces or renames related `LexerFeature` and `ParserFeature` entries
- **THEN** equivalent intent across lexer and parser gates SHALL use consistent canonical naming terms
- **AND** naming alignment SHALL NOT change gate evaluation outcomes for existing inputs
