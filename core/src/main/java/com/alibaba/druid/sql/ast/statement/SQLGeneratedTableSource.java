package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLGeneratedTableSource extends SQLTableSourceImpl
        implements SQLReplaceable {
    private final List<SQLExpr> items = new ArrayList<SQLExpr>();
    protected List<SQLName> columns = new ArrayList<SQLName>();
    private SQLIdentifierExpr methodName;

    public List<SQLExpr> getItems() {
        return items;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, methodName);
            acceptChild(v, columns);
            acceptChild(v, items);
            super.accept0(v);
        }
        v.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == expr) {
                target.setParent(this);
                items.set(i, target);
                return true;
            }
        }
        if (target instanceof SQLName) {
            SQLName targetName = (SQLName) target;
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i) == expr) {
                    target.setParent(this);
                    columns.set(i, targetName);
                    return true;
                }
            }
        }
        if (target instanceof SQLIdentifierExpr) {
            if (methodName == expr) {
                target.setParent(this);
                methodName = (SQLIdentifierExpr) target;
                return true;
            }
        }
        return false;
    }

    public SQLIdentifierExpr getMethodName() {
        return methodName;
    }

    public void setMethodName(SQLIdentifierExpr methodName) {
        this.methodName = methodName;
    }
}
