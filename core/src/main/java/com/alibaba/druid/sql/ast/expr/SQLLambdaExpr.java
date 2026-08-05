package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLLambdaExpr extends SQLExprImpl {
    private List<SQLExpr> arguments = new ArrayList<SQLExpr>();
    private SQLExpr expr;

    public SQLLambdaExpr() {
    }

    public List<SQLExpr> getArguments() {
        return arguments;
    }

    public void addArgument(SQLExpr argument) {
        if (argument != null) {
            argument.setParent(this);
        }
        this.arguments.add(argument);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, arguments);
            acceptChild(visitor, expr);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        children.addAll(this.arguments);
        if (this.expr != null) {
            children.add(this.expr);
        }
        return children;
    }

    @Override
    public SQLLambdaExpr clone() {
        SQLLambdaExpr x = new SQLLambdaExpr();
        for (SQLExpr arg : arguments) {
            SQLExpr arg2 = arg.clone();
            arg2.setParent(x);
            x.arguments.add(arg2);
        }
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        return x;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLLambdaExpr other = (SQLLambdaExpr) obj;
        if (arguments == null) {
            if (other.arguments != null) {
                return false;
            }
        } else if (!arguments.equals(other.arguments)) {
            return false;
        }
        if (expr == null) {
            if (other.expr != null) {
                return false;
            }
        } else if (!expr.equals(other.expr)) {
            return false;
        }
        return true;
    }
}
