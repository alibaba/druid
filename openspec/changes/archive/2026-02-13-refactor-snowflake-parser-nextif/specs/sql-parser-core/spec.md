## Capability Overview

Refine Snowflake parser internals to use `lexer.nextIf(...)` patterns while preserving parser behavior defined by `sql-parser-core`.

## MODIFIED Requirements

### Requirement: Parser Behavior Preservation During Refactoring

Snowflake parser internal refactoring SHALL preserve externally observable parsing behavior and SHALL allow a no-op implementation when eligible refactor candidates are absent.

**Previous Behavior:**
Snowflake parser used explicit `if (lexer.token() == X) { lexer.nextToken(); }` and `if (lexer.identifierEquals("X")) { lexer.nextToken(); }` patterns in multiple parsing branches.

**New Behavior:**
Snowflake parser uses equivalent helper methods (`lexer.nextIf`, `lexer.nextIfIdentifier`) for conditional token consumption while preserving statement parsing outcomes and AST semantics.

**Migration Notes:**
No external API or behavior migration is required. This is an internal readability refactor.

#### Scenario: Simplify token consumption checks in Snowflake parser
- **WHEN** Snowflake parser code replaces explicit token checks with `nextIf` helper calls
- **THEN** parsing results SHALL remain semantically equivalent for existing Snowflake SQL coverage
- **AND** existing Snowflake parser regression tests SHALL continue to pass

#### Scenario: Keep behavior for optional token branches
- **WHEN** optional clauses are present or absent in Snowflake SQL
- **THEN** parser control flow SHALL consume tokens under the same conditions as before refactoring
- **AND** no new acceptance or rejection behavior SHALL be introduced

#### Scenario: No-op applicability when target patterns are absent
- **GIVEN** the current Snowflake parser source does not contain eligible conditional token-consumption branches for this refactor
- **WHEN** the change is applied in this repository state
- **THEN** no Java source rewrite SHALL be required
- **AND** change completion SHALL rely on artifact/spec alignment and verification evidence
