package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCurrentOfCursorExpr extends SQLExprImpl {
    private static final long serialVersionUID = 1L;

    private SQLName cursorName;

    public SQLCurrentOfCursorExpr() {

    }

    public SQLName getCursorName() {
        return cursorName;
    }

    public void setCursorName(SQLName cursorName) {
        this.cursorName = cursorName;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("CURRENT OF ");
        cursorName.output(buf);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.cursorName);
        }
        visitor.endVisit(this);
    }

}
