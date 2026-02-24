## 1. Scope Validation

- [x] 1.1 Validate existing Snowflake dialect parser files in `core/src/main/java/com/alibaba/druid/sql/dialect/snowflake/`
- [x] 1.2 Confirm that previously targeted files/methods are absent in the current repository baseline
- [x] 1.3 Re-scope this change as artifact/spec alignment to avoid unverifiable implementation claims

## 2. Delta Spec Alignment

- [x] 2.1 Move Snowflake delta spec to standard path `specs/sql-parser-core/spec.md`
- [x] 2.2 Update delta requirement wording to focus on behavior equivalence and no-op applicability
- [x] 2.3 Remove obsolete non-standard spec file entries

## 3. Main Spec Sync

- [x] 3.1 Merge Snowflake refactor equivalence scenario into `openspec/specs/sql-parser-core/spec.md`
- [x] 3.2 Keep existing baseline requirements unchanged while appending Snowflake-specific scenario

## 4. Change Artifact Consistency

- [x] 4.1 Update `proposal.md` affected files and testing statements to match current codebase
- [x] 4.2 Update `design.md` decisions to reflect current parser structure and no-op source impact
- [x] 4.3 Replace stale implementation tasks with validated and completed artifact tasks

## 5. Workflow Verification

- [x] 5.1 Run `openspec status --change "refactor-snowflake-parser-nextif" --json`
- [x] 5.2 Run `openspec instructions apply --change "refactor-snowflake-parser-nextif" --json`
- [x] 5.3 Perform verify-style check for task completeness and artifact/code coherence
