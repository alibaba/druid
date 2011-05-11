package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleAnalytic extends SQLObjectImpl {
    private final List<SQLExpr> partitionBy = new ArrayList<SQLExpr>();
    private OracleOrderBy orderBy;
    private OracleAnalyticWindowing windowing;

    public OracleAnalytic() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.windowing);
        }
        visitor.endVisit(this);
    }

    public OracleOrderBy getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(OracleOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public OracleAnalyticWindowing getWindowing() {
        return this.windowing;
    }

    public void setWindowing(OracleAnalyticWindowing windowing) {
        this.windowing = windowing;
    }

    public List<SQLExpr> getPartitionBy() {
        return this.partitionBy;
    }
}
