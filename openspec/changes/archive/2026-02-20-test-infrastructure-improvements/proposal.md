## Why

Current parser and component tests have uneven coverage patterns, inconsistent fixture style, and limited reusable test utilities, which makes regressions harder to localize and increases maintenance cost. We should standardize and strengthen test infrastructure now to reduce refactor risk and improve iteration speed across `core` and related modules.

## What Changes

- Establish a shared test-infrastructure baseline for `core` test code (utility helpers, fixture conventions, and assertion patterns) to reduce duplication.
- Improve targeted regression coverage templates for parser/BVT style tests so new changes can add consistent, high-signal tests with less boilerplate.
- Define lightweight test execution guidance for affected suites (focused test sets vs full module runs) to improve local and CI feedback loops.
- Add documentation and structure for test evidence capture (before/after outputs for behavior-sensitive refactors).
- No runtime behavior changes and no breaking API changes are introduced.

## Capabilities

### New Capabilities
- `test-infrastructure-core`: Defines reusable testing conventions, helper patterns, and validation expectations for Druid core test suites.

### Modified Capabilities
- `sql-parser-core`: Clarify requirement-level expectations for regression coverage quality and evidence capture when parser-internal refactors are performed.

## Impact

- **Affected modules**: Primarily `core` (test sources under `src/test/java`, especially parser and BVT suites); optional minor documentation updates at repo root or `docs/`.
- **Change type**: Enhancement / refactoring of test infrastructure (non-functional).
- **Backward compatibility**: Backward compatible; no changes to production runtime contracts.
- **Public APIs**: No public API changes expected.
- **Dependencies/systems**: Maven test execution flow and CI test stages may be adjusted for clearer scope and repeatability, without adding mandatory runtime dependencies.
