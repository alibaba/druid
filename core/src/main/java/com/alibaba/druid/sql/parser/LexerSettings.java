package com.alibaba.druid.sql.parser;
public class LexerSettings {
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
    public LexerSettings() {
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
}
