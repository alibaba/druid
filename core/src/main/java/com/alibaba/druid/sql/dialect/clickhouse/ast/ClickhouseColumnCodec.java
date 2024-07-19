package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class ClickhouseColumnCodec extends ClickhouseColumnConstraint {
    private SQLExpr expr;
    public ClickhouseColumnCodec() {
        super();
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

    @Override
    public ClickhouseColumnCodec clone() {
        ClickhouseColumnCodec clickhouseColumnCodec = (ClickhouseColumnCodec) super.clone();
        clickhouseColumnCodec.setExpr(expr.clone());
        return clickhouseColumnCodec;
    }
}
