## MODIFIED Requirements

### Requirement: Parser Behavior Preservation During Refactoring

Parser internal refactoring SHALL preserve externally observable parsing behavior, including Snowflake token-consumption refactors, `SQLStatementParser` giant-method decomposition changes, `SQLASTOutputVisitor` decomposition, `SQLExprParser.primary()` decomposition, `SQLASTVisitor` interface optimization, and lexer scan-mode deduplication. Refactor verification SHALL also include structured, comparable baseline/post-change regression evidence for touched parser paths.

**Previous Behavior:**
Snowflake parser used explicit `if (lexer.token() == X) { lexer.nextToken(); }` and `if (lexer.identifierEquals("X")) { lexer.nextToken(); }` patterns in multiple parsing branches. `SQLStatementParser` also contained oversized methods with dense branch dispatch logic. `SQLASTOutputVisitor` output logic was centralized in a large class with mixed responsibilities. `SQLExprParser.primary()` concentrated multiple expression parsing branches in one large method. `SQLASTVisitor` contract usage in some areas relied on broad interface surface with uneven default handling patterns. Lexer scan logic also contained duplicated scan-mode branches with overlapping tokenization behavior.

**New Behavior:**
Snowflake parser uses equivalent helper methods (`lexer.nextIf`, `lexer.nextIfIdentifier`) for conditional token consumption while preserving statement parsing outcomes and AST semantics. `SQLStatementParser` giant methods may be decomposed into helper methods while keeping branch ordering, token advancement semantics, AST output, and parser diagnostics behavior-equivalent. `SQLASTOutputVisitor` may be split into smaller responsibilities while preserving emitted SQL token order, formatting semantics, and dialect-specific output compatibility. `SQLExprParser.primary()` may be decomposed into focused helper methods while preserving expression acceptance rules, token progression semantics, AST construction, and parser error locality. `SQLASTVisitor` interface and touched implementations may be optimized for maintainability while preserving visit dispatch semantics and compatibility expectations for existing parser/output flows. Lexer duplicated scan-mode branches may be consolidated into shared paths while preserving token classification, token advancement order, and diagnostics locality. Verification for parser refactors includes a consistent baseline/post-change evidence summary.

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

#### Scenario: Preserve primary-expression parsing semantics after `SQLExprParser.primary()` split
- **WHEN** `SQLExprParser.primary()` is decomposed into smaller helper methods
- **THEN** parsing the same expression input SHALL produce equivalent AST semantics as before decomposition
- **AND** helper extraction SHALL NOT change expression acceptance or rejection behavior

#### Scenario: Preserve token advancement in optional branches within primary-expression paths
- **WHEN** optional syntax fragments in `primary()`-handled paths are present or absent
- **THEN** token advancement order and branch selection SHALL remain equivalent to pre-refactor behavior
- **AND** no branch SHALL consume additional tokens compared to baseline behavior

#### Scenario: Preserve parser error locality for malformed primary expressions
- **WHEN** malformed inputs fail in branches previously handled directly by `SQLExprParser.primary()`
- **THEN** parser exceptions SHALL retain meaningful token/location context comparable to baseline
- **AND** refactoring SHALL NOT generalize away branch-specific diagnostics that were previously available

#### Scenario: Preserve visitor dispatch compatibility after `SQLASTVisitor` interface optimization
- **WHEN** `SQLASTVisitor` interface methods are reorganized or simplified in touched paths
- **THEN** equivalent AST traversal inputs SHALL produce behavior-equivalent visit dispatch and output semantics
- **AND** existing visitor implementations used by parser/output flows SHALL remain compatibility-equivalent for supported paths

#### Scenario: Preserve lexer behavior after scan-mode deduplication
- **WHEN** duplicated lexer scan-mode branches are consolidated into shared helper paths
- **THEN** token category and token advancement sequence SHALL remain behavior-equivalent to baseline
- **AND** malformed input diagnostics SHALL retain comparable token/location context

#### Scenario: Capture structured baseline and post-refactor evidence
- **WHEN** parser-internal behavior-preserving refactors are validated
- **THEN** verification records SHALL include a defined baseline and post-change command set for touched parser paths
- **AND** records SHALL summarize pass/fail equivalence and relevant performance or memory observations
