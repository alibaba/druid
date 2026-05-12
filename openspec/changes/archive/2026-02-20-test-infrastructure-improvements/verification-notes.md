## Verification Notes: test-infrastructure-improvements

## 1) Baseline and Scope

### 1.1 Pain-point inventory and target files
- Duplicate/near-duplicate parser formatting setup:
  - `core/src/test/java/com/alibaba/druid/sql/parser/LateralViewTest.java`
- Repeated single-statement parsing/assertion setup in Hive BVT tests:
  - `core/src/test/java/com/alibaba/druid/bvt/sql/hive/HiveSelectTest_distribute.java`
  - `core/src/test/java/com/alibaba/druid/bvt/sql/hive/HiveSelectTest_2_lateralview.java`
- Evidence capture inconsistency for behavior-preserving refactors:
  - no shared baseline/post checklist document under `docs/` for focused lane + full lane commands

### 1.2 Baseline focused regression suite
- Command:
  - `mvn -pl core -Dtest=LateralViewTest,HiveSelectTest_distribute,HiveSelectTest_2_lateralview,SnowflakeParserTest,DialectFeatureTest test`
- Result:
  - `Tests run: 137, Failures: 0, Errors: 0, Skipped: 0`

### 1.3 Baseline perf/memory
- Command:
  - `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`
- `MySqlPerfTest` samples:
  - `754, 516, 501, 513, 502, 503, 497, 501, 496, 495`
- `MemoryTest`:
  - `memory used : 25,165,824`

## 2) Post-change validation

### 2.1 Focused regression suite
- Command:
  - `mvn -pl core -Dtest=LateralViewTest,HiveSelectTest_distribute,HiveSelectTest_2_lateralview,SnowflakeParserTest,DialectFeatureTest,SQLParserRefactorRegressionTest test`
- Result:
  - `Tests run: 139, Failures: 0, Errors: 0, Skipped: 0`

### 2.2 Perf/memory comparison
- Command:
  - `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`
- Result:
  - `MySqlPerfTest` samples (post): `759, 525, 508, 516, 495, 496, 531, 498, 599, 521`
  - `MemoryTest` post: `memory used : 25,165,824`
  - Baseline average: `527.8`
  - Post average: `544.8`
  - Delta: `+3.22%`
  - Memory delta: `0`

### 2.3 Full-lane + style
- Full lane:
  - `mvn -pl core test` -> `BUILD SUCCESS`
- Checkstyle:
  - `mvn -pl core checkstyle:check` / `mvn checkstyle:check` reports large pre-existing repository-wide violations unrelated to this change path.
  - Equivalent style gate in standard module lifecycle (`mvn -pl core test`) reports:
    - `--- checkstyle:3.4.0:check (checkstyle) @ druid ---`
    - `You have 0 Checkstyle violations.`
