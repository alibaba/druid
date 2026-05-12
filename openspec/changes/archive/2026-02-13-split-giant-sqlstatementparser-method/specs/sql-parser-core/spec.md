## MODIFIED Requirements

### Requirement: Parser Behavior Preservation During Refactoring
`SQLStatementParser` large-method decomposition SHALL preserve externally observable parsing behavior and parser error locality while improving internal method structure.

**Previous Behavior:**
Refactoring guidance was described mainly around Snowflake parser token-consumption cleanup and did not explicitly constrain giant-method decomposition in `SQLStatementParser`.

**New Behavior:**
When giant methods in `SQLStatementParser` are split into helper methods, parser branch ordering, token advancement semantics, AST output, and error diagnostics SHALL remain behavior-equivalent for existing supported SQL inputs.

**Migration Notes:**
No user migration is required. This is an internal parser maintainability refactor.

#### Scenario: Preserve AST output after method decomposition
- **WHEN** `SQLStatementParser` giant methods are decomposed into smaller helpers
- **THEN** parsing the same SQL input SHALL produce equivalent statement/AST semantics as before decomposition
- **AND** existing parser regression suites SHALL continue to pass

#### Scenario: Preserve optional-branch token behavior
- **WHEN** optional grammar clauses are present or absent in inputs handled by decomposed methods
- **THEN** token advancement and branch selection SHALL remain equivalent to pre-refactor behavior
- **AND** no new acceptance or rejection behavior SHALL be introduced

#### Scenario: Preserve parser error locality
- **WHEN** malformed SQL triggers parser errors in code paths touched by method decomposition
- **THEN** exceptions SHALL retain meaningful token/location context
- **AND** decomposition SHALL NOT suppress or generalize previously specific parse diagnostics
