## Why

The original change assumed Snowflake parser classes contained many token-consumption branches suitable for `nextIf` refactoring. After validating the current codebase, Snowflake dialect currently exposes only thin parser wrappers (`SnowflakeStatementParser`, `SnowflakeExprParser`) and does not contain the previously targeted parser methods/files.

To keep this change useful and correct, we convert it to a scope-correction and behavior-preservation change: align artifacts with real files, keep delta requirements focused on refactoring equivalence, and avoid inaccurate implementation claims.

## What Changes

- Audit Snowflake dialect parser files that exist in current repository
- Remove references to non-existent Snowflake parser files and methods from change artifacts
- Keep `sql-parser-core` delta focused on refactoring behavior equivalence and no-op applicability
- Sync verified Snowflake refactor scenario into main `sql-parser-core` spec baseline

## Capabilities

### New Capabilities
<!-- No new capabilities - this is a refactoring change -->

### Modified Capabilities
- `sql-parser-core`: clarify Snowflake refactoring equivalence requirements and no-op behavior when target patterns are absent

## Impact

### Affected Files
- `openspec/changes/refactor-snowflake-parser-nextif/proposal.md`
- `openspec/changes/refactor-snowflake-parser-nextif/design.md`
- `openspec/changes/refactor-snowflake-parser-nextif/tasks.md`
- `openspec/changes/refactor-snowflake-parser-nextif/specs/sql-parser-core/spec.md`
- `openspec/specs/sql-parser-core/spec.md`

### Backward Compatibility
- This change is backward compatible
- No public API changes
- No runtime behavior changes - this is artifact and specification alignment with existing implementation

### Testing
- Structural verification of Snowflake parser file set and parser-scope references
- OpenSpec status and apply-context verification for artifact consistency
