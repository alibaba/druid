## 1. Baseline and Scope Definition

- [x] 1.1 Run baseline parser regression tests for alias-heavy and DialectFeature-gated SQL paths and record outcomes
- [x] 1.2 Run baseline `MySqlPerfTest` and memory test and store comparison notes for post-refactor checks
- [x] 1.3 Identify current `SQLParser.tableAlias()` branch groups and DialectFeature check sites, then lock a no-behavior-change extraction plan

## 2. Core Refactoring

- [x] 2.1 Refactor `SQLParser.tableAlias()` into focused helper branches while keeping `tableAlias()` as orchestration entry
- [x] 2.2 Unify `DialectFeature` usage patterns in touched parser paths without changing effective gate semantics
- [x] 2.3 Preserve token-consumption order and alias acceptance/rejection behavior across optional alias branches
- [x] 2.4 Preserve parser diagnostics locality and token context for malformed alias-related inputs

## 3. Regression Coverage

- [x] 3.1 Add/extend unit tests for supported table-alias forms and edge syntax around alias parsing
- [x] 3.2 Add tests for feature-gated parser paths touched by DialectFeature unification
- [x] 3.3 Add malformed-input regression tests asserting stable parser error locality and context
- [x] 3.4 Run parser/BVT suites under `core/src/test/java/com/alibaba/druid/bvt/sql/` and verify no behavior regressions

## 4. Quality Gates and Validation

- [x] 4.1 Run post-refactor `MySqlPerfTest` and memory test, compare with baseline, and document conclusion
- [x] 4.2 Run affected module tests (`mvn test`) and fix any regressions
- [x] 4.3 Run style gate (`mvn checkstyle:check` or equivalent) and resolve violations
- [x] 4.4 Mark tasks complete only after equivalence evidence and test outputs are captured
