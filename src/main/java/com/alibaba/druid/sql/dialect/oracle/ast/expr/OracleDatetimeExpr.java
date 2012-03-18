package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleDatetimeExpr extends OracleSQLObjectImpl implements SQLExpr {

    private static final long serialVersionUID = 1L;

    private SQLExpr           expr;
    private SQLExpr           timeZone;

    public OracleDatetimeExpr(){

    }

    public OracleDatetimeExpr(SQLExpr expr, SQLExpr timeZone){
        this.expr = expr;
        this.timeZone = timeZone;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
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

}
