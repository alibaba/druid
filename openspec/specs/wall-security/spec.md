## Purpose

Define baseline requirements for SQL Wall security checks that rely on parsed AST and dialect-aware validation.

## Requirements

### Requirement: Parse-Then-Validate Security Flow

Wall checks SHALL validate SQL semantics from parsed structures rather than only raw string matching.

#### Scenario: Validate incoming SQL
- **WHEN** SQL passes through wall protection
- **THEN** SQL SHALL be parsed and inspected via AST-oriented logic
- **AND** security decisions SHALL be based on parsed statement and expression structure

### Requirement: Dialect-Aware Wall Validation

Wall validation SHALL support dialect-specific behavior through dedicated providers or visitors.

#### Scenario: Run wall check for specific database dialect
- **WHEN** wall validation executes under a selected dialect
- **THEN** dialect-specific visitor/provider logic SHALL be applied
- **AND** dialect grammar differences SHALL not be treated as generic syntax violations

### Requirement: Enforced Security Outcome

Detected security violations SHALL produce an explicit block or violation result.

#### Scenario: SQL contains blocked operation pattern
- **WHEN** wall rules classify the SQL as unsafe
- **THEN** wall check SHALL return a violation outcome that callers can enforce
- **AND** the operation SHALL not be treated as an allowed success path
