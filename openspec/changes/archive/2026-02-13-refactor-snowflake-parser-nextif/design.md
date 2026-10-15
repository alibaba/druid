## Context

The initial design assumed multiple Snowflake parser files with rich statement/select parsing logic. Current repository state differs:

- Existing Snowflake dialect Java files:
  - `core/src/main/java/com/alibaba/druid/sql/dialect/snowflake/SnowflakeLexer.java`
  - `core/src/main/java/com/alibaba/druid/sql/dialect/snowflake/SnowflakeExprParser.java`
  - `core/src/main/java/com/alibaba/druid/sql/dialect/snowflake/SnowflakeStatementParser.java`
- `SnowflakeExprParser` and `SnowflakeStatementParser` are thin wrappers and do not currently expose the previously targeted refactor methods.

Therefore this change must be treated as a scope-correction and spec-alignment change, not a broad source-level parser refactor.

## Goals / Non-Goals

**Goals:**
- Align OpenSpec artifacts with the real Snowflake parser code structure
- Preserve the original intent at requirement level: refactoring must keep parser behavior equivalent
- Make the change verifiable and idempotent for future `/opsx:verify` and `/opsx:sync`

**Non-Goals:**
- No addition of new Snowflake parser classes or grammar features in this change
- No speculative edits in non-existent files
- No runtime behavior changes

## Decisions

### Decision 1: Correct the Refactor Scope to Existing Files

**Options Considered:**
| Option | Pros | Cons |
|--------|------|------|
| Keep original 4-file refactor plan | Preserves initial narrative | Invalid in current repository (files/methods absent) |
| Re-scope to current Snowflake files and artifacts | Verifiable and accurate | Produces a no-op at Java source level today |

**Decision:** Re-scope to existing Snowflake files and align artifacts/specs accordingly.

### Decision 2: Preserve Behavior as the Primary Requirement

Keep the behavioral contract in `sql-parser-core`:
- If helper-method refactor is applied, behavior must remain equivalent
- If target patterns are absent, no source rewrite is performed and the change remains a no-op implementation-wise

### Decision 3: Use Spec Sync to Record the Verified Scenario

Apply the delta spec to main `sql-parser-core` baseline by adding a Snowflake-specific equivalence scenario, keeping main capability stable and future-friendly.

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| Original tasks referenced nonexistent files | Replace with validated file list and artifact-only scope |
| Future contributors may reintroduce invalid scope assumptions | Keep explicit capability mapping in `openspec/config.yaml` and change artifacts |
| No Java source changes may appear low-value | Preserve requirement-level behavior constraints and synced main spec scenario |
