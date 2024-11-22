package com.alibaba.druid.sql.dialect.gaussdb.ast;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.gaussdb.visitor.GaussDbASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class GaussDbDistributeBy extends GaussDbObjectImpl {
    protected SQLSubPartitionBy subPartitionBy;
    protected List<SQLPartition> distributions = new ArrayList<>();
    protected List<SQLName> storeIn = new ArrayList<SQLName>();
    protected List<SQLExpr> columns = new ArrayList<SQLExpr>();
    protected SQLName type;

    protected SQLIntegerExpr lifeCycle;

    public List<SQLPartition> getDistributions() {
        return distributions;
    }

    public void setType(SQLName type) {
        this.type = type;
    }

    public SQLName getType() {
        return type;
    }

    public void addDistribution(SQLPartitionSingle distribution) {
        if (distribution != null) {
            distribution.setParent(this);
        }
        this.distributions.add(distribution);
    }

    public SQLSubPartitionBy getSubPartitionBy() {
        return subPartitionBy;
    }

    public void setSubPartitionBy(SQLSubPartitionBy subPartitionBy) {
        if (subPartitionBy != null) {
            subPartitionBy.setParent(this);
        }
        this.subPartitionBy = subPartitionBy;
    }

    public List<SQLName> getStoreIn() {
        return storeIn;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public void addColumn(SQLExpr column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public boolean isDistributionColumn(long columnNameHashCode64) {
        for (SQLExpr column : columns) {
            if (column instanceof SQLIdentifierExpr
                    && ((SQLIdentifierExpr) column)
                    .nameHashCode64() == columnNameHashCode64) {
                return true;
            }
        }

        if (subPartitionBy != null) {
            return subPartitionBy.isPartitionByColumn(columnNameHashCode64);
        }
        return false;
    }

    public SQLIntegerExpr getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(SQLIntegerExpr x) {
        this.lifeCycle = x;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof GaussDbASTVisitor) {
            GaussDbASTVisitor vv = (GaussDbASTVisitor) v;
            if (vv.visit(this)) {
                acceptChild(v, this.columns);
                acceptChild(v, this.distributions);
                acceptChild(v, this.storeIn);
                acceptChild(v, this.lifeCycle);
                acceptChild(v, this.type);
                acceptChild(v, this.subPartitionBy);
            }
        }
    }
}
