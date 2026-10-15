## Verification Summary

This change applies naming-alignment refactors for `LexerFeature` and `ParserFeature` while preserving parser behavior.

## Commands Executed

```bash
mvn -pl core -Dtest=DialectFeatureNamingAliasTest,SQLParserRefactorRegressionTest -DfailIfNoTests=false test
```

## Results

- Build status: PASS
- Checkstyle: PASS (0 violations)
- Focused regression tests: PASS
  - `SQLParserRefactorRegressionTest` (2 tests)
  - `DialectFeatureNamingAliasTest` (2 tests)

## Behavior Parity Notes

- Introduced canonical aliases:
  - `LexerFeature.ScanStringDoubleBackslash` (compatible alias of `ScanString2PutDoubleBackslash`)
  - `ParserFeature.UserDefinedJoin` (compatible alias of `UDJ`)
- Updated selected parser/lexer usage sites to canonical names without altering feature masks.
- Alias compatibility tests verify both old and new names map to identical masks and enable/disable state transitions.
