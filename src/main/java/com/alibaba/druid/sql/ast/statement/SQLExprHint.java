package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;


@SuppressWarnings("serial")
public class SQLExprHint extends SQLObjectImpl implements SQLHint {
    private SQLExpr expr;
    
    public SQLExprHint() {
        
    }
    
    public SQLExprHint(SQLExpr expr) {
        this.setExpr(expr);
    }

    public SQLExpr getExpr() {
        return expr;
    }
    
    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        
        this.expr = expr;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }
        visitor.endVisit(this);
    }
    
    
}
