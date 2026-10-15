## Verification Notes

### Scope inventory
- Bug surface: ODPS / Hive-style `GROUP BY` clause combined with a trailing
  `GROUPING SETS(...)` operator. Two distinct surface forms exist:
  - `GROUP BY a, GROUPING SETS((a, b), (a), (b), ())` — comma separator
  - `GROUP BY a GROUPING SETS((a, b), (a), (b), ())` — no comma
- Pre-fix observation: parser collapses both into the same AST, output
  visitor unconditionally drops the comma. Roundtrip therefore mutates SQL.

### Reproduction
```java
String sql = "SELECT a, b, COUNT(*) FROM data_sec.yidie_test "
        + "GROUP BY a, GROUPING SETS((a, b), (a), (b), ())";
SQLStatementParser parser = new SQLStatementParser(sql, DbType.odps);
System.out.println(SQLUtils.toOdpsString(parser.parseSelect()));
```
Pre-fix actual output:
```
SELECT a, b, COUNT(*)
FROM data_sec.yidie_test
GROUP BY a
	GROUPING SETS ((a, b), (a), (b), ())
```
Post-fix actual output for the same input:
```
SELECT a, b, COUNT(*)
FROM data_sec.yidie_test
GROUP BY a,
	GROUPING SETS ((a, b), (a), (b), ())
```

### Core implementation summary
- `SQLGroupingSetExpr` gains `boolean hasPrefixComma = true` plus
  `isHasPrefixComma()` / `setHasPrefixComma(boolean)`. Field flows through
  `clone()`, `equals(Object)`, `hashCode()`.
- `SQLSelectParser.parseGroupBy` tracks `previousWasComma`. When a parsed
  item is a `SQLGroupingSetExpr` and the previous iteration did not
  consume a comma, the parser sets `hasPrefixComma = false`.
- `SQLASTOutputVisitor.visit(SQLSelectGroupByClause)` reads
  `hasPrefixComma` for non-first `SQLGroupingSetExpr` siblings and emits
  a comma when the flag is true; otherwise keeps the historical
  newline-only behavior.

### Compatibility principles maintained
- Default `hasPrefixComma = true` keeps the historically dominant emit
  shape (`GROUP BY x, GROUPING SETS(...)`) for AST builders that never
  set the flag.
- No parser grammar change beyond annotating the AST.
- No token-advancement behavior change.
- No parse acceptance/rejection boundary change.

### Regression coverage
- New test class:
  `core/src/test/java/com/alibaba/druid/bvt/sql/odps/OdpsGroupingSetsCommaTest.java`
  — 3 scenarios: comma preserved, no-comma preserved, GROUPING SETS as
  only item.

### Focused regression sweep (run on this branch)
- Command:
  `mvn -pl core test -Dtest='GroupingSetsTest,OracleGroupingSetTest,OracleGroupingSetsTest,MySqlSelectTest_286,MySqlSelectTest_294_dla,MySqlSelectTest_296,OracleSelectTest62,OracleSelectTest121,HiveSelectTest_42_cte,OracleFlashbackQueryTest3'`
- Result: 11 / 11 classes pass, 0 failures, 0 errors.

### Resource-fixture regression
- Command:
  `mvn -pl core test -Dtest='OdpsResourceTest,OracleResourceTest'`
- Result:
  - `com.alibaba.druid.bvt.sql.odps.OdpsResourceTest`: 15 tests pass (includes `odps-14.txt`, which exercises the no-comma + comments form).
  - `com.alibaba.druid.bvt.sql.oracle.visitor.OracleResourceTest`: 12 tests pass.
  - `com.alibaba.druid.sql.oracle.demo.OracleResourceTest.test_0` errors with `FileNotFound /Users/wenshao/Downloads/unknownSql(2).txt`. Pre-existing on master, unrelated to this change.

### Conclusion
- Bug confirmed pre-fix via failing `OdpsGroupingSetsCommaTest.test_groupBy_comma_groupingSets`.
- All three new test scenarios pass post-fix.
- No regression in 11 GROUPING-SETS-touching test classes nor in `OdpsResourceTest` / `OracleResourceTest`-visitor.
- Sole observed failure (`OracleResourceTest.test_0` in `demo` package) is a developer-local-path issue unrelated to this fix.
