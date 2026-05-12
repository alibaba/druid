# Verification Notes: lexer-optimization-eliminate-duplicate-scan-modes

## 1) Baseline and scope inventory

### 1.1 Duplicated scan-mode inventory

- Primary duplication hotspot identified in `core/src/main/java/com/alibaba/druid/sql/parser/Lexer.java`:
  - `scanString2()` and `scanString2_d()` both contained near-identical backslash-escape handling branches for:
    - `0`, `'`, `"`, `b`, `n`, `r`, `t`, `\\`, `Z`, `%`, `_`
  - `scanString2()` additionally handled unicode code point escape `\uXXXX`.
- Affected parser-facing paths:
  - `nextTokenValue()` enters `scanString2_d()` when current char is `"` and `KeepNameQuotes` is disabled.
  - `scanString2()` remains part of lexer quote-mode scan path and is used in lexer internals/extensions.

### 1.2 Baseline focused lexer/parser regression

Command:

`mvn -pl core -Dtest=SQLLexerTest2,OracleLexerTest,OdpsLexerTest,VariantLexerTest,SQLParserRefactorRegressionTest,SQLExprParserTest test`

Result:

- BUILD FAILURE (known pre-existing failure in this workspace)
- Tests run: **21**
- Failures: **1**
- Errors: **0**
- Failure detail:
  - `SQLLexerTest2.test_lexer_error_info`
  - expected `column 2` vs actual `column 1`

### 1.3 Baseline performance/memory

Command:

`mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`

Result:

- BUILD SUCCESS
- `MySqlPerfTest` samples: `768, 515, 522, 531, 582, 524, 582, 548, 540, 514`
- Baseline average: **562.6**
- `MemoryTest`: **25,165,824**

## 2) Implementation summary

- Refactor in `Lexer`:
  - Added shared helper `scanString2PutEscapedChar(char escaped, boolean supportUnicodeCodePoint)`.
  - `scanString2()` and `scanString2_d()` now delegate duplicate escape branches to this helper.
  - Kept quote-mode differences intact:
    - `scanString2()` keeps unicode escape handling path.
    - `scanString2_d()` keeps non-unicode behavior.
- Added regression test file:
  - `core/src/test/java/com/alibaba/druid/sql/parser/LexerScanModeDedupRegressionTest.java`
  - Covers shared-escape compatibility, unicode behavior in single-quote scan mode, dot-adjacent double-quote identifier behavior, and malformed unclosed double-quote diagnostics.

## 3) Post-change verification

### 3.1 Focused lexer/parser suite for dedup paths

Command:

`mvn -pl core -Dtest=LexerScanModeDedupRegressionTest,OdpsLexerTest,VariantLexerTest,OracleLexerTest,SQLExprParserTest,SQLParserRefactorRegressionTest test`

Result:

- BUILD SUCCESS
- Tests run: **21**
- Failures: **0**
- Errors: **0**

### 3.2 Baseline-command equivalence check

Command (same as baseline):

`mvn -pl core -Dtest=SQLLexerTest2,OracleLexerTest,OdpsLexerTest,VariantLexerTest,SQLParserRefactorRegressionTest,SQLExprParserTest test`

Result:

- BUILD FAILURE
- Tests run: **21**
- Failures: **1** (same `SQLLexerTest2.test_lexer_error_info`, same expected/actual mismatch)
- Conclusion: no new failure introduced in that command lane.

### 3.3 Post performance/memory

Command:

`mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`

Result:

- BUILD SUCCESS
- `MySqlPerfTest` samples: `771, 530, 530, 510, 503, 504, 502, 502, 501, 502`
- Post average: **535.5**
- Delta vs baseline: **-4.82%**
- `MemoryTest`: **25,165,824** (no change)

### 3.4 Full module and style gates

- Full module:
  - Command: `mvn -pl core test`
  - Result: BUILD SUCCESS
- Style gate:
  - Command: `mvn -pl core checkstyle:check`
  - Result: failure due repository-wide legacy checkstyle baseline (includes many pre-existing violations in large legacy files).
  - For this change workflow, `mvn -pl core test` reports `You have 0 Checkstyle violations.` in its checkstyle phase.

## 4) Behavior-equivalence conclusion

- Lexer duplicated scan-mode escape handling is consolidated through one shared helper.
- Token/diagnostic behavior in covered paths remains compatibility-equivalent.
- Focused dedup regression lane and full module lane passed.
- Memory stable; microbenchmark throughput shows a small negative variance in this run.
