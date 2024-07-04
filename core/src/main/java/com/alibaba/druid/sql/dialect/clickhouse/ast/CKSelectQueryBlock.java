package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.util.ArrayList;
import java.util.List;

public class CKSelectQueryBlock extends SQLSelectQueryBlock {
    {
        dbType = DbType.clickhouse;
    }

    private SQLExpr preWhere;
    private SQLExpr sample;
    private List<SQLAssignItem> settings = new ArrayList<>();

    public SQLExpr getPreWhere() {
        return preWhere;
    }

    public void setPreWhere(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.preWhere = x;
    }

    public SQLExpr getSample() {
        return sample;
    }

    public void setSample(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.sample = x;
    }

    public List<SQLAssignItem> getSettings() {
        return settings;
    }

    public void setSettings(List<SQLAssignItem> x) {
        this.settings = x;
    }
}
