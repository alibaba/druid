package com.alibaba.druid.sql.dialect.synapse.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.synapse.visitor.SynapseASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SynapseCreateTableStatement extends SQLCreateTableStatement {
    private SQLExpr distribution;
    private List<SQLExpr> clusteredIndexColumns = new ArrayList<>();
    private boolean isDistributionHash;

    public SynapseCreateTableStatement() {
        super();
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SynapseASTVisitor) {
            accept0((SynapseASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(SynapseASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getTableSource());
            acceptChild(visitor, getTableElementList());
            acceptChild(visitor, getInherits());
            acceptChild(visitor, getSelect());
        }
        visitor.endVisit(this);
    }

    public SQLExpr getDistribution() {
        return distribution;
    }

    public void setDistribution(SQLExpr distribution) {
        this.distribution = distribution;
    }

    public List<SQLExpr> getClusteredIndexColumns() {
        return clusteredIndexColumns;
    }

    public void setClusteredIndexColumns(List<SQLExpr> clusteredIndexColumns) {
        this.clusteredIndexColumns = clusteredIndexColumns;
    }

    public boolean isDistributionHash() {
        return isDistributionHash;
    }

    public void setDistributionHash(boolean distributionHash) {
        isDistributionHash = distributionHash;
    }
}
