# Test Infrastructure Guide (Core)

## Scope

This guide applies to `core` test code, especially parser and BVT coverage under `core/src/test/java`.

## Shared Conventions

- Prefer reusable parsing helpers for repeated single-statement setup.
- Keep assertion style consistent: expected vs actual order, stable formatting checks, and explicit behavior checks.
- For behavior-preserving refactors, capture baseline/post evidence in a single notes file under the change directory.

## Recommended Validation Lanes

### Focused lane (fast feedback)

- Parser/BVT regression subset:
  - `mvn -pl core -Dtest=LateralViewTest,HiveSelectTest_distribute,HiveSelectTest_2_lateralview,SnowflakeParserTest,DialectFeatureTest,SQLParserRefactorRegressionTest test`

### Full lane (completion gate)

- Affected module full test run:
  - `mvn -pl core test`
- Style gate:
  - `mvn -pl core checkstyle:check`

## Evidence Capture Template

For behavior-sensitive refactors, include:

1. Baseline focused-lane output summary (`tests run`, failures/errors/skips).
2. Baseline perf/memory output (`MySqlPerfTest`, `MemoryTest`) if required by tasks.
3. Post-change focused-lane output summary and direct comparison.
4. Post-change perf/memory comparison (averages + delta) where applicable.
5. Final full-lane + style gate result.
