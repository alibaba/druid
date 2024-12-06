package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Objects;

public class SQLAtTimeZoneExpr extends SQLExprImpl {
    private SQLExpr expr;
    private SQLExpr timeZone;

    public SQLAtTimeZoneExpr() {
    }

    public SQLAtTimeZoneExpr(SQLExpr expr, SQLExpr timeZone) {
        this.expr = expr;
        this.timeZone = timeZone;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
            acceptChild(visitor, timeZone);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public SQLExpr getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(SQLExpr timeZone) {
        this.timeZone = timeZone;
    }

    public SQLAtTimeZoneExpr clone() {
        SQLAtTimeZoneExpr x = new SQLAtTimeZoneExpr();

        if (expr != null) {
            x.setExpr(expr.clone());
        }

        if (timeZone != null) {
            x.setTimeZone(timeZone.clone());
        }

        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLAtTimeZoneExpr that = (SQLAtTimeZoneExpr) o;
        return Objects.equals(expr, that.expr) && Objects.equals(timeZone, that.timeZone);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(expr);
        result = 31 * result + Objects.hashCode(timeZone);
        return result;
    }
}
