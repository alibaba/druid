package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleErrorLoggingClause extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;
    private SQLName           into;
    private SQLExpr           simpleExpression;
    private SQLExpr           limit;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, into);
            acceptChild(visitor, simpleExpression);
            acceptChild(visitor, limit);
        }
        visitor.endVisit(this);
    }

    public SQLName getInto() {
        return into;
    }

    public void setInto(SQLName into) {
        this.into = into;
    }

    public SQLExpr getSimpleExpression() {
        return simpleExpression;
    }

    public void setSimpleExpression(SQLExpr simpleExpression) {
        this.simpleExpression = simpleExpression;
    }

    public SQLExpr getLimit() {
        return limit;
    }

    public void setLimit(SQLExpr limit) {
        this.limit = limit;
    }

}