package com.alibaba.druid.sql.dialect.teradata.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLTop;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;

public class TDSelectQueryBlock extends SQLSelectQueryBlock {
    private SQLTop top;
    private boolean withDeletedRows;
    private boolean asJson;
    private TDNormalize normalize;

    public TDSelectQueryBlock(DbType dbType) {
        super(dbType);
        withDeletedRows = false;
        asJson = false;
    }

    public SQLTop getTop() {
        return top;
    }

    public void setTop(SQLTop top) {
        if (top != null) {
            top.setParent(this);
        }
        this.top = top;
    }

    public boolean isWithDeletedRows() {
        return withDeletedRows;
    }

    public void setWithDeletedRows(boolean withDeletedRows) {
        this.withDeletedRows = withDeletedRows;
    }

    public boolean isAsJson() {
        return asJson;
    }

    public void setAsJson(boolean asJson) {
        this.asJson = asJson;
    }

    public TDNormalize getNormalize() {
        return normalize;
    }

    public void setNormalize(TDNormalize normalize) {
        if (normalize != null) {
            normalize.setParent(this);
        }
        this.normalize = normalize;
    }
}
