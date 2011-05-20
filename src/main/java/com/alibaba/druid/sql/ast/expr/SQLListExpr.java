package com.alibaba.druid.sql.ast.expr;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLListExpr extends SQLExprImpl {

    private static final long   serialVersionUID = 1L;
    
    private final List<SQLExpr> items            = new ArrayList<SQLExpr>();

    public List<SQLExpr> getItems() {
        return items;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }
}
