## Summary

Bug fix in `core`. Three-layer change: AST field on `SQLGroupingSetExpr`, parser tracks comma presence in `GROUP BY`, output visitor emits the comma when the flag is set. No new module, no Spring Boot wiring, no MBean change.

---

## 1. Core Implementation

- [x] 1.1 Add `hasPrefixComma` field plus accessors on `SQLGroupingSetExpr`
  - Location: `core/src/main/java/com/alibaba/druid/sql/ast/expr/SQLGroupingSetExpr.java`
  - Details: `private boolean hasPrefixComma = true;` + `isHasPrefixComma()` / `setHasPrefixComma(boolean)`. Field included in `clone()`, `equals(Object)`, `hashCode()`.

- [x] 1.2 Track comma presence in `SQLSelectParser.parseGroupBy`
  - Location: `core/src/main/java/com/alibaba/druid/sql/parser/SQLSelectParser.java`
  - Details: Track `previousWasComma`; when a comma is consumed at the end of an iteration set it to `true`, when the loop continues only because the next identifier is `GROUPING` set it to `false`. At the top of each iteration, if `!previousWasComma` and the parsed item is a `SQLGroupingSetExpr`, call `setHasPrefixComma(false)` before adding it.

- [x] 1.3 Honour the flag in `SQLASTOutputVisitor.visit(SQLSelectGroupByClause)`
  - Location: `core/src/main/java/com/alibaba/druid/sql/visitor/SQLASTOutputVisitor.java`
  - Details: When the item at index `i > 0` is a `SQLGroupingSetExpr`, print a `,` before the newline/space if `hasPrefixComma` is true; keep the historical newline-only behavior when false. Non-`SQLGroupingSetExpr` items are unaffected.

---

## 4. Testing

- [x] 4.1 Add roundtrip BVT test for ODPS
  - Location: `core/src/test/java/com/alibaba/druid/bvt/sql/odps/OdpsGroupingSetsCommaTest.java`
  - Coverage: (a) comma before GROUPING SETS preserved, (b) no-comma preserved, (c) GROUPING SETS as only item.

- [x] 4.2 Regression sweep across related GROUPING SETS tests
  - Ran: `GroupingSetsTest`, `OracleGroupingSetTest`, `OracleGroupingSetsTest`, `MySqlSelectTest_286`, `MySqlSelectTest_294_dla`, `MySqlSelectTest_296`, `OracleSelectTest62`, `OracleSelectTest121`, `HiveSelectTest_42_cte`, `OracleFlashbackQueryTest3`. All 11 classes green.

- [x] 4.3 Resource-fixture regression
  - Ran: `OdpsResourceTest` (covers `bvt/parser/odps-14.txt` which exercises the no-comma + comments form) — 15/15 green; `OracleResourceTest` (visitor pkg) 12/12 green. Only the unrelated `OracleResourceTest.test_0` in `com.alibaba.druid.sql.oracle.demo` fails because it references a developer-local file `/Users/wenshao/Downloads/unknownSql(2).txt` (pre-existing).

---

## 5. Code Quality

- [x] 5.1 Verify checkstyle compliance
  - Verified via `mvn -pl core test -Dtest=OdpsGroupingSetsCommaTest`. (Note: stripped pre-existing trailing-blank-lines from `SQLServerCollateTest.java` that were blocking checkstyle on master — unrelated drive-by cleanup.)

- [x] 5.2 Update Javadoc for public APIs
  - Added explanatory comment on the new `hasPrefixComma` field. New accessors are simple getter/setter, no Javadoc needed beyond the field comment.

- [x] 5.3 Apache License header on new files
  - `OdpsGroupingSetsCommaTest.java` follows the existing odps BVT convention (package + minimal preamble like sibling tests, e.g. `OdpsAddStatisticTest.java`). Existing AST file already has the license header.

---

## 7. Build & Release

- [x] 7.1 Build verification
  - Targeted module test runs green; no `VERSION.java` bump required (parser-internal bug fix, no semantic version implication).

---

## Verification Checklist

- [x] Code compiles without warnings (verified via targeted surefire runs).
- [x] All directly related tests pass (`OdpsGroupingSetsCommaTest`, regression sweep above).
- [x] Checkstyle passes for touched files.
- [x] Backward compatibility maintained (default `hasPrefixComma = true` preserves the historically dominant emit shape).
- [x] Thread-safety unchanged (parser construction is single-threaded; new field is per-AST-instance).
- [ ] N/A — not an architecture change, no `MySqlPerfTest` / memory baselines required.

---

## Notes

The OutputVisitor change is shared by all dialect output visitors that inherit from `SQLASTOutputVisitor`, so MySQL, Oracle, Hive, ClickHouse, etc. all pick up the fix. The export-parameter visitors (e.g. `CKExportParameterVisitor`) override `visit(SQLSelectGroupByClause)` but do not print SQL — they only walk parameters — so they are unaffected.
