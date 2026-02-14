## Purpose

Define baseline requirements for Druid SQL parser architecture, including lexer/parser layering, dialect dispatch, and behavior-preserving parser refactoring.
## Requirements
### Requirement: Layered Parsing Pipeline

The SQL parsing capability SHALL process SQL text through a layered pipeline of Lexer, Parser, and AST construction.

#### Scenario: Build AST from SQL text
- **WHEN** a SQL string is parsed through parser utilities
- **THEN** the parser SHALL tokenize input with a lexer before parser-level grammar handling
- **AND** the output SHALL be represented as AST statements or expressions

### Requirement: Dialect-Specific Parser Dispatch

The parser framework SHALL dispatch to dialect-specific parser implementations using a deterministic two-step strategy: resolve a registered dialect provider first, and fallback to built-in `DbType` parser dispatch when no provider is registered. The base parser class SHALL NOT directly branch on concrete dialect type values when selecting parser implementations.

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

#### Scenario: Keep base parser decoupled from concrete dialect branches
- **WHEN** parser implementation selection is executed from shared base parser flow
- **THEN** selection SHALL delegate through provider registration and fallback abstractions
- **AND** the base parser flow SHALL NOT require direct checks against concrete dialect type constants

### Requirement: Parser Behavior Preservation During Refactoring

Parser internal refactoring SHALL preserve externally observable parsing behavior, including Snowflake token-consumption refactors and `SQLStatementParser` giant-method decomposition changes.

**Previous Behavior:**
Snowflake parser used explicit `if (lexer.token() == X) { lexer.nextToken(); }` and `if (lexer.identifierEquals("X")) { lexer.nextToken(); }` patterns in multiple parsing branches. `SQLStatementParser` also contained oversized methods with dense branch dispatch logic.

**New Behavior:**
Snowflake parser uses equivalent helper methods (`lexer.nextIf`, `lexer.nextIfIdentifier`) for conditional token consumption while preserving statement parsing outcomes and AST semantics. `SQLStatementParser` giant methods may be decomposed into helper methods while keeping branch ordering, token advancement semantics, AST output, and parser diagnostics behavior-equivalent.

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

### Requirement: Parser Error Locality

Parser errors SHALL remain diagnosable with meaningful token and location context.

#### Scenario: Invalid SQL input
- **WHEN** malformed SQL cannot be parsed
- **THEN** parser exception messages SHALL include token-related context
- **AND** error reporting SHALL not silently swallow parser failures

