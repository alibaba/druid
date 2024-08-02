package com.alibaba.druid.sql.parser;

public class Settings {
    private boolean enableScanSQLTypeBlockComment;

    private boolean enableScanSQLTypeWithSemi;

    private boolean enableScanSQLTypeWithFrom;

    private boolean enableScanSQLTypeWithFunction;

    private boolean enableScanSQLTypeWithBegin;

    private boolean enableScanSQLTypeWithAt;

    private boolean enableNextTokenColon;

    private boolean enableNextTokenPrefixN;

    private boolean enableScanString2PutDoubleBackslash;

    private boolean enableScanAliasU;

    private boolean enableScanNumberPrefixB;

    private boolean enableScanNumberCommonProcess;

    private boolean enableScanVariableAt;

    private boolean enableScanVariableGreaterThan;

    private boolean enableScanVariableSkipIdentifiers;

    private boolean enableScanVariableMoveToSemi;

    private boolean enableScanHiveCommentDoubleSpace;

    private boolean enableAcceptUnion;

    private boolean enableQueryRestSemi;

    private boolean enableAsofJoin;

    private boolean enableGlobalJoin;

    private boolean enableJoinAt;

    private boolean enableJoinRightTableWith;

    private boolean enableJoinRightTableFrom;

    private boolean enableJoinRightTableAlias;

    private boolean enablePostNaturalJoin;

    private boolean enableMultipleJoinOn;

    private boolean enableUDJ;

    private boolean enableTwoConsecutiveUnion;

    private boolean enableQueryTable;

    private boolean enableGroupByAll;

    private boolean enableRewriteGroupByCubeRollupToFunction;

    private boolean enableGroupByPostDesc;

    private boolean enableGroupByItemOrder;

    private boolean enableSQLDateExpr;

    private boolean enableSQLTimestampExpr;

    private boolean enablePrimaryVariantColon;

    private boolean enablePrimaryBangBangSupport;

    private boolean enablePrimaryTwoConsecutiveSet;

    private boolean enablePrimaryLbraceOdbcEscape;

    private boolean enableParseAllIdentifier;

    private boolean enablePrimaryRestCommaAfterLparen;

    private boolean enableInRestSpecificOperation;

    private boolean enableAdditiveRestPipesAsConcat;

    private boolean enableParseAssignItemRparenCommaSetReturn;

    private boolean enableParseAssignItemEqSemiReturn;

    private boolean enableParseAssignItemSkip;

    private boolean enableParseAssignItemEqeq;

    private boolean enableParseSelectItemPrefixX;

    private boolean enableParseLimitBy;

    private boolean enableParseStatementListWhen;

    private boolean enableParseStatementListSelectUnsupportedSyntax;

    private boolean enableParseStatementListUpdatePlanCache;

    private boolean enableParseStatementListRollbackReturn;

    private boolean enableParseStatementListCommitReturn;

    private boolean enableParseStatementListLparenContinue;

    private boolean enableParseRevokeFromUser;

    private boolean enableParseDropTableTables;

    private boolean enableParseCreateSql;

    private boolean enableCreateTableBodySupplemental;

    private boolean enableTableAliasConnectWhere;

    private boolean enableTableAliasAsof;

    private boolean enableTableAliasLock;

    private boolean enableTableAliasPartition;

    private boolean enableTableAliasTable;

    private boolean enableTableAliasBetween;

    private boolean enableTableAliasRest;

    private boolean enableAsCommaFrom;

    private boolean enableAsSkip;

    private boolean enableAsSequence;

    private boolean enableAsDatabase;

    private boolean enableAsDefault;

    private boolean enableAliasLiteralFloat;

    public Settings() {
        this.enableScanSQLTypeBlockComment = false;
        this.enableScanSQLTypeWithSemi = false;
        this.enableScanSQLTypeWithFrom = false;
        this.enableScanSQLTypeWithFunction = false;
        this.enableScanSQLTypeWithBegin = false;
        this.enableScanSQLTypeWithAt = false;
        this.enableNextTokenColon = false;
        this.enableNextTokenPrefixN = false;
        this.enableScanString2PutDoubleBackslash = false;
        this.enableScanAliasU = false;
        this.enableScanNumberPrefixB = true;
        this.enableScanNumberCommonProcess = true;
        this.enableScanVariableAt = false;
        this.enableScanVariableGreaterThan = false;
        this.enableScanVariableSkipIdentifiers = false;
        this.enableScanVariableMoveToSemi = false;
        this.enableScanHiveCommentDoubleSpace = false;
        this.enableAcceptUnion = true;
        this.enableQueryRestSemi = false;
        this.enableAsofJoin = false;
        this.enableGlobalJoin = false;
        this.enableJoinAt = false;
        this.enableJoinRightTableWith = false;
        this.enableJoinRightTableFrom = false;
        this.enableJoinRightTableAlias = false;
        this.enablePostNaturalJoin = false;
        this.enableMultipleJoinOn = false;
        this.enableUDJ = false;
        this.enableTwoConsecutiveUnion = false;
        this.enableQueryTable = false;
        this.enableGroupByAll = false;
        this.enableRewriteGroupByCubeRollupToFunction = false;
        this.enableGroupByPostDesc = false;
        this.enableGroupByItemOrder = false;
        this.enableSQLDateExpr = false;
        this.enableSQLTimestampExpr = true;
        this.enablePrimaryVariantColon = false;
        this.enablePrimaryBangBangSupport = true;
        this.enablePrimaryTwoConsecutiveSet = false;
        this.enablePrimaryLbraceOdbcEscape = false;
        this.enableParseAllIdentifier = false;
        this.enablePrimaryRestCommaAfterLparen = false;
        this.enableInRestSpecificOperation = false;
        this.enableAdditiveRestPipesAsConcat = true;
        this.enableParseAssignItemRparenCommaSetReturn = false;
        this.enableParseAssignItemEqSemiReturn = false;
        this.enableParseAssignItemSkip = false;
        this.enableParseAssignItemEqeq = false;
        this.enableParseSelectItemPrefixX = false;
        this.enableParseLimitBy = false;
        this.enableParseStatementListWhen = false;
        this.enableParseStatementListSelectUnsupportedSyntax = true;
        this.enableParseStatementListUpdatePlanCache = false;
        this.enableParseStatementListRollbackReturn = false;
        this.enableParseStatementListCommitReturn = false;
        this.enableParseStatementListLparenContinue = false;
        this.enableParseRevokeFromUser = false;
        this.enableParseDropTableTables = false;
        this.enableParseCreateSql = false;
        this.enableCreateTableBodySupplemental = false;
        this.enableTableAliasConnectWhere = false;
        this.enableTableAliasAsof = false;
        this.enableTableAliasLock = false;
        this.enableTableAliasPartition = false;
        this.enableTableAliasTable = false;
        this.enableTableAliasBetween = false;
        this.enableTableAliasRest = false;
        this.enableAsCommaFrom = false;
        this.enableAsSkip = false;
        this.enableAsSequence = false;
        this.enableAsDatabase = false;
        this.enableAsDefault = false;
        this.enableAliasLiteralFloat = false;
    }

    public boolean isEnableScanSQLTypeBlockComment() {
        return enableScanSQLTypeBlockComment;
    }

    public void setEnableScanSQLTypeBlockComment(boolean enableScanSQLTypeBlockComment) {
        this.enableScanSQLTypeBlockComment = enableScanSQLTypeBlockComment;
    }

    public boolean isEnableScanSQLTypeWithSemi() {
        return enableScanSQLTypeWithSemi;
    }

    public void setEnableScanSQLTypeWithSemi(boolean enableScanSQLTypeWithSemi) {
        this.enableScanSQLTypeWithSemi = enableScanSQLTypeWithSemi;
    }

    public boolean isEnableScanSQLTypeWithFrom() {
        return enableScanSQLTypeWithFrom;
    }

    public void setEnableScanSQLTypeWithFrom(boolean enableScanSQLTypeWithFrom) {
        this.enableScanSQLTypeWithFrom = enableScanSQLTypeWithFrom;
    }

    public boolean isEnableScanSQLTypeWithFunction() {
        return enableScanSQLTypeWithFunction;
    }

    public void setEnableScanSQLTypeWithFunction(boolean enableScanSQLTypeWithFunction) {
        this.enableScanSQLTypeWithFunction = enableScanSQLTypeWithFunction;
    }

    public boolean isEnableScanSQLTypeWithBegin() {
        return enableScanSQLTypeWithBegin;
    }

    public void setEnableScanSQLTypeWithBegin(boolean enableScanSQLTypeWithBegin) {
        this.enableScanSQLTypeWithBegin = enableScanSQLTypeWithBegin;
    }

    public boolean isEnableScanSQLTypeWithAt() {
        return enableScanSQLTypeWithAt;
    }

    public void setEnableScanSQLTypeWithAt(boolean enableScanSQLTypeWithAt) {
        this.enableScanSQLTypeWithAt = enableScanSQLTypeWithAt;
    }

    public boolean isEnableNextTokenColon() {
        return enableNextTokenColon;
    }

    public void setEnableNextTokenColon(boolean enableNextTokenColon) {
        this.enableNextTokenColon = enableNextTokenColon;
    }

    public boolean isEnableNextTokenPrefixN() {
        return enableNextTokenPrefixN;
    }

    public void setEnableNextTokenPrefixN(boolean enableNextTokenPrefixN) {
        this.enableNextTokenPrefixN = enableNextTokenPrefixN;
    }

    public boolean isEnableScanString2PutDoubleBackslash() {
        return enableScanString2PutDoubleBackslash;
    }

    public void setEnableScanString2PutDoubleBackslash(boolean enableScanString2PutDoubleBackslash) {
        this.enableScanString2PutDoubleBackslash = enableScanString2PutDoubleBackslash;
    }

    public boolean isEnableScanAliasU() {
        return enableScanAliasU;
    }

    public void setEnableScanAliasU(boolean enableScanAliasU) {
        this.enableScanAliasU = enableScanAliasU;
    }

    public boolean isEnableScanNumberPrefixB() {
        return enableScanNumberPrefixB;
    }

    public void setEnableScanNumberPrefixB(boolean enableScanNumberPrefixB) {
        this.enableScanNumberPrefixB = enableScanNumberPrefixB;
    }

    public boolean isEnableScanNumberCommonProcess() {
        return enableScanNumberCommonProcess;
    }

    public void setEnableScanNumberCommonProcess(boolean enableScanNumberCommonProcess) {
        this.enableScanNumberCommonProcess = enableScanNumberCommonProcess;
    }

    public boolean isEnableScanVariableAt() {
        return enableScanVariableAt;
    }

    public void setEnableScanVariableAt(boolean enableScanVariableAt) {
        this.enableScanVariableAt = enableScanVariableAt;
    }

    public boolean isEnableScanVariableGreaterThan() {
        return enableScanVariableGreaterThan;
    }

    public void setEnableScanVariableGreaterThan(boolean enableScanVariableGreaterThan) {
        this.enableScanVariableGreaterThan = enableScanVariableGreaterThan;
    }

    public boolean isEnableScanVariableSkipIdentifiers() {
        return enableScanVariableSkipIdentifiers;
    }

    public void setEnableScanVariableSkipIdentifiers(boolean enableScanVariableSkipIdentifiers) {
        this.enableScanVariableSkipIdentifiers = enableScanVariableSkipIdentifiers;
    }

    public boolean isEnableScanVariableMoveToSemi() {
        return enableScanVariableMoveToSemi;
    }

    public void setEnableScanVariableMoveToSemi(boolean enableScanVariableMoveToSemi) {
        this.enableScanVariableMoveToSemi = enableScanVariableMoveToSemi;
    }

    public boolean isEnableScanHiveCommentDoubleSpace() {
        return enableScanHiveCommentDoubleSpace;
    }

    public void setEnableScanHiveCommentDoubleSpace(boolean enableScanHiveCommentDoubleSpace) {
        this.enableScanHiveCommentDoubleSpace = enableScanHiveCommentDoubleSpace;
    }

    public boolean isEnableAcceptUnion() {
        return enableAcceptUnion;
    }

    public void setEnableAcceptUnion(boolean enableAcceptUnion) {
        this.enableAcceptUnion = enableAcceptUnion;
    }

    public boolean isEnableQueryRestSemi() {
        return enableQueryRestSemi;
    }

    public void setEnableQueryRestSemi(boolean enableQueryRestSemi) {
        this.enableQueryRestSemi = enableQueryRestSemi;
    }

    public boolean isEnableAsofJoin() {
        return enableAsofJoin;
    }

    public void setEnableAsofJoin(boolean enableAsofJoin) {
        this.enableAsofJoin = enableAsofJoin;
    }

    public boolean isEnableGlobalJoin() {
        return enableGlobalJoin;
    }

    public void setEnableGlobalJoin(boolean enableGlobalJoin) {
        this.enableGlobalJoin = enableGlobalJoin;
    }

    public boolean isEnableJoinAt() {
        return enableJoinAt;
    }

    public void setEnableJoinAt(boolean enableJoinAt) {
        this.enableJoinAt = enableJoinAt;
    }

    public boolean isEnableJoinRightTableWith() {
        return enableJoinRightTableWith;
    }

    public void setEnableJoinRightTableWith(boolean enableJoinRightTableWith) {
        this.enableJoinRightTableWith = enableJoinRightTableWith;
    }

    public boolean isEnableJoinRightTableFrom() {
        return enableJoinRightTableFrom;
    }

    public void setEnableJoinRightTableFrom(boolean enableJoinRightTableFrom) {
        this.enableJoinRightTableFrom = enableJoinRightTableFrom;
    }

    public boolean isEnableJoinRightTableAlias() {
        return enableJoinRightTableAlias;
    }

    public void setEnableJoinRightTableAlias(boolean enableJoinRightTableAlias) {
        this.enableJoinRightTableAlias = enableJoinRightTableAlias;
    }

    public boolean isEnablePostNaturalJoin() {
        return enablePostNaturalJoin;
    }

    public void setEnablePostNaturalJoin(boolean enablePostNaturalJoin) {
        this.enablePostNaturalJoin = enablePostNaturalJoin;
    }

    public boolean isEnableMultipleJoinOn() {
        return enableMultipleJoinOn;
    }

    public void setEnableMultipleJoinOn(boolean enableMultipleJoinOn) {
        this.enableMultipleJoinOn = enableMultipleJoinOn;
    }

    public boolean isEnableUDJ() {
        return enableUDJ;
    }

    public void setEnableUDJ(boolean enableUDJ) {
        this.enableUDJ = enableUDJ;
    }

    public boolean isEnableTwoConsecutiveUnion() {
        return enableTwoConsecutiveUnion;
    }

    public void setEnableTwoConsecutiveUnion(boolean enableTwoConsecutiveUnion) {
        this.enableTwoConsecutiveUnion = enableTwoConsecutiveUnion;
    }

    public boolean isEnableQueryTable() {
        return enableQueryTable;
    }

    public void setEnableQueryTable(boolean enableQueryTable) {
        this.enableQueryTable = enableQueryTable;
    }

    public boolean isEnableGroupByAll() {
        return enableGroupByAll;
    }

    public void setEnableGroupByAll(boolean enableGroupByAll) {
        this.enableGroupByAll = enableGroupByAll;
    }

    public boolean isEnableRewriteGroupByCubeRollupToFunction() {
        return enableRewriteGroupByCubeRollupToFunction;
    }

    public void setEnableRewriteGroupByCubeRollupToFunction(boolean enableRewriteGroupByCubeRollupToFunction) {
        this.enableRewriteGroupByCubeRollupToFunction = enableRewriteGroupByCubeRollupToFunction;
    }

    public boolean isEnableGroupByPostDesc() {
        return enableGroupByPostDesc;
    }

    public void setEnableGroupByPostDesc(boolean enableGroupByPostDesc) {
        this.enableGroupByPostDesc = enableGroupByPostDesc;
    }

    public boolean isEnableGroupByItemOrder() {
        return enableGroupByItemOrder;
    }

    public void setEnableGroupByItemOrder(boolean enableGroupByItemOrder) {
        this.enableGroupByItemOrder = enableGroupByItemOrder;
    }

    public boolean isEnableSQLDateExpr() {
        return enableSQLDateExpr;
    }

    public void setEnableSQLDateExpr(boolean enableSQLDateExpr) {
        this.enableSQLDateExpr = enableSQLDateExpr;
    }

    public boolean isEnableSQLTimestampExpr() {
        return enableSQLTimestampExpr;
    }

    public void setEnableSQLTimestampExpr(boolean enableSQLTimestampExpr) {
        this.enableSQLTimestampExpr = enableSQLTimestampExpr;
    }

    public boolean isEnablePrimaryVariantColon() {
        return enablePrimaryVariantColon;
    }

    public void setEnablePrimaryVariantColon(boolean enablePrimaryVariantColon) {
        this.enablePrimaryVariantColon = enablePrimaryVariantColon;
    }

    public boolean isEnablePrimaryBangBangSupport() {
        return enablePrimaryBangBangSupport;
    }

    public void setEnablePrimaryBangBangSupport(boolean enablePrimaryBangBangSupport) {
        this.enablePrimaryBangBangSupport = enablePrimaryBangBangSupport;
    }

    public boolean isEnablePrimaryTwoConsecutiveSet() {
        return enablePrimaryTwoConsecutiveSet;
    }

    public void setEnablePrimaryTwoConsecutiveSet(boolean enablePrimaryTwoConsecutiveSet) {
        this.enablePrimaryTwoConsecutiveSet = enablePrimaryTwoConsecutiveSet;
    }

    public boolean isEnablePrimaryLbraceOdbcEscape() {
        return enablePrimaryLbraceOdbcEscape;
    }

    public void setEnablePrimaryLbraceOdbcEscape(boolean enablePrimaryLbraceOdbcEscape) {
        this.enablePrimaryLbraceOdbcEscape = enablePrimaryLbraceOdbcEscape;
    }

    public boolean isEnableParseAllIdentifier() {
        return enableParseAllIdentifier;
    }

    public void setEnableParseAllIdentifier(boolean enableParseAllIdentifier) {
        this.enableParseAllIdentifier = enableParseAllIdentifier;
    }

    public boolean isEnablePrimaryRestCommaAfterLparen() {
        return enablePrimaryRestCommaAfterLparen;
    }

    public void setEnablePrimaryRestCommaAfterLparen(boolean enablePrimaryRestCommaAfterLparen) {
        this.enablePrimaryRestCommaAfterLparen = enablePrimaryRestCommaAfterLparen;
    }

    public boolean isEnableInRestSpecificOperation() {
        return enableInRestSpecificOperation;
    }

    public void setEnableInRestSpecificOperation(boolean enableInRestSpecificOperation) {
        this.enableInRestSpecificOperation = enableInRestSpecificOperation;
    }

    public boolean isEnableAdditiveRestPipesAsConcat() {
        return enableAdditiveRestPipesAsConcat;
    }

    public void setEnableAdditiveRestPipesAsConcat(boolean enableAdditiveRestPipesAsConcat) {
        this.enableAdditiveRestPipesAsConcat = enableAdditiveRestPipesAsConcat;
    }

    public boolean isEnableParseAssignItemRparenCommaSetReturn() {
        return enableParseAssignItemRparenCommaSetReturn;
    }

    public void setEnableParseAssignItemRparenCommaSetReturn(boolean enableParseAssignItemRparenCommaSetReturn) {
        this.enableParseAssignItemRparenCommaSetReturn = enableParseAssignItemRparenCommaSetReturn;
    }

    public boolean isEnableParseAssignItemEqSemiReturn() {
        return enableParseAssignItemEqSemiReturn;
    }

    public void setEnableParseAssignItemEqSemiReturn(boolean enableParseAssignItemEqSemiReturn) {
        this.enableParseAssignItemEqSemiReturn = enableParseAssignItemEqSemiReturn;
    }

    public boolean isEnableParseAssignItemSkip() {
        return enableParseAssignItemSkip;
    }

    public void setEnableParseAssignItemSkip(boolean enableParseAssignItemSkip) {
        this.enableParseAssignItemSkip = enableParseAssignItemSkip;
    }

    public boolean isEnableParseAssignItemEqeq() {
        return enableParseAssignItemEqeq;
    }

    public void setEnableParseAssignItemEqeq(boolean enableParseAssignItemEqeq) {
        this.enableParseAssignItemEqeq = enableParseAssignItemEqeq;
    }

    public boolean isEnableParseSelectItemPrefixX() {
        return enableParseSelectItemPrefixX;
    }

    public void setEnableParseSelectItemPrefixX(boolean enableParseSelectItemPrefixX) {
        this.enableParseSelectItemPrefixX = enableParseSelectItemPrefixX;
    }

    public boolean isEnableParseLimitBy() {
        return enableParseLimitBy;
    }

    public void setEnableParseLimitBy(boolean enableParseLimitBy) {
        this.enableParseLimitBy = enableParseLimitBy;
    }

    public boolean isEnableParseStatementListWhen() {
        return enableParseStatementListWhen;
    }

    public void setEnableParseStatementListWhen(boolean enableParseStatementListWhen) {
        this.enableParseStatementListWhen = enableParseStatementListWhen;
    }

    public boolean isEnableParseStatementListSelectUnsupportedSyntax() {
        return enableParseStatementListSelectUnsupportedSyntax;
    }

    public void setEnableParseStatementListSelectUnsupportedSyntax(
            boolean enableParseStatementListSelectUnsupportedSyntax) {
        this.enableParseStatementListSelectUnsupportedSyntax = enableParseStatementListSelectUnsupportedSyntax;
    }

    public boolean isEnableParseStatementListUpdatePlanCache() {
        return enableParseStatementListUpdatePlanCache;
    }

    public void setEnableParseStatementListUpdatePlanCache(boolean enableParseStatementListUpdatePlanCache) {
        this.enableParseStatementListUpdatePlanCache = enableParseStatementListUpdatePlanCache;
    }

    public boolean isEnableParseStatementListRollbackReturn() {
        return enableParseStatementListRollbackReturn;
    }

    public void setEnableParseStatementListRollbackReturn(boolean enableParseStatementListRollbackReturn) {
        this.enableParseStatementListRollbackReturn = enableParseStatementListRollbackReturn;
    }

    public boolean isEnableParseStatementListCommitReturn() {
        return enableParseStatementListCommitReturn;
    }

    public void setEnableParseStatementListCommitReturn(boolean enableParseStatementListCommitReturn) {
        this.enableParseStatementListCommitReturn = enableParseStatementListCommitReturn;
    }

    public boolean isEnableParseStatementListLparenContinue() {
        return enableParseStatementListLparenContinue;
    }

    public void setEnableParseStatementListLparenContinue(boolean enableParseStatementListLparenContinue) {
        this.enableParseStatementListLparenContinue = enableParseStatementListLparenContinue;
    }

    public boolean isEnableParseRevokeFromUser() {
        return enableParseRevokeFromUser;
    }

    public void setEnableParseRevokeFromUser(boolean enableParseRevokeFromUser) {
        this.enableParseRevokeFromUser = enableParseRevokeFromUser;
    }

    public boolean isEnableParseDropTableTables() {
        return enableParseDropTableTables;
    }

    public void setEnableParseDropTableTables(boolean enableParseDropTableTables) {
        this.enableParseDropTableTables = enableParseDropTableTables;
    }

    public boolean isEnableParseCreateSql() {
        return enableParseCreateSql;
    }

    public void setEnableParseCreateSql(boolean enableParseCreateSql) {
        this.enableParseCreateSql = enableParseCreateSql;
    }

    public boolean isEnableCreateTableBodySupplemental() {
        return enableCreateTableBodySupplemental;
    }

    public void setEnableCreateTableBodySupplemental(boolean enableCreateTableBodySupplemental) {
        this.enableCreateTableBodySupplemental = enableCreateTableBodySupplemental;
    }

    public boolean isEnableTableAliasConnectWhere() {
        return enableTableAliasConnectWhere;
    }

    public void setEnableTableAliasConnectWhere(boolean enableTableAliasConnectWhere) {
        this.enableTableAliasConnectWhere = enableTableAliasConnectWhere;
    }

    public boolean isEnableTableAliasAsof() {
        return enableTableAliasAsof;
    }

    public void setEnableTableAliasAsof(boolean enableTableAliasAsof) {
        this.enableTableAliasAsof = enableTableAliasAsof;
    }

    public boolean isEnableTableAliasLock() {
        return enableTableAliasLock;
    }

    public void setEnableTableAliasLock(boolean enableTableAliasLock) {
        this.enableTableAliasLock = enableTableAliasLock;
    }

    public boolean isEnableTableAliasPartition() {
        return enableTableAliasPartition;
    }

    public void setEnableTableAliasPartition(boolean enableTableAliasPartition) {
        this.enableTableAliasPartition = enableTableAliasPartition;
    }

    public boolean isEnableTableAliasTable() {
        return enableTableAliasTable;
    }

    public void setEnableTableAliasTable(boolean enableTableAliasTable) {
        this.enableTableAliasTable = enableTableAliasTable;
    }

    public boolean isEnableTableAliasBetween() {
        return enableTableAliasBetween;
    }

    public void setEnableTableAliasBetween(boolean enableTableAliasBetween) {
        this.enableTableAliasBetween = enableTableAliasBetween;
    }

    public boolean isEnableTableAliasRest() {
        return enableTableAliasRest;
    }

    public void setEnableTableAliasRest(boolean enableTableAliasRest) {
        this.enableTableAliasRest = enableTableAliasRest;
    }

    public boolean isEnableAsCommaFrom() {
        return enableAsCommaFrom;
    }

    public void setEnableAsCommaFrom(boolean enableAsCommaFrom) {
        this.enableAsCommaFrom = enableAsCommaFrom;
    }

    public boolean isEnableAsSkip() {
        return enableAsSkip;
    }

    public void setEnableAsSkip(boolean enableAsSkip) {
        this.enableAsSkip = enableAsSkip;
    }

    public boolean isEnableAsSequence() {
        return enableAsSequence;
    }

    public void setEnableAsSequence(boolean enableAsSequence) {
        this.enableAsSequence = enableAsSequence;
    }

    public boolean isEnableAsDatabase() {
        return enableAsDatabase;
    }

    public void setEnableAsDatabase(boolean enableAsDatabase) {
        this.enableAsDatabase = enableAsDatabase;
    }

    public boolean isEnableAsDefault() {
        return enableAsDefault;
    }

    public void setEnableAsDefault(boolean enableAsDefault) {
        this.enableAsDefault = enableAsDefault;
    }

    public boolean isEnableAliasLiteralFloat() {
        return enableAliasLiteralFloat;
    }

    public void setEnableAliasLiteralFloat(boolean enableAliasLiteralFloat) {
        this.enableAliasLiteralFloat = enableAliasLiteralFloat;
    }
}
