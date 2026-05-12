## Purpose

Define baseline requirements for reusable core test infrastructure conventions, structured regression evidence capture, and tiered test execution guidance.

## Requirements

### Requirement: Reusable Core Test Infrastructure Conventions
Core test suites SHALL provide reusable infrastructure conventions for fixture setup, assertion style, and regression validation so that new tests can be added with lower duplication and consistent readability.

#### Scenario: Use shared fixture and assertion patterns
- **WHEN** developers add or update tests in `core` parser/BVT areas
- **THEN** tests SHALL follow shared fixture and assertion conventions defined for this capability
- **AND** duplicated setup/verification logic SHALL be minimized through reusable helper patterns

### Requirement: Structured Regression Evidence for Refactor-Sensitive Changes
Refactor-sensitive changes SHALL capture baseline and post-change verification evidence in a structured format that allows behavior and quality comparisons.

#### Scenario: Capture comparable before/after verification output
- **WHEN** a change modifies parser-internal or similarly behavior-sensitive logic
- **THEN** verification records SHALL include baseline and post-change command outputs for selected regression and quality checks
- **AND** the comparison summary SHALL state behavior-equivalence conclusions and any observed performance or memory deltas

### Requirement: Tiered Test Execution Guidance
Test infrastructure guidance SHALL define both focused and full validation lanes to balance local iteration speed with release safety.

#### Scenario: Use focused lane before full lane
- **WHEN** a contributor validates a scoped change during development
- **THEN** they SHALL be able to run a documented focused suite for fast feedback
- **AND** completion criteria SHALL still require running the broader affected-module validation lane before finalization
