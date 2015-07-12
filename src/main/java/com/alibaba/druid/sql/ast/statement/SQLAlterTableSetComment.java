package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableSetComment extends SQLObjectImpl implements SQLAlterTableItem {

    private SQLExpr comment;

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, comment);
        }
        visitor.endVisit(this);
    }

}
