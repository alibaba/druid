## ADDED Requirements

### Requirement: Inheritance Hierarchy Consistency in AST Traversal
Parser/visitor refactors touching SQL AST inheritance layers SHALL keep equivalent node families on consistent base-class contracts so that traversal and dispatch behavior remain predictable and behavior-preserving.

#### Scenario: Preserve traversal compatibility while consolidating inherited defaults
- **WHEN** shared behavior is moved from duplicated subclass branches into a common parent-level contract
- **THEN** equivalent SQL inputs SHALL produce behavior-equivalent traversal/dispatch outcomes versus baseline
- **AND** no new acceptance or rejection behavior SHALL be introduced by hierarchy consolidation alone

#### Scenario: Preserve token advancement behavior in inheritance-sensitive parse branches
- **WHEN** parser branches that instantiate related AST subclasses are refactored for hierarchy consistency
- **THEN** token advancement order and branch selection SHALL remain equivalent to baseline behavior
- **AND** refactoring SHALL NOT consume additional tokens in failure or success paths

#### Scenario: Keep diagnostics context stable for hierarchy-related malformed inputs
- **WHEN** malformed SQL fails in parser paths that are affected by inheritance-hierarchy refactoring
- **THEN** exceptions SHALL retain meaningful token/location context comparable to baseline
- **AND** diagnostics SHALL remain sufficient to identify the failing parse branch
