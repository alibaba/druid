## Baseline and Post-Refactor Verification Notes

### 1.1 Baseline parser regression coverage (alias-heavy + feature-gated)
- Command:
  - `mvn -pl core -Dtest=LateralViewTest,HiveSelectTest_distribute,HiveSelectTest_2_lateralview,SnowflakeParserTest,DialectFeatureTest test`
- Baseline result: `Tests run: 137, Failures: 0, Errors: 0, Skipped: 0`

### 1.2 Baseline performance and memory
- Command:
  - `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`
- `MySqlPerfTest` samples (baseline):
  - `783, 523, 526, 518, 510, 511, 529, 524, 514, 508`
- `MemoryTest` baseline:
  - `memory used : 25,165,824`

### 1.3 Locked no-behavior-change extraction scope
- `SQLParser.tableAlias()` branch groups:
  - Leading keyword guard (`CONNECT/START/SELECT/FROM/WHERE`)
  - Identifier alias handling and hash-based special cases (`NATURAL/CROSS/OFFSET/LIMIT/...`)
  - Optional keyword alias branch resolution (`LEFT/RIGHT/INNER/FULL/OUTER/.../UNION`)
  - Required alias fallback branch (`TableAliasRest` guarded clauses)
- DialectFeature check sites covered by this change:
  - `TableAliasConnectWhere`, `TableAliasAsof`, `TableAliasLock`, `TableAliasPartition`, `TableAliasTable`, `TableAliasBetween`, `TableAliasRest`

### 3.x/4.x Post-refactor regression and validation
- Parser/BVT command:
  - `mvn -pl core -Dtest=SQLParserTableAliasRefactorTest,LateralViewTest,HiveSelectTest_distribute,HiveSelectTest_2_lateralview,SnowflakeParserTest,DialectFeatureTest test`
- Result: `Tests run: 143, Failures: 0, Errors: 0, Skipped: 0`
- New targeted unit tests:
  - `core/src/test/java/com/alibaba/druid/sql/parser/SQLParserTableAliasRefactorTest.java`

### 4.1 Post-refactor performance and memory
- Command:
  - `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test`
- `MySqlPerfTest` samples (post):
  - `763, 498, 502, 500, 495, 494, 495, 492, 501, 504`
- `MemoryTest` post:
  - `memory used : 25,165,824`
- Aggregate comparison:
  - Baseline average: `544.6`
  - Post average: `524.4`
  - Delta: `-3.71%`
  - Memory delta: `0`

### Conclusion
- Table alias parsing behavior remains regression-clean for covered alias-heavy and feature-gated test sets.
- Memory usage is unchanged in sampled run.
- Throughput sample average is lower in this run (`-3.71%`), likely affected by benchmark noise/JIT/GC variability; can be re-run with larger sample windows if stricter performance gating is required.
