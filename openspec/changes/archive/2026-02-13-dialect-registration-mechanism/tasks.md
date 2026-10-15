## 1. Baseline & Scope Confirmation

- [x] 1.1 Run baseline parser performance test (`MySqlPerfTest`) and record results before implementation
  - Baseline: warm runs around `509-518ms` (first run `783ms`), command `./mvnw -pl core -Dtest=MySqlPerfTest test`
- [x] 1.2 Run baseline memory test for parser creation/dispatch paths and record results before implementation
  - Baseline: `memory used : 27,067,904`, command `./mvnw -pl core -Dtest=MemoryTest test`
- [x] 1.3 Confirm target integration points in `core/src/main/java/com/alibaba/druid/sql/parser/` for registry-aware dispatch
  - Integration points: `SQLParserUtils#createSQLStatementParser`, `#createExprParser`, `#createLexer`

## 2. Registry API and Core Integration

- [x] 2.1 Add dialect provider registration contract and registry abstraction in `core` parser package
- [x] 2.2 Implement register/replace/unregister/lookup lifecycle with input validation and deterministic semantics
- [x] 2.3 Integrate registry-first resolution into parser creation flow with built-in `DbType` fallback
- [x] 2.4 Ensure backward compatibility for existing parser entry APIs when no provider is registered

## 3. Concurrency and Correctness Tests

- [x] 3.1 Add unit tests for registration lifecycle (register, replace, unregister, lookup)
- [x] 3.2 Add unit tests for invalid inputs (null/blank key, null provider)
- [x] 3.3 Add unit tests for dispatch precedence (registered provider overrides built-in dispatch)
- [x] 3.4 Add concurrency tests covering concurrent registration/unregistration and parse lookups
- [x] 3.5 Add regression tests validating fallback to built-in dispatch after unregister

## 4. Performance, Quality, and Verification

- [x] 4.1 Re-run `MySqlPerfTest` after implementation and compare against baseline
  - Post-change: warm runs around `504-511ms` (first run `774ms`), command `./mvnw -pl core -Dtest=MySqlPerfTest test`
- [x] 4.2 Re-run memory tests after implementation and compare against baseline
  - Post-change: `memory used : 27,067,904`, command `./mvnw -pl core -Dtest=MemoryTest test`
- [x] 4.3 Document performance and memory deltas with pass/fail conclusion for parser hot paths
  - Performance delta (warm runs): from about `509-518ms` to `504-511ms`, no regression observed.
  - Memory delta: unchanged at `27,067,904`, no regression observed.
  - Conclusion: pass.
- [x] 4.4 Run `mvn checkstyle:check` and fix violations
  - Verified with project execution: `./mvnw -pl core checkstyle:check@checkstyle` (`0` violations)
- [x] 4.5 Run relevant parser test suites (`mvn test` scoped to parser modules/tests) and confirm pass
  - Verified command: `./mvnw -pl core -Dtest=SQLParserUtilsDialectRegistryTest,SnowflakeParserTest,SQLParserUtilsTest test` (`137` tests, `0` failures)
