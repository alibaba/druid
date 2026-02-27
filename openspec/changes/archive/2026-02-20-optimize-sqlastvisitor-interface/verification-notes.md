# Verification Notes: optimize-sqlastvisitor-interface

## 1) Baseline and scope inventory

### 1.1 `SQLASTVisitor` complexity inventory

- `core/src/main/java/com/alibaba/druid/sql/visitor/SQLASTVisitor.java`
  - `default boolean visit(...)` methods: **389**
  - `default void endVisit(...)` methods: **388**
- Primary affected touchpoint: table-source visitor branches had repeated default behavior (`return true` / no-op).
- Representative impacted visitor stacks mapped during inventory:
  - Core: `SQLASTOutputVisitor`, `SchemaStatVisitor`, `SQLASTVisitorAdapter`
  - Dialect adapters/output visitors: MySQL/Oracle/PostgreSQL/SQLServer/Hive/Odps/DB2/StarRocks and others under `core/src/main/java/com/alibaba/druid/sql/dialect/**/visitor/`

### 1.2 Baseline focused regression (visitor/parser/output)

Command:

`mvn -pl core -Dtest=SQLASTOutputVisitorSplitRefactorTest,PGASTVisitorAdapterTest,OracleASTVisitorAdapterTest,SQLParserUtilsDialectDispatchTest test`

Result:

- BUILD SUCCESS
- Tests run: **9**
- Failures: **0**
- Errors: **0**
- Skipped: **0**

### 1.3 Baseline performance/memory

Command:

`mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`

Result:

- BUILD SUCCESS
- `MySqlPerfTest` samples: `751, 522, 514, 511, 512, 531, 568, 524, 517, 518`
- Baseline average: **546.8**
- `MemoryTest`: **25,165,824**

## 2) Implementation summary

- Added compatibility-first default touchpoints in `SQLASTVisitor`:
  - `visitTableSource(SQLTableSource x)`
  - `endVisitTableSource(SQLTableSource x)`
- Delegated representative table-source visit/endVisit methods to those defaults:
  - `SQLTableSourceImpl`, `SQLExprTableSource`, `SQLJoinTableSource`, `SQLSubqueryTableSource`,
    `SQLUnionQueryTableSource`, `SQLLateralViewTableSource`, `SQLValuesTableSource`,
    `SQLAdhocTableSource`, `SQLUnnestTableSource`, `SQLGeneratedTableSource`
- Added regression test:
  - `core/src/test/java/com/alibaba/druid/bvt/sql/SQLASTVisitorInterfaceOptimizationTest.java`
  - Covers table-source delegation and specific-override compatibility.

## 3) Post-change verification

### 3.1 Focused visitor/parser/output regression

Command:

`mvn -pl core -Dtest=SQLASTVisitorInterfaceOptimizationTest,SQLASTOutputVisitorSplitRefactorTest,PGASTVisitorAdapterTest,OracleASTVisitorAdapterTest,SQLParserUtilsDialectDispatchTest test`

Result:

- BUILD SUCCESS
- Tests run: **11**
- Failures: **0**
- Errors: **0**
- Skipped: **0**

### 3.2 Post performance/memory

Command:

`mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`

Result:

- BUILD SUCCESS
- `MySqlPerfTest` samples: `762, 515, 508, 527, 545, 511, 509, 504, 509, 504`
- Post average: **539.4**
- Delta vs baseline: **-1.35%**
- `MemoryTest`: **25,165,824** (no change)

### 3.3 Full module test gate

Command:

`mvn -pl core test`

Result:

- BUILD SUCCESS

### 3.4 Style gate

Command:

`mvn -pl core checkstyle:check`

Result:

- Command reported failure due large pre-existing repository-wide violations (legacy baseline), including many entries in `SQLASTVisitor.java` unrelated to this refactor session.
- In contrast, `mvn -pl core test` path reports `You have 0 Checkstyle violations.` for this change workflow.

## 4) Behavior-equivalence conclusion

- Visitor dispatch semantics in touched table-source paths remain compatibility-equivalent for covered flows.
- Focused and full test lanes passed.
- No memory regression observed; performance variation remained within a small range for this microbenchmark run.
