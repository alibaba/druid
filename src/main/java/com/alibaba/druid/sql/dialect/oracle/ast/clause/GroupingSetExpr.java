package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class GroupingSetExpr extends SQLExprImpl {

    private static final long   serialVersionUID = 1L;
    private final List<SQLExpr> parameters       = new ArrayList<SQLExpr>();

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, parameters);
        }
        visitor.endVisit(this);
    }

}
