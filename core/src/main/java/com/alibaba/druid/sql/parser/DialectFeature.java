package com.alibaba.druid.sql.parser;

public class DialectFeature {
    private long lexerFeature;

    private long parserFeature;

    public DialectFeature() {
        lexerFeature = 0L;
        configFeature(LexerFeature.EnableScanNumberPrefixB, true);
        configFeature(LexerFeature.EnableScanNumberCommonProcess, true);

        parserFeature = 0L;
        configFeature(ParserFeature.EnableAcceptUnion, true);
        configFeature(ParserFeature.EnablePrimaryBangBangSupport, true);
        configFeature(ParserFeature.EnableAdditiveRestPipesAsConcat, true);
        configFeature(ParserFeature.EnableParseStatementListSelectUnsupportedSyntax, true);
    }

    public void configFeature(LexerFeature feature, boolean state) {
        this.lexerFeature = feature.config(this.lexerFeature, state);
    }

    public void configFeature(ParserFeature feature, boolean state) {
        this.parserFeature = feature.config(this.parserFeature, state);
    }

    public boolean isEnabled(LexerFeature feature) {
        return feature.isEnabled(this.lexerFeature);
    }

    public boolean isEnabled(ParserFeature feature) {
        return feature.isEnabled(this.parserFeature);
    }

    public enum LexerFeature {
        EnableScanSQLTypeBlockComment(1L),
        EnableScanSQLTypeWithSemi(1L << 1),
        EnableScanSQLTypeWithFrom(1L << 2),
        EnableScanSQLTypeWithFunction(1L << 3),
        EnableScanSQLTypeWithBegin(1L << 4),
        EnableScanSQLTypeWithAt(1L << 5),
        EnableNextTokenColon(1L << 6),
        EnableNextTokenPrefixN(1L << 7),
        EnableScanString2PutDoubleBackslash(1L << 8),
        EnableScanAliasU(1L << 9),
        EnableScanNumberPrefixB(1L << 10),
        EnableScanNumberCommonProcess(1L << 11),
        EnableScanVariableAt(1L << 12),
        EnableScanVariableGreaterThan(1L << 13),
        EnableScanVariableSkipIdentifiers(1L << 14),
        EnableScanVariableMoveToSemi(1L << 15),
        EnableScanHiveCommentDoubleSpace(1L << 16);

        private final long mask;

        public long getMask() {
            return mask;
        }

        LexerFeature(long mask) {
            this.mask = mask;
        }

        public boolean isEnabled(long features) {
            return (features & mask) != 0;
        }

        public long config(long features, boolean state) {
            if (state) {
                features |= this.mask;
            } else {
                features &= ~this.mask;
            }

            return features;
        }
    }

    public enum ParserFeature {
        EnableAcceptUnion(1L),
        EnableQueryRestSemi(1L << 1),
        EnableAsofJoin(1L << 2),
        EnableGlobalJoin(1L << 3),
        EnableJoinAt(1L << 4),
        EnableJoinRightTableWith(1L << 5),
        EnableJoinRightTableFrom(1L << 6),
        EnableJoinRightTableAlias(1L << 7),
        EnablePostNaturalJoin(1L << 8),
        EnableMultipleJoinOn(1L << 9),
        EnableUDJ(1L << 10),
        EnableTwoConsecutiveUnion(1L << 11),
        EnableQueryTable(1L << 12),
        EnableGroupByAll(1L << 13),
        EnableRewriteGroupByCubeRollupToFunction(1L << 14),
        EnableGroupByPostDesc(1L << 15),
        EnableGroupByItemOrder(1L << 16),
        EnableSQLDateExpr(1L << 17),
        EnableSQLTimestampExpr(1L << 18),
        EnablePrimaryVariantColon(1L << 19),
        EnablePrimaryBangBangSupport(1L << 20),
        EnablePrimaryTwoConsecutiveSet(1L << 21),
        EnablePrimaryLbraceOdbcEscape(1L << 22),
        EnableParseAllIdentifier(1L << 23),
        EnablePrimaryRestCommaAfterLparen(1L << 24),
        EnableInRestSpecificOperation(1L << 25),
        EnableAdditiveRestPipesAsConcat(1L << 26),
        EnableParseAssignItemRparenCommaSetReturn(1L << 27),
        EnableParseAssignItemEqSemiReturn(1L << 28),
        EnableParseAssignItemSkip(1L << 29),
        EnableParseAssignItemEqeq(1L << 30),
        EnableParseSelectItemPrefixX(1L << 31),
        EnableParseLimitBy(1L << 32),
        EnableParseStatementListWhen(1L << 33),
        EnableParseStatementListSelectUnsupportedSyntax(1L << 34),
        EnableParseStatementListUpdatePlanCache(1L << 35),
        EnableParseStatementListRollbackReturn(1L << 36),
        EnableParseStatementListCommitReturn(1L << 37),
        EnableParseStatementListLparenContinue(1L << 38),
        EnableParseRevokeFromUser(1L << 39),
        EnableParseDropTableTables(1L << 40),
        EnableParseCreateSql(1L << 41),
        EnableCreateTableBodySupplemental(1L << 42),
        EnableTableAliasConnectWhere(1L << 43),
        EnableTableAliasAsof(1L << 44),
        EnableTableAliasLock(1L << 45),
        EnableTableAliasPartition(1L << 46),
        EnableTableAliasTable(1L << 47),
        EnableTableAliasBetween(1L << 48),
        EnableTableAliasRest(1L << 49),
        EnableAsCommaFrom(1L << 50),
        EnableAsSkip(1L << 51),
        EnableAsSequence(1L << 52),
        EnableAsDatabase(1L << 53),
        EnableAsDefault(1L << 54),
        EnableAliasLiteralFloat(1L << 55);

        private final long mask;

        ParserFeature(long mask) {
            this.mask = mask;
        }

        public long getMask() {
            return mask;
        }

        public boolean isEnabled(long features) {
            return (features & mask) != 0;
        }

        public long config(long features, boolean state) {
            if (state) {
                features |= this.mask;
            } else {
                features &= ~this.mask;
            }

            return features;
        }
    }

}
