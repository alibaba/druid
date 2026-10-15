## MODIFIED Requirements

### Requirement: Parser Error Locality

Parser errors SHALL remain diagnosable with meaningful token and location context, and diagnostics in touched lexer/parser paths SHALL use a consistent context pattern for equivalent malformed-input classes.

#### Scenario: Invalid SQL input
- **WHEN** malformed SQL cannot be parsed
- **THEN** parser exception messages SHALL include token-related context
- **AND** error reporting SHALL not silently swallow parser failures

#### Scenario: Keep diagnostic context pattern stable for equivalent malformed inputs
- **WHEN** equivalent malformed inputs fail through touched lexer/parser branches
- **THEN** exceptions SHALL contain comparable location context (line/column or equivalent position metadata)
- **AND** diagnostics SHALL include sufficient token or branch context to identify the failing parse point

#### Scenario: Preserve behavior while improving diagnostic clarity
- **WHEN** diagnostics are refined in behavior-preserving parser/lexer refactors
- **THEN** parse acceptance and rejection boundaries SHALL remain equivalent to baseline
- **AND** diagnostic improvements SHALL NOT consume extra tokens compared to baseline failure paths
