## MODIFIED Requirements

### Requirement: Parser Behavior Preservation During Refactoring

Parser internal refactoring SHALL preserve externally observable parsing behavior, including Snowflake token-consumption refactors, `SQLStatementParser` giant-method decomposition changes, and `SQLASTOutputVisitor` decomposition.

**Previous Behavior:**
Snowflake parser used explicit `if (lexer.token() == X) { lexer.nextToken(); }` and `if (lexer.identifierEquals("X")) { lexer.nextToken(); }` patterns in multiple parsing branches. `SQLStatementParser` also contained oversized methods with dense branch dispatch logic. `SQLASTOutputVisitor` output logic was centralized in a large class with mixed responsibilities.

**New Behavior:**
Snowflake parser uses equivalent helper methods (`lexer.nextIf`, `lexer.nextIfIdentifier`) for conditional token consumption while preserving statement parsing outcomes and AST semantics. `SQLStatementParser` giant methods may be decomposed into helper methods while keeping branch ordering, token advancement semantics, AST output, and parser diagnostics behavior-equivalent. `SQLASTOutputVisitor` may be split into smaller responsibilities while preserving emitted SQL token order, formatting semantics, and dialect-specific output compatibility.

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

#### Scenario: Preserve AST output after method decomposition
- **WHEN** `SQLStatementParser` giant methods are decomposed into smaller helpers
- **THEN** parsing the same SQL input SHALL produce equivalent statement/AST semantics as before decomposition
- **AND** existing parser regression suites SHALL continue to pass

#### Scenario: Preserve optional-branch token behavior in decomposed paths
- **WHEN** optional grammar clauses are present or absent in inputs handled by decomposed methods
- **THEN** token advancement and branch selection SHALL remain equivalent to pre-refactor behavior
- **AND** no new acceptance or rejection behavior SHALL be introduced

#### Scenario: Preserve parser error locality after decomposition
- **WHEN** malformed SQL triggers parser errors in code paths touched by method decomposition
- **THEN** exceptions SHALL retain meaningful token/location context
- **AND** decomposition SHALL NOT suppress or generalize previously specific parse diagnostics

#### Scenario: Preserve SQL output semantics after visitor split
- **WHEN** `SQLASTOutputVisitor` implementation is decomposed into smaller units
- **THEN** formatting output for the same AST input SHALL remain semantically equivalent to pre-split behavior
- **AND** emitted token order, keyword placement, and dialect-specific rendering SHALL remain compatible with existing regression baselines
