## ADDED Requirements

### Requirement: Canonical Naming for Lexer and Parser Feature Gates
`LexerFeature` and `ParserFeature` identifiers SHALL follow a canonical naming convention with consistent scope terms and semantic polarity so equivalent gates are easy to correlate during development and review.
Canonical names SHALL follow the pattern `Scope + Intent`, and names expressing enabled/disabled semantics SHALL avoid ambiguous abbreviations.

#### Scenario: Use consistent scope terms for equivalent gates
- **WHEN** feature gates represent equivalent behavior constraints across lexer and parser paths
- **THEN** names SHALL use aligned scope wording instead of divergent synonyms
- **AND** reviewers SHALL be able to infer equivalence without reading implementation details

#### Scenario: Keep naming polarity explicit
- **WHEN** a feature gate name describes enabled or disabled behavior
- **THEN** the name SHALL use explicit polarity terms that avoid double negatives and ambiguous intent
- **AND** the same polarity convention SHALL be used for both lexer and parser feature families

### Requirement: Deterministic Feature-Gate Naming Review Guidance
Changes that add or rename `LexerFeature` or `ParserFeature` entries SHALL include deterministic naming review checks and examples of accepted and rejected names.
Accepted examples SHALL include canonical names such as `ScanStringDoubleBackslash` and `UserDefinedJoin`; rejected examples SHALL include ambiguous abbreviations that do not expose scope or intent.

#### Scenario: Provide actionable guidance for non-canonical names
- **WHEN** a proposed feature name violates canonical naming guidance
- **THEN** the review output SHALL include at least one compliant alternative
- **AND** the rationale SHALL reference the canonical naming rule that was violated
