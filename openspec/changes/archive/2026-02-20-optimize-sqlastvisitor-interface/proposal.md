## Why

`SQLASTVisitor` currently carries broad method surface and uneven default behavior patterns, which increases maintenance cost and raises the risk of inconsistent visitor handling across dialect and AST extensions. We should optimize the interface now to improve readability and extensibility while keeping existing traversal semantics stable.

## What Changes

- Refine `SQLASTVisitor` interface structure to reduce complexity and make common visitor contracts easier to follow.
- Standardize default/compatibility behavior for touched visitor entry points so existing implementations remain behavior-equivalent.
- Update impacted visitor implementations and related tests to verify no regression in traversal/output behavior.
- Add focused regression validation guidance for interface-level refactor changes in visitor-related parser paths.
- No intentional SQL parsing/formatting behavior change and no **BREAKING** runtime contract changes.

## Capabilities

### New Capabilities
- `<none>`: No new capability is introduced; this change focuses on refining existing visitor architecture behavior contracts.

### Modified Capabilities
- `sql-parser-core`: Extend behavior-preserving refactor requirements to explicitly cover `SQLASTVisitor` interface optimization and compatibility expectations.

## Impact

- **Affected modules**: Primarily `core` (visitor interfaces/implementations and related SQL parser tests).
- **Change type**: Refactoring / maintainability enhancement.
- **Backward compatibility**: Expected backward compatible for existing parser/visitor flows; compatibility paths will be kept in touched areas.
- **Public APIs**: `SQLASTVisitor` is a public interface; this change will preserve practical compatibility expectations and verify with regression tests.
- **Dependencies/systems**: No new runtime dependencies expected.
