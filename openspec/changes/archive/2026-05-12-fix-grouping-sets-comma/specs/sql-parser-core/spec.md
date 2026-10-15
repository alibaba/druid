## Capability Overview

Extend `sql-parser-core` so that the comma separator between a `GROUP BY` ordinary item list and a trailing `GROUPING SETS` operator is preserved through parse + emit. The two surface forms `GROUP BY a, GROUPING SETS(...)` and `GROUP BY a GROUPING SETS(...)` MUST roundtrip without mutation.

## ADDED Requirements

### Requirement: GROUP BY GROUPING SETS Separator Preservation

The parser SHALL record whether a `SQLGroupingSetExpr` sibling in a `GROUP BY` clause was preceded by a comma in the source SQL, and the SQL output visitor SHALL emit a comma before the GROUPING SETS keyword if and only if that flag is set. The flag SHALL default to `true` for programmatically constructed `SQLGroupingSetExpr` nodes so that pre-existing AST builders continue to emit the comma form.

#### Scenario: Parse and emit preserve a comma before GROUPING SETS
- **GIVEN** the input SQL `GROUP BY a, GROUPING SETS((a, b), (a), (b), ())` for any dialect that supports GROUPING SETS (ODPS in particular)
- **WHEN** the SQL is parsed and then serialized back via the standard `SQLASTOutputVisitor` (or a dialect output visitor that inherits from it)
- **THEN** the resulting `SQLSelectGroupByClause` SHALL contain a `SQLGroupingSetExpr` item whose `hasPrefixComma` flag is `true`
- **AND** the emitted SQL SHALL contain a `,` immediately before `GROUPING SETS`, equivalent to the source SQL separator

#### Scenario: Parse and emit preserve the absence of a comma before GROUPING SETS
- **GIVEN** the input SQL `GROUP BY a GROUPING SETS((a, b), (a), (b), ())` for any dialect that supports GROUPING SETS
- **WHEN** the SQL is parsed and then serialized back via the standard SQL output visitor
- **THEN** the resulting `SQLSelectGroupByClause` SHALL contain a `SQLGroupingSetExpr` item whose `hasPrefixComma` flag is `false`
- **AND** the emitted SQL SHALL NOT contain a comma immediately before `GROUPING SETS`

#### Scenario: GROUPING SETS as the only GROUP BY item emits no leading comma
- **GIVEN** the input SQL `GROUP BY GROUPING SETS((a, b), (a), ())`
- **WHEN** the SQL is parsed and then serialized back
- **THEN** the resulting clause SHALL contain a single `SQLGroupingSetExpr` item
- **AND** the emitted SQL SHALL be `GROUP BY GROUPING SETS ((a, b), (a), ())` with no leading comma, regardless of the value of `hasPrefixComma` on that first item

#### Scenario: Programmatic AST construction defaults to comma-form
- **GIVEN** application code that constructs a `SQLSelectGroupByClause` by appending an ordinary expression item and then a freshly instantiated `SQLGroupingSetExpr` without setting `hasPrefixComma`
- **WHEN** the resulting AST is serialized via the standard SQL output visitor
- **THEN** the emitted SQL SHALL be `GROUP BY x, GROUPING SETS(...)` (comma present)
- **AND** the default behavior SHALL match the historically dominant emit shape used by pre-existing AST consumers

#### Scenario: AST equality reflects the separator difference
- **GIVEN** two `SQLGroupingSetExpr` instances with identical `parameters` lists
- **WHEN** one has `hasPrefixComma = true` and the other has `hasPrefixComma = false`
- **THEN** `equals` SHALL return `false`
- **AND** `hashCode` values SHALL differ, so AST diff tools can identify the two surface forms as distinct

#### Scenario: Clone preserves the separator flag
- **GIVEN** a `SQLGroupingSetExpr` whose `hasPrefixComma` flag has been set explicitly
- **WHEN** `clone()` is invoked
- **THEN** the cloned instance SHALL carry the same `hasPrefixComma` value as the source
