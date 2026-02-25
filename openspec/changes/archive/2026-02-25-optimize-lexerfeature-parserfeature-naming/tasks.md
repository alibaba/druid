## 1. Naming Contract Definition

- [x] 1.1 Finalize canonical naming rules for `LexerFeature` and `ParserFeature` (scope wording, polarity, and kebab/camel conventions where applicable).
- [x] 1.2 Define accepted/rejected naming examples and anti-patterns for review consistency.
- [x] 1.3 Validate that each added naming requirement has at least one verifiable scenario outcome.

## 2. Parser Core Requirement Alignment

- [x] 2.1 Update parser feature-gate naming in targeted `core` parser paths to follow canonical terms without changing behavior.
- [x] 2.2 Ensure `sql-parser-core` gate-flow semantics remain equivalent for enabled/disabled feature branches after naming alignment.
- [x] 2.3 Add or update parser tests that cover naming-aligned gate paths and confirm unchanged acceptance/rejection outcomes.

## 3. Verification and Readiness

- [x] 3.1 Run focused parser regression checks for touched files and confirm pass status.
- [x] 3.2 Perform checkstyle and build verification for modified sources/tests.
- [x] 3.3 Record concise verification notes for behavior parity and naming consistency review outcomes.
