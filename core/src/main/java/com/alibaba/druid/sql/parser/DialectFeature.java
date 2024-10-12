package com.alibaba.druid.sql.parser;

public class DialectFeature {
    private long lexerFeature;

    private long parserFeature;

    public DialectFeature() {
        lexerFeature = 0L;
        parserFeature = 0L;
        configFeature(
                LexerFeature.ScanNumberPrefixB,
                LexerFeature.ScanNumberCommonProcess,
                ParserFeature.AcceptUnion,
                ParserFeature.SQLTimestampExpr,
                ParserFeature.PrimaryBangBangSupport,
                ParserFeature.AdditiveRestPipesAsConcat,
                ParserFeature.ParseStatementListSelectUnsupportedSyntax
        );
    }

    public void configFeature(Feature feature, boolean state) {
        if (feature instanceof LexerFeature) {
            this.lexerFeature = feature.config(this.lexerFeature, state);
        } else if (feature instanceof ParserFeature) {
            this.parserFeature = feature.config(this.parserFeature, state);
        } else {
            throw new ParserException("Unsupported feature type.");
        }
    }

    public void configFeature(Feature... features) {
        for (Feature feature : features) {
            configFeature(feature, true);
        }
    }

    public void unconfigFeature(Feature... features) {
        for (Feature feature : features) {
            configFeature(feature, false);
        }
    }

    public boolean isEnabled(Feature feature) {
        if (feature instanceof LexerFeature) {
            return feature.isEnabled(this.lexerFeature);
        } else if (feature instanceof ParserFeature) {
            return feature.isEnabled(this.parserFeature);
        } else {
            throw new ParserException("Unsupported feature type.");
        }
    }

    public interface Feature {
        boolean isEnabled(long features);
        long config(long features, boolean state);
        long getMask();
    }

    public enum LexerFeature implements Feature {
        ScanSQLTypeBlockComment(1L),
        ScanSQLTypeWithSemi(1L << 1),
        ScanSQLTypeWithFrom(1L << 2),
        ScanSQLTypeWithFunction(1L << 3),
        ScanSQLTypeWithBegin(1L << 4),
        ScanSQLTypeWithAt(1L << 5),
        NextTokenColon(1L << 6),
        NextTokenPrefixN(1L << 7),
        ScanString2PutDoubleBackslash(1L << 8),
        ScanAliasU(1L << 9),
        ScanNumberPrefixB(1L << 10),
        ScanNumberCommonProcess(1L << 11),
        ScanVariableAt(1L << 12),
        ScanVariableGreaterThan(1L << 13),
        ScanVariableSkipIdentifiers(1L << 14),
        ScanVariableMoveToSemi(1L << 15),
        ScanHiveCommentDoubleSpace(1L << 16),
        ScanSubAsIdentifier(1L << 17);

        private final long mask;

        @Override
        public long getMask() {
            return mask;
        }

        LexerFeature(long mask) {
            this.mask = mask;
        }

        @Override
        public boolean isEnabled(long features) {
            return (features & mask) != 0;
        }

        @Override
        public long config(long features, boolean state) {
            if (state) {
                features |= this.mask;
            } else {
                features &= ~this.mask;
            }

            return features;
        }
    }

    public enum ParserFeature implements Feature {
        AcceptUnion(1L),
        QueryRestSemi(1L << 1),
        AsofJoin(1L << 2),
        GlobalJoin(1L << 3),
        JoinAt(1L << 4),
        JoinRightTableWith(1L << 5),
        JoinRightTableFrom(1L << 6),
        JoinRightTableAlias(1L << 7),
        PostNaturalJoin(1L << 8),
        MultipleJoinOn(1L << 9),
        UDJ(1L << 10),
        TwoConsecutiveUnion(1L << 11),
        QueryTable(1L << 12),
        GroupByAll(1L << 13),
        RewriteGroupByCubeRollupToFunction(1L << 14),
        GroupByPostDesc(1L << 15),
        GroupByItemOrder(1L << 16),
        SQLDateExpr(1L << 17),
        SQLTimestampExpr(1L << 18),
        PrimaryVariantColon(1L << 19),
        PrimaryBangBangSupport(1L << 20),
        PrimaryTwoConsecutiveSet(1L << 21),
        PrimaryLbraceOdbcEscape(1L << 22),
        ParseAllIdentifier(1L << 23),
        PrimaryRestCommaAfterLparen(1L << 24),
        InRestSpecificOperation(1L << 25),
        AdditiveRestPipesAsConcat(1L << 26),
        ParseAssignItemRparenCommaSetReturn(1L << 27),
        ParseAssignItemEqSemiReturn(1L << 28),
        ParseAssignItemSkip(1L << 29),
        ParseAssignItemEqeq(1L << 30),
        ParseSelectItemPrefixX(1L << 31),
        ParseLimitBy(1L << 32),
        ParseStatementListWhen(1L << 33),
        ParseStatementListSelectUnsupportedSyntax(1L << 34),
        ParseStatementListUpdatePlanCache(1L << 35),
        ParseStatementListRollbackReturn(1L << 36),
        ParseStatementListCommitReturn(1L << 37),
        ParseStatementListLparenContinue(1L << 38),
        ParseRevokeFromUser(1L << 39),
        ParseDropTableTables(1L << 40),
        ParseCreateSql(1L << 41),
        CreateTableBodySupplemental(1L << 42),
        TableAliasConnectWhere(1L << 43),
        TableAliasAsof(1L << 44),
        TableAliasLock(1L << 45),
        TableAliasPartition(1L << 46),
        TableAliasTable(1L << 47),
        TableAliasBetween(1L << 48),
        TableAliasRest(1L << 49),
        AsCommaFrom(1L << 50),
        AsSkip(1L << 51),
        AsSequence(1L << 52),
        AsDatabase(1L << 53),
        AsDefault(1L << 54),
        AliasLiteralFloat(1L << 55);

        private final long mask;

        ParserFeature(long mask) {
            this.mask = mask;
        }

        @Override
        public long getMask() {
            return mask;
        }

        @Override
        public boolean isEnabled(long features) {
            return (features & mask) != 0;
        }

        @Override
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
