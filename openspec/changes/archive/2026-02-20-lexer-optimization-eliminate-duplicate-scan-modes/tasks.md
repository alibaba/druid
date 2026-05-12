## 1. Baseline and Scope

- [x] 1.1 Inventory duplicated lexer scan-mode branches and map affected parser paths
- [x] 1.2 Run baseline lexer/parser focused regression tests and record behavior-equivalence references
- [x] 1.3 Run baseline `MySqlPerfTest` and memory test for post-change comparison notes

## 2. Core Lexer Refactoring

- [x] 2.1 Refactor duplicated scan-mode branches into shared helper paths with compatibility-first defaults
- [x] 2.2 Update affected lexer call sites and internal branch dispatch to align with deduplicated flow
- [x] 2.3 Ensure token classification, token advancement order, and diagnostics locality remain behavior-equivalent

## 3. Regression Coverage

- [x] 3.1 Add/extend unit tests for lexer scan-mode deduplication compatibility in touched parser flows
- [x] 3.2 Add malformed/edge regression checks to verify stable token context and error diagnostics
- [x] 3.3 Run focused lexer/parser regression suites and verify no functional behavior regressions

## 4. Quality Gates and Validation

- [x] 4.1 Run post-change `MySqlPerfTest` and memory test, compare with baseline, and document findings
- [x] 4.2 Run affected module tests (`mvn -pl core test`) and fix any regressions
- [x] 4.3 Run style gate (`mvn -pl core checkstyle:check` or equivalent) and resolve violations
- [x] 4.4 Capture structured baseline/post evidence and finalize verification notes
