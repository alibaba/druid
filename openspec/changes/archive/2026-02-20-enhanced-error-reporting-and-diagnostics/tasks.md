## 1. Baseline and Scope

- [x] 1.1 Inventory inconsistent error-reporting paths in touched lexer/parser branches
- [x] 1.2 Run baseline malformed-input focused regression suite and capture diagnostic references
- [x] 1.3 Run baseline `MySqlPerfTest` and memory test for post-change comparison notes

## 2. Core Diagnostic Improvements

- [x] 2.1 Refine diagnostics in targeted lexer/parser paths with compatibility-first behavior
- [x] 2.2 Ensure token/location context is consistently included for equivalent malformed-input classes
- [x] 2.3 Preserve parse acceptance/rejection and token advancement behavior in touched paths

## 3. Regression Coverage

- [x] 3.1 Add/extend tests for enhanced diagnostics in touched malformed-input branches
- [x] 3.2 Add edge-case regression checks to validate stable diagnostic context patterns
- [x] 3.3 Run focused lexer/parser regression suites and verify no functional behavior regressions

## 4. Quality Gates and Validation

- [x] 4.1 Run post-change `MySqlPerfTest` and memory test, compare with baseline, and document findings
- [x] 4.2 Run affected module tests (`mvn -pl core test`) and fix any regressions
- [x] 4.3 Run style gate (`mvn -pl core checkstyle:check` or equivalent) and resolve violations
- [x] 4.4 Capture structured baseline/post evidence and finalize verification notes
