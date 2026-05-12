## Baseline and Post-Refactor Verification Notes

### Environment
- Baseline code snapshot: detached `HEAD` worktree at `9667e0fa7` (`.baseline-head/`)
- Post-refactor code snapshot: current workspace (`refactor` branch, uncommitted implementation)

### 1.1 Parser behavior baseline and equivalence
- Command (baseline):  
  `mvn -pl core -Dtest=SplitTest,SplitTest2,EqualTest_boolean,EqualTest_binary,EqualTest_inquery_mysql,EqualTest_inquery_oracle test`
- Command (post):  
  `mvn -pl core -Dtest=SplitTest,SplitTest2,EqualTest_boolean,EqualTest_binary,EqualTest_inquery_mysql,EqualTest_inquery_oracle test`
- Result: both baseline and post passed `6/6`.
- Representative output snapshots were identical in baseline vs post:
  - `SplitTest`: `((1 + 2) + (3 + 4) + 5) + ((6 + 7) + (8 + 9) + 10)` and list `[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]`
  - `SplitTest2`: `0 + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9` and list `[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]`

### 1.2 / 4.1 Performance and memory baseline vs post
- Command (baseline):  
  `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test` (in `.baseline-head/`)
- Command (post):  
  `mvn -pl core -Dtest=MySqlPerfTest,MemoryTest test` (in current workspace)

#### MySqlPerfTest throughput samples
- Baseline: `760, 539, 499, 498, 518, 643, 561, 497, 495, 496`
- Post: `820, 524, 546, 527, 514, 518, 527, 513, 516, 514`
- Baseline average: `550.6`
- Post average: `551.9`
- Relative delta: `+0.24%`

#### MemoryTest
- Baseline: `memory used : 25,165,824`
- Post: `memory used : 25,165,824`
- Delta: `0`

### Conclusion
- Parser behavior equivalence is preserved for representative baseline test set.
- Performance/memory metrics are stable relative to baseline (no meaningful regression observed).
