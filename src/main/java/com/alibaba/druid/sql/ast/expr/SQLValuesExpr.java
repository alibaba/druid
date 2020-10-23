package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLValuesExpr extends SQLExprImpl implements SQLReplaceable {
    private List<SQLListExpr> values = new ArrayList<SQLListExpr>();

    public List<SQLListExpr> getValues() {
        return values;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, values);
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLValuesExpr that = (SQLValuesExpr) o;

        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public SQLExpr clone() {
        SQLValuesExpr x = new SQLValuesExpr();

        for (SQLListExpr value : values) {
            SQLListExpr value2 = value.clone();
            value2.setParent(x);
            x.values.add(value2);
        }

        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == expr) {
                target.setParent(this);
                values.set(i, (SQLListExpr) target);
                return true;
            }
        }
        return false;
    }

    @Override
    public List getChildren() {
        return values;
    }
}
