package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by tianzhen.wtz on 2014/12/29 0029 16:10.
 * 类说明：
 */
public class PGIntervalExpr extends SQLExprImpl implements SQLLiteralExpr,PGExpr{

    private SQLExpr value;


    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PGIntervalExpr that = (PGIntervalExpr) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((PGASTVisitor) visitor);
    }
}
