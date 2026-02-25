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

### Requirement: Parser Error Locality

Parser errors SHALL remain diagnosable with meaningful token and location context, and diagnostics in touched lexer/parser paths SHALL use a consistent context pattern for equivalent malformed-input classes.

#### Scenario: Invalid SQL input
- **WHEN** malformed SQL cannot be parsed
- **THEN** parser exception messages SHALL include token-related context
- **AND** error reporting SHALL not silently swallow parser failures

#### Scenario: Keep diagnostic context pattern stable for equivalent malformed inputs
- **WHEN** equivalent malformed inputs fail through touched lexer/parser branches
- **THEN** exceptions SHALL contain comparable location context (line/column or equivalent position metadata)
- **AND** diagnostics SHALL include sufficient token or branch context to identify the failing parse point

#### Scenario: Preserve behavior while improving diagnostic clarity
- **WHEN** diagnostics are refined in behavior-preserving parser/lexer refactors
- **THEN** parse acceptance and rejection boundaries SHALL remain equivalent to baseline
- **AND** diagnostic improvements SHALL NOT consume extra tokens compared to baseline failure paths

### Requirement: Inheritance Hierarchy Consistency in AST Traversal
Parser/visitor refactors touching SQL AST inheritance layers SHALL keep equivalent node families on consistent base-class contracts so that traversal and dispatch behavior remain predictable and behavior-preserving. Refactors SHALL also define explicit extension boundaries between base implementations and dialect-specific overrides to prevent duplicated override paths and hidden behavior divergence.

#### Scenario: Preserve traversal compatibility while consolidating inherited defaults
- **WHEN** shared behavior is moved from duplicated subclass branches into a common parent-level contract
- **THEN** equivalent SQL inputs SHALL produce behavior-equivalent traversal/dispatch outcomes versus baseline
- **AND** no new acceptance or rejection behavior SHALL be introduced by hierarchy consolidation alone

#### Scenario: Preserve token advancement behavior in inheritance-sensitive parse branches
- **WHEN** parser branches that instantiate related AST subclasses are refactored for hierarchy consistency
- **THEN** token advancement order and branch selection SHALL remain equivalent to baseline behavior
- **AND** refactoring SHALL NOT consume additional tokens in failure or success paths

#### Scenario: Keep diagnostics context stable for hierarchy-related malformed inputs
- **WHEN** malformed SQL fails in parser paths that are affected by inheritance-hierarchy refactoring
- **THEN** exceptions SHALL retain meaningful token/location context comparable to baseline
- **AND** diagnostics SHALL remain sufficient to identify the failing parse branch

#### Scenario: Keep base and dialect extension contracts deterministic
- **WHEN** a dialect parser/visitor specializes behavior from shared base classes
- **THEN** extension points SHALL be invoked in a deterministic and documented order for equivalent inputs
- **AND** specialization SHALL NOT require duplicating unchanged base behavior branches

#### Scenario: Preserve compatibility for existing visitor entry points
- **WHEN** visitor inheritance cleanup introduces support or adapter classes for internal dispatch
- **THEN** externally used visitor entry paths SHALL remain behavior-equivalent for supported AST inputs
- **AND** existing integration tests for output and traversal compatibility SHALL continue to pass
