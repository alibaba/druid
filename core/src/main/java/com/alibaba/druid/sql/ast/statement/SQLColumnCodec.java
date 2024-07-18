package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLColumnCodec extends SQLConstraintImpl implements SQLColumnConstraint {
    private SQLExpr expr;
    public SQLColumnCodec(DbType dbType) {
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
            sqlastOutputVisitor.print("CODEC(");
            expr.accept(sqlastOutputVisitor);
            sqlastOutputVisitor.print(")");
        }
    }

    public SQLColumnCodec clone() {
        SQLColumnCodec sqlColumnCodec = new SQLColumnCodec(dbType);
        super.cloneTo(sqlColumnCodec);
        return sqlColumnCodec;
    }
}
