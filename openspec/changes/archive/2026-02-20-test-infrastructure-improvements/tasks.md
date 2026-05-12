## 1. Baseline and Scope

- [x] 1.1 Inventory current `core` test infrastructure pain points (duplicate fixtures, inconsistent assertions, evidence gaps) and map target files
- [x] 1.2 Run baseline focused parser/BVT regression suite and record results for before/after comparison
- [x] 1.3 Run baseline `MySqlPerfTest` and memory test and store outputs for post-change validation

## 2. Core Test Infrastructure Improvements

- [x] 2.1 Introduce reusable test helper/convention utilities in `core/src/test/java` for parser/BVT style tests
- [x] 2.2 Refactor representative parser/BVT tests to adopt shared helper patterns without changing functional expectations
- [x] 2.3 Add or update documentation for test conventions, fixture style, and evidence capture workflow

## 3. Spec-Driven Regression Coverage Alignment

- [x] 3.1 Add/adjust regression tests to cover behavior-preserving refactor verification expectations from updated `sql-parser-core` requirement
- [x] 3.2 Ensure tests explicitly validate baseline-equivalent outcomes and stable parser error locality where applicable
- [x] 3.3 Verify focused validation lane commands and full-lane commands are reproducible in local workflow notes

## 4. Quality Gates and Comparison

- [x] 4.1 Run post-change focused parser/BVT suites and compare with baseline evidence
- [x] 4.2 Run post-change `MySqlPerfTest` and memory test, compare against baseline, and document conclusions
- [x] 4.3 Run affected module tests (`mvn -pl core test`) and resolve any regressions
- [x] 4.4 Run style gate (`mvn -pl core checkstyle:check`) and fix violations
