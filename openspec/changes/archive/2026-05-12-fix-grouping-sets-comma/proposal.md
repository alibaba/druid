## Why

For ODPS/Hive-style SQL, `GROUP BY a, GROUPING SETS(...)` and `GROUP BY a GROUPING SETS(...)` (no comma) are two distinct surface forms. The current parser collapses both into the same AST, and the output visitor unconditionally emits the no-comma form, so a parse + emit roundtrip silently mutates the SQL. Downstream tooling that re-runs the formatted SQL has been observed to break on this.

Reproduction:
```java
String sql = "SELECT a, b, COUNT(*) FROM data_sec.yidie_test "
        + "GROUP BY a, GROUPING SETS((a, b), (a), (b), ())";
SQLStatementParser parser = new SQLStatementParser(sql, DbType.odps);
System.out.println(SQLUtils.toOdpsString(parser.parseSelect()));
```
Actual output:
```
SELECT a, b, COUNT(*)
FROM data_sec.yidie_test
GROUP BY a
	GROUPING SETS ((a, b), (a), (b), ())
```
The comma after `a` is dropped even though the source SQL had one.

## Change Type

- [ ] Feature (new functionality)
- [ ] Enhancement (improvement to existing functionality)
- [x] Bug Fix
- [ ] Refactoring
- [ ] Documentation
- [ ] Performance

## Affected Modules

- [x] `core` - Main Druid library
- [ ] `druid-spring-boot-starter`
- [ ] `druid-spring-boot-3-starter`
- [ ] `druid-spring-boot-4-starter`
- [ ] `druid-wrapper`
- [ ] `druid-demo-petclinic`

## What Changes

Three coordinated changes inside `core`:

1. **AST** — `SQLGroupingSetExpr` gains a `boolean hasPrefixComma` field (default `true`). The field participates in `clone()`, `equals(Object)`, and `hashCode()`.
2. **Parser** — `SQLSelectParser.parseGroupBy` tracks whether the previous loop iteration consumed a comma. When the next-parsed item is a `SQLGroupingSetExpr` and no comma was consumed, the parser sets `hasPrefixComma=false` on it.
3. **Output visitor** — `SQLASTOutputVisitor.visit(SQLSelectGroupByClause)` emits a `,` before a non-first `SQLGroupingSetExpr` sibling iff its `hasPrefixComma` flag is `true`; otherwise keeps the current newline-only behavior. This path is shared by all dialect output visitors that inherit from the base.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `sql-parser-core`: adds **Requirement: GROUP BY GROUPING SETS Separator Preservation** with six scenarios covering roundtrip, default construction, equality, and clone.

## API Changes

### New Public APIs
- `com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr#isHasPrefixComma()`
- `com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr#setHasPrefixComma(boolean)`

### Changed Public APIs
- `SQLGroupingSetExpr#equals(Object)` and `#hashCode()` now include the `hasPrefixComma` field.
- `SQLGroupingSetExpr#clone()` propagates the field.

### Deprecated/Removed APIs
<!-- None -->

## Impact

### Backward Compatibility

- [x] This change is backward compatible
- [ ] This change breaks backward compatibility

Default `hasPrefixComma = true` preserves the dominant pre-existing emit shape (`GROUP BY x, GROUPING SETS(...)`) for any code that constructs a `SQLGroupingSetExpr` without setting the flag. The previously incorrect *parse-roundtrip* output for source SQL containing `GROUP BY ..., GROUPING SETS(...)` now correctly retains the comma — that is the intended fix, not a regression.

### Performance Impact

Negligible. One additional boolean field per `SQLGroupingSetExpr` instance and one additional boolean check per emission of a `GROUP BY` group-by-item loop iteration.

### Dependencies

None.

## Related Issues

<!-- None reported externally; reproduction provided by user. -->
