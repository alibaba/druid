## 1. Baseline and Scope Lock

- [x] 1.1 Run pre-refactor baseline checks for parser behavior and record representative `SQLExprParser.primary()` inputs/outputs
- [x] 1.2 Run baseline performance and memory tests (`MySqlPerfTest` and memory test) and store comparison notes
- [x] 1.3 Identify `SQLExprParser.primary()` branch groups and define extraction order with explicit no-behavior-change constraints

## 2. Primary Method Decomposition

- [x] 2.1 Extract branch-focused private helpers from `SQLExprParser.primary()` while keeping `primary()` as orchestration entry
- [x] 2.2 Preserve token advancement order and optional-branch consumption semantics during extraction
- [x] 2.3 Preserve AST construction behavior and expression acceptance/rejection outcomes for existing grammar paths
- [x] 2.4 Preserve parser error locality and branch-specific diagnostics in malformed-input paths

## 3. Regression Coverage

- [x] 3.1 Add/extend unit tests for representative primary-expression paths (identifier, literal, call-like, and nested forms)
- [x] 3.2 Add tests that verify optional syntax fragment handling in `primary()`-touched branches
- [x] 3.3 Add malformed-expression regression tests to verify token/location context remains meaningful
- [x] 3.4 Run parser/BVT suites in `core/src/test/java/com/alibaba/druid/bvt/sql/` and confirm no semantic regressions

## 4. Quality Gates and Validation

- [x] 4.1 Run post-refactor `MySqlPerfTest` and memory test, compare with baseline, and document conclusion
- [x] 4.2 Run module tests (`mvn test`) for affected scope and fix regressions
- [x] 4.3 Run style gate (`mvn checkstyle:check` or equivalent) and resolve violations
- [x] 4.4 Mark tasks complete only after behavior equivalence evidence and test results are recorded
