## 1. Baseline and Scope

- [x] 1.1 Inventory inheritance-hierarchy pain points in targeted AST/parser/visitor branches
- [x] 1.2 Capture baseline regression evidence for inheritance-sensitive parsing and traversal paths
- [x] 1.3 Run baseline `MySqlPerfTest` and memory test for post-change comparison

## 2. Core Refactoring

- [x] 2.1 Consolidate duplicated inherited behavior into shared parent-level contracts in targeted branches
- [x] 2.2 Align visitor dispatch defaults for equivalent node families under the updated inheritance contracts
- [x] 2.3 Preserve parse acceptance/rejection and token advancement invariants in touched paths

## 3. Regression Coverage

- [x] 3.1 Add/extend unit tests for inheritance-sensitive parser/visitor traversal behavior
- [x] 3.2 Add edge-case tests for malformed-input diagnostics in hierarchy-related branches
- [x] 3.3 Run focused regression suites for touched parser/visitor paths and fix any new failures

## 4. Quality Gates and Validation

- [x] 4.1 Run post-change `MySqlPerfTest` and memory test, compare with baseline, and document results
- [x] 4.2 Run `mvn -pl core test` and resolve regressions
- [x] 4.3 Run `mvn -pl core checkstyle:check` (or equivalent style gate) and resolve violations
- [x] 4.4 Capture structured baseline/post evidence and summarize compatibility conclusions
