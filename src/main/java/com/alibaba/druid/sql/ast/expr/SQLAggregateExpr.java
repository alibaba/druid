package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAggregateExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    protected SQLIdentifierExpr methodName;
    protected int option;
    protected final List<SQLExpr> arguments = new ArrayList<SQLExpr>();

    public SQLAggregateExpr(String methodName) {

        this.methodName = new SQLIdentifierExpr(methodName);
        this.option = 1;
    }

    public SQLAggregateExpr(String methodName, int option) {

        this.methodName = new SQLIdentifierExpr(methodName);
        this.option = option;
    }

    public SQLIdentifierExpr getMethodName() {
        return this.methodName;
    }

    public void setMethodName(SQLIdentifierExpr methodName) {
        this.methodName = methodName;
    }

    public int getOption() {
        return this.option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public List<SQLExpr> getArguments() {
        return this.arguments;
    }

    public void output(StringBuffer buf) {
        buf.append(this.methodName);
        buf.append("(");
        int i = 0;
        for (int size = this.arguments.size(); i < size; ++i) {
            ((SQLExpr) this.arguments.get(i)).output(buf);
        }
        buf.append(")");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.methodName);
            acceptChild(visitor, this.arguments);
        }

        visitor.endVisit(this);
    }
}
