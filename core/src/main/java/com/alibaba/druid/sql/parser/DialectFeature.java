package com.alibaba.druid.sql.parser;

public class DialectFeature {
    private long lexerFeature;

    private long parserFeature;

    public DialectFeature() {
        lexerFeature = 0L;
        configFeature(LexerFeature.EnableScanSQLTypeBlockComment, false);
        configFeature(LexerFeature.EnableScanSQLTypeBlockComment, false);
        configFeature(LexerFeature.EnableScanSQLTypeWithSemi, false);
        configFeature(LexerFeature.EnableScanSQLTypeWithFrom, false);
        configFeature(LexerFeature.EnableScanSQLTypeWithFunction, false);
        configFeature(LexerFeature.EnableScanSQLTypeWithBegin, false);
        configFeature(LexerFeature.EnableScanSQLTypeWithAt, false);
        configFeature(LexerFeature.EnableNextTokenColon, false);
        configFeature(LexerFeature.EnableNextTokenPrefixN, false);
        configFeature(LexerFeature.EnableScanString2PutDoubleBackslash, false);
        configFeature(LexerFeature.EnableScanAliasU, false);
        configFeature(LexerFeature.EnableScanNumberPrefixB, true);
        configFeature(LexerFeature.EnableScanNumberCommonProcess, true);
        configFeature(LexerFeature.EnableScanVariableAt, false);
        configFeature(LexerFeature.EnableScanVariableGreaterThan, false);
        configFeature(LexerFeature.EnableScanVariableSkipIdentifiers, false);
        configFeature(LexerFeature.EnableScanVariableMoveToSemi, false);
        configFeature(LexerFeature.EnableScanHiveCommentDoubleSpace, false);

        parserFeature = 0L;
        configFeature(ParserFeature.EnableAcceptUnion, true);
        configFeature(ParserFeature.EnableQueryRestSemi, false);
        configFeature(ParserFeature.EnableAsofJoin, false);
        configFeature(ParserFeature.EnableGlobalJoin, false);
        configFeature(ParserFeature.EnableJoinAt, false);
        configFeature(ParserFeature.EnableJoinRightTableWith, false);
        configFeature(ParserFeature.EnableJoinRightTableFrom, false);
        configFeature(ParserFeature.EnableJoinRightTableAlias, false);
        configFeature(ParserFeature.EnablePostNaturalJoin, false);
        configFeature(ParserFeature.EnableMultipleJoinOn, false);
        configFeature(ParserFeature.EnableUDJ, false);
        configFeature(ParserFeature.EnableTwoConsecutiveUnion, false);
        configFeature(ParserFeature.EnableQueryTable, false);
        configFeature(ParserFeature.EnableGroupByAll, false);
        configFeature(ParserFeature.EnableRewriteGroupByCubeRollupToFunction, false);
        configFeature(ParserFeature.EnableGroupByPostDesc, false);
        configFeature(ParserFeature.EnableGroupByItemOrder, false);
        configFeature(ParserFeature.EnableSQLDateExpr, false);
        configFeature(ParserFeature.EnableSQLTimestampExpr, true);
        configFeature(ParserFeature.EnablePrimaryVariantColon, false);
        configFeature(ParserFeature.EnablePrimaryBangBangSupport, true);
        configFeature(ParserFeature.EnablePrimaryTwoConsecutiveSet, false);
        configFeature(ParserFeature.EnablePrimaryLbraceOdbcEscape, false);
        configFeature(ParserFeature.EnableParseAllIdentifier, false);
        configFeature(ParserFeature.EnablePrimaryRestCommaAfterLparen, false);
        configFeature(ParserFeature.EnableInRestSpecificOperation, false);
        configFeature(ParserFeature.EnableAdditiveRestPipesAsConcat, true);
        configFeature(ParserFeature.EnableParseAssignItemRparenCommaSetReturn, false);
        configFeature(ParserFeature.EnableParseAssignItemEqSemiReturn, false);
        configFeature(ParserFeature.EnableParseAssignItemSkip, false);
        configFeature(ParserFeature.EnableParseAssignItemEqeq, false);
        configFeature(ParserFeature.EnableParseSelectItemPrefixX, false);
        configFeature(ParserFeature.EnableParseLimitBy, false);
        configFeature(ParserFeature.EnableParseStatementListWhen, false);
        configFeature(ParserFeature.EnableParseStatementListSelectUnsupportedSyntax, true);
        configFeature(ParserFeature.EnableParseStatementListUpdatePlanCache, false);
        configFeature(ParserFeature.EnableParseStatementListRollbackReturn, false);
        configFeature(ParserFeature.EnableParseStatementListCommitReturn, false);
        configFeature(ParserFeature.EnableParseStatementListLparenContinue, false);
        configFeature(ParserFeature.EnableParseRevokeFromUser, false);
        configFeature(ParserFeature.EnableParseDropTableTables, false);
        configFeature(ParserFeature.EnableParseCreateSql, false);
        configFeature(ParserFeature.EnableCreateTableBodySupplemental, false);
        configFeature(ParserFeature.EnableTableAliasConnectWhere, false);
        configFeature(ParserFeature.EnableTableAliasAsof, false);
        configFeature(ParserFeature.EnableTableAliasLock, false);
        configFeature(ParserFeature.EnableTableAliasPartition, false);
        configFeature(ParserFeature.EnableTableAliasTable, false);
        configFeature(ParserFeature.EnableTableAliasBetween, false);
        configFeature(ParserFeature.EnableTableAliasRest, false);
        configFeature(ParserFeature.EnableAsCommaFrom, false);
        configFeature(ParserFeature.EnableAsSkip, false);
        configFeature(ParserFeature.EnableAsSequence, false);
        configFeature(ParserFeature.EnableAsDatabase, false);
        configFeature(ParserFeature.EnableAsDefault, false);
        configFeature(ParserFeature.EnableAliasLiteralFloat, false);
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
        EnableScanSQLTypeBlockComment(1),

        EnableScanSQLTypeWithSemi(1 << 1),

        EnableScanSQLTypeWithFrom(1 << 2),

        EnableScanSQLTypeWithFunction(1 << 3),

        EnableScanSQLTypeWithBegin(1 << 4),

        EnableScanSQLTypeWithAt(1 << 4),

        EnableNextTokenColon(1 << 5),

        EnableNextTokenPrefixN(1 << 6),

        EnableScanString2PutDoubleBackslash(1 << 7),

        EnableScanAliasU(1 << 8),

        EnableScanNumberPrefixB(1 << 9),

        EnableScanNumberCommonProcess(1 << 10),

        EnableScanVariableAt(1 << 11),

        EnableScanVariableGreaterThan(1 << 12),

        EnableScanVariableSkipIdentifiers(1 << 13),

        EnableScanVariableMoveToSemi(1 << 14),

        EnableScanHiveCommentDoubleSpace(1 << 15);

        private final long mask;

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
