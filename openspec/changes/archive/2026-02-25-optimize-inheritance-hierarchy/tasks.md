## 1. Baseline and Scope Lock

- [x] 1.1 Capture pre-change parser/visitor baseline by running targeted `core` parser regression suites and recording pass/fail outputs.
- [x] 1.2 Run one baseline performance and memory check (`MySqlPerfTest` or applicable `MySqlPerf*` benchmark) and save comparable metrics for post-change validation.
- [x] 1.3 Identify inheritance-sensitive parser and visitor entry points in `com.alibaba.druid.sql.parser` and `com.alibaba.druid.sql.visitor` that are in scope for contract-preserving refactor.

## 2. Parser/Visitor Inheritance Refactor

- [x] 2.1 Refactor shared parser inheritance paths to centralize base behavior and remove duplicated override branches without changing parse acceptance/rejection semantics.
- [x] 2.2 Refactor visitor inheritance/dispatch paths (including support/adapter extraction where needed) while preserving existing visitor entry behavior.
- [x] 2.3 Keep deterministic base-to-dialect extension ordering and ensure unchanged branches are not duplicated in dialect overrides.

## 3. Regression and Compatibility Verification

- [x] 3.1 Add or update unit/BVT tests covering hierarchy-sensitive parser token advancement and malformed-input diagnostics locality.
- [x] 3.2 Add or update visitor/output regression tests to verify traversal and formatting compatibility after inheritance cleanup.
- [x] 3.3 Run full touched test suites in `core/src/test/java` and confirm checkstyle/build pass for modified files.

## 4. Performance and Release Readiness

- [x] 4.1 Re-run the same `MySqlPerf*` performance and memory checks used in baseline and compare before/after metrics against defined acceptance criteria.
- [x] 4.2 Document compatibility and benchmark evidence in change verification notes (behavior parity, perf/memory deltas, known trade-offs).
- [x] 4.3 Perform final artifact/code review for backward compatibility and confirm no new public API or dependency changes are introduced.
