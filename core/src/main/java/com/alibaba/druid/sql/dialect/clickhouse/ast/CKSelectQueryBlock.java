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
    private SQLExpr format;
    private boolean isFinal;
    private boolean withTotals;
    private boolean withFill;
    private boolean withTies;
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

    public SQLExpr getFormat() {
        return format;
    }

    public void setFormat(SQLExpr format) {
        this.format = format;
    }

    public List<SQLAssignItem> getSettings() {
        return settings;
    }

    public void setSettings(List<SQLAssignItem> x) {
        this.settings = x;
    }

    public boolean isFinal() {
        return isFinal;
    }
    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isWithTotals() {
        return withTotals;
    }

    public void setWithTotals(boolean withTotals) {
        this.withTotals = withTotals;
    }

    public boolean isWithFill() {
        return withFill;
    }

    public void setWithFill(boolean withFill) {
        this.withFill = withFill;
    }

    public boolean isWithTies() {
        return withTies;
    }

    public void setWithTies(boolean withTies) {
        this.withTies = withTies;
    }
}
