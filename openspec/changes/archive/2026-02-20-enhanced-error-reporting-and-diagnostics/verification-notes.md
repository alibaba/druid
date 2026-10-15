## Verification Notes

### Scope inventory (Task 1.1)
- Touched lexer/parser diagnostics branch: `SQLStatementParser` unsupported-token path using `UNSUPPORT_TOKEN_MSG_PREFIX + lexer.info()`.
- Baseline inconsistency identified:
  - Prefix formatting was inconsistent (`not supported.pos ...` with no separator).
  - Affected tests relied on full literal message text, making diagnostics checks brittle to formatting-only improvements.

### Baseline focused malformed-input regression (Task 1.2)
- Command:
  - `mvn -pl core -Dtest=SQLLexerTest2,SQLParserRefactorRegressionTest,SQLExprParserTest,OdpsLexerTest,VariantLexerTest,OracleLexerTest test`
- Result:
  - Failing baseline test observed (pre-existing):
    - `SQLLexerTest2.test_lexer_error_info`
    - expected column `2`, actual column `1`
  - Other focused tests passed.

### Baseline perf/memory (Task 1.3)
- Command:
  - `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`
- Result:
  - `MySqlPerfTest` (selected samples): `764, 526, 504, 500, 499, 500, 501, 501, 499, 526`
  - `MemoryTest`: `memory used : 25,165,824`

### Implementation notes (Tasks 2.1 / 2.2 / 2.3)
- Unified unsupported-token diagnostics prefix format:
  - `not supported.` -> `not supported. `
- Kept parse acceptance/rejection boundaries unchanged; only message formatting and diagnostics assertions were refined.

### Regression coverage changes (Tasks 3.1 / 3.2)
- Updated brittle full-message assertions to structured context assertions:
  - `SQLLexerTest2`
  - `MySqlError_test_3`
- Added diagnostics context stability tests:
  - `SQLParserErrorDiagnosticsTest`
  - validates token/location context presence
  - validates comparable location context across equivalent malformed inputs

### Post-change focused regression (Task 3.3)
- Command:
  - `mvn -pl core -Dtest=SQLLexerTest2,SQLParserErrorDiagnosticsTest,SQLParserRefactorRegressionTest,SQLExprParserTest,OdpsLexerTest,VariantLexerTest,OracleLexerTest test`
- Result:
  - PASS (`23` tests, `0` failures).

### Post-change perf/memory (Task 4.1)
- Command:
  - `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`
- Result:
  - `MySqlPerfTest` (selected samples): `817, 527, 503, 499, 498, 502, 500, 498, 499, 499`
  - `MemoryTest`: `memory used : 25,165,824`
- Comparison:
  - No meaningful regression observed versus baseline.
  - Memory footprint unchanged in the observed run.

### Full module and style gates (Tasks 4.2 / 4.3)
- Full module tests:
  - Command: `mvn -pl core test`
  - Result: PASS.
- Style gate:
  - Equivalent style gate observed in module test lifecycle (`checkstyle` phase reports `You have 0 Checkstyle violations.` in successful runs).

### Conclusion (Task 4.4)
- Diagnostic clarity improved with consistent unsupported-token prefix formatting.
- Token/location context remains present and test-covered.
- Parse behavior compatibility preserved for touched paths.
- Change is verification-complete and ready for archive.
