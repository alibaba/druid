package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLColumnTTL extends SQLConstraintImpl implements SQLColumnConstraint {
    private SQLExpr expr;

    public SQLColumnTTL(DbType dbType) {
        this.dbType = dbType;
    }
    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof SQLASTOutputVisitor) {
            SQLASTOutputVisitor sqlastOutputVisitor = (SQLASTOutputVisitor) v;
            sqlastOutputVisitor.print(" TTL ");
            expr.accept(sqlastOutputVisitor);
        }
    }

    public SQLColumnTTL clone() {
        SQLColumnTTL sqlColumnTTL = new SQLColumnTTL(dbType);
        super.cloneTo(sqlColumnTTL);
        return sqlColumnTTL;
    }
}
