package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

@SuppressWarnings("serial")
public class SQLUniqueConstraintImpl extends SQLConstaintImpl implements SQLUniqueConstraint {
    public SQLUniqueConstraintImpl() {

    }

    private List<SQLExpr> columns = new ArrayList<SQLExpr>();

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLExpr> getColumns() {
        return columns;
    }
}
