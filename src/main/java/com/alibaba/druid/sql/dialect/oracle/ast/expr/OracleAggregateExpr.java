package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleAggregateExpr extends SQLAggregateExpr implements Serializable {
    private static final long serialVersionUID = 1L;
    private OracleAnalytic over;
    private boolean ignoreNulls = false;

    public boolean isIgnoreNulls() {
        return this.ignoreNulls;
    }

    public void setIgnoreNulls(boolean ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
    }

    public OracleAggregateExpr(String methodName) {
        super(methodName);
    }

    public OracleAggregateExpr(String methodName, int option) {
        super(methodName, option);
    }

    public SQLIdentifierExpr getMethodName() {
        return this.methodName;
    }

    public OracleAnalytic getOver() {
        return this.over;
    }

    public void setOver(OracleAnalytic over) {
        this.over = over;
    }

    public void output(StringBuffer buf) {
        buf.append(this.methodName);
        buf.append("(");
        int i = 0;
        for (int size = this.arguments.size(); i < size; ++i) {
            ((SQLExpr) this.arguments.get(i)).output(buf);
        }
        buf.append(")");

        if (this.over != null) {
            buf.append(" ");
            this.over.output(buf);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.methodName);
            acceptChild(visitor, this.arguments);
            acceptChild(visitor, this.over);
        }
        visitor.endVisit(this);
    }
}
