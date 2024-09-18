package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLColumnDefault extends SQLConstraintImpl implements SQLColumnConstraint, SQLReplaceable {
    private SQLExpr defaultExpr;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, this.getDefaultExpr());
        }
        visitor.endVisit(this);
    }

    public SQLColumnDefault clone() {
        SQLColumnDefault x = new SQLColumnDefault();

        super.cloneTo(x);

        if (defaultExpr != null) {
            x.setDefaultExpr(defaultExpr.clone());
        }

        return x;
    }

    public void setDefaultExpr(SQLExpr defaultExpr) {
        this.defaultExpr = defaultExpr;
    }

    public SQLExpr getDefaultExpr() {
        return defaultExpr;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.defaultExpr == expr) {
            setDefaultExpr(target);
            return true;
        }

        if (getName() == expr) {
            setName((SQLName) target);
            return true;
        }

        if (getComment() == expr) {
            setComment(target);
            return true;
        }
        return false;
    }
}
