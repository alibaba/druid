## 1. Baseline and Scope Confirmation

- [x] 1.1 Identify giant methods in `core/src/main/java/com/alibaba/druid/sql/parser/SQLStatementParser.java` and define phase-1 split scope
  - Phase-1 scope: `parseStatementList(...)` non-standard statement dispatch branch (CALL/UPSERT/RENAME/transaction/control statements) extracted into helper.
  - Identified giant methods for later phases: `parseWith()`, `parseStatementList(...)`.
- [x] 1.2 Run baseline parser benchmark (`MySqlPerfTest`) before refactoring and record results
  - Baseline command: `./mvnw -pl core -Dtest=MySqlPerfTest test`
  - Baseline sample runs (warm): `506, 516, 518, 529, 519, 523, 528, 557, 553` ms (first run `798` ms).
- [x] 1.3 Run baseline memory test for parser paths and record results
  - Baseline command: `./mvnw -pl core -Dtest=MemoryTest test`
  - Baseline memory: `26,817,024`.

## 2. Core Refactor Implementation

- [x] 2.1 Extract grammar-boundary helper methods from selected giant `SQLStatementParser` methods
  - Extracted `parseStatementListNonStandard(...)` from `parseStatementList(...)` to isolate grammar/control fallback parsing.
- [x] 2.2 Keep token advancement order and branch sequencing behavior-equivalent during extraction
  - Preserved original branch ordering and token operations; only moved logic into helper and kept call sequence in loop.
- [x] 2.3 Preserve parser error locality and exception context in touched branches
  - Preserved existing `ParserException("TODO " + lexer.info())` path for malformed parenthesized non-select statements.
- [x] 2.4 Ensure no public API/signature changes for parser entry points
  - No public parser method signatures were changed.

## 3. Regression and Characterization Tests

- [x] 3.1 Add/update unit tests for representative SQL branches affected by decomposition
  - Added `SQLStatementParserSplitRefactorTest` covering non-standard keyword dispatch and parenthesized select path.
- [x] 3.2 Add regression tests validating AST/statement equivalence before vs after refactor
  - Added branch regression assertions on statement type/structure for `COMMIT`, `ROLLBACK`, `RESET`, and parenthesized `SELECT`.
- [x] 3.3 Add tests for optional-clause present/absent branch behavior in decomposed paths
  - Added `OPTIMIZE TABLE ... DEDUPLICATE BY ...` vs `OPTIMIZE TABLE ...` assertions.
- [x] 3.4 Add malformed-SQL tests to verify parser error token/location diagnostics remain equivalent
  - Added malformed parenthesized statement test asserting error includes `TODO` and `pos`.

## 4. Verification and Quality Gates

- [x] 4.1 Re-run `MySqlPerfTest` after refactor and compare with baseline
  - Post command: `./mvnw -pl core -Dtest=MySqlPerfTest test`
  - Post sample runs (warm): `515, 508, 514, 518, 507, 509, 503, 515, 508` ms (first run `718` ms).
- [x] 4.2 Re-run memory tests after refactor and compare with baseline
  - Post command: `./mvnw -pl core -Dtest=MemoryTest test`
  - Post memory: `27,043,328`.
- [x] 4.3 Document perf/memory deltas and pass/fail conclusion for parser hot paths
  - Warm-run performance remained in the same band as baseline (no material regression).
  - Memory changed from `26,817,024` to `27,043,328` (small fluctuation, no material regression signal).
  - Conclusion: pass.
- [x] 4.4 Run checkstyle verification and fix violations (`mvn checkstyle:check`)
  - Verified with `./mvnw -pl core checkstyle:check@checkstyle` (0 violations).
- [x] 4.5 Run parser-focused test suite and confirm all pass
  - Verified with `./mvnw -pl core -Dtest=SQLStatementParserSplitRefactorTest,SQLParserUtilsDialectRegistryTest,SQLParserUtilsTest,SnowflakeParserTest test` (136 tests, 0 failures).
