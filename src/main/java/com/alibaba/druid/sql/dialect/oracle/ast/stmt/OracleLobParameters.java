package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleLobParameters extends OracleSQLObjectImpl {
    private SQLName tableSpace;
    private Boolean enableStorageInRow;
    private SQLExpr chunk;
    private Boolean cache;
    private Boolean logging;
    private Boolean compress;
    private Boolean keepDuplicates;
    private OracleStorageClause storage;
    private SQLExpr pctVersion;

    @Override
    public void accept0(OracleASTVisitor visitor) {

    }

    public OracleStorageClause getStorage() {
        return storage;
    }

    public void setStorage(OracleStorageClause x) {
        if (x != null) {
            x.setParent(this);
        }
        this.storage = x;
    }

    public SQLName getTableSpace() {
        return tableSpace;
    }

    public void setTableSpace(SQLName tableSpace) {
        this.tableSpace = tableSpace;
    }

    public Boolean getEnableStorageInRow() {
        return enableStorageInRow;
    }

    public void setEnableStorageInRow(Boolean enableStorageInRow) {
        this.enableStorageInRow = enableStorageInRow;
    }

    public SQLExpr getChunk() {
        return chunk;
    }

    public void setChunk(SQLExpr chunk) {
        this.chunk = chunk;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getLogging() {
        return logging;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public Boolean getCompress() {
        return compress;
    }

    public void setCompress(Boolean compress) {
        this.compress = compress;
    }

    public Boolean getKeepDuplicates() {
        return keepDuplicates;
    }

    public void setKeepDuplicates(Boolean keepDuplicates) {
        this.keepDuplicates = keepDuplicates;
    }

    public SQLExpr getPctVersion() {
        return pctVersion;
    }

    public void setPctVersion(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.pctVersion = x;
    }
}
