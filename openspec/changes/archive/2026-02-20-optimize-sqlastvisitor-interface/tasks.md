## 1. Baseline and Scope

- [x] 1.1 Inventory current `SQLASTVisitor` interface complexity points and map affected core/dialect visitor implementations
- [x] 1.2 Run baseline visitor-related parser/output regression tests and record behavior-equivalence references
- [x] 1.3 Run baseline `MySqlPerfTest` and memory test for post-change comparison notes

## 2. Core Interface Optimization

- [x] 2.1 Refactor `SQLASTVisitor` interface touchpoints to improve readability and maintainability with compatibility-first defaults
- [x] 2.2 Update impacted visitor implementations to align with optimized interface contracts without changing observable behavior
- [x] 2.3 Ensure dispatch order and traversal semantics remain behavior-equivalent in affected paths

## 3. Regression Coverage

- [x] 3.1 Add/extend unit tests covering visitor dispatch compatibility in touched parser/output flows
- [x] 3.2 Add malformed/edge regression checks to verify stable diagnostics and traversal behavior where applicable
- [x] 3.3 Run focused visitor/parser regression suites and verify no functional behavior regressions

## 4. Quality Gates and Validation

- [x] 4.1 Run post-change `MySqlPerfTest` and memory test, compare with baseline, and document findings
- [x] 4.2 Run affected module tests (`mvn -pl core test`) and fix any regressions
- [x] 4.3 Run style gate (`mvn -pl core checkstyle:check` or equivalent) and resolve violations
- [x] 4.4 Capture structured baseline/post evidence and finalize verification notes
