package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCommentStatement extends SQLObjectImpl implements SQLStatement {

    private static final long serialVersionUID = 1L;

    public static enum Type {
        TABLE, COLUMN
    }

    private SQLExpr on;
    private Type    type;
    private SQLExpr comment;

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        this.comment = comment;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SQLExpr getOn() {
        return on;
    }

    public void setOn(SQLExpr on) {
        this.on = on;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, on);
            acceptChild(visitor, comment);
        }
        visitor.endVisit(this);
    }

}
