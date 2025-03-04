package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public abstract class SQLTypeExpr extends SQLExprImpl implements SQLLiteralExpr, SQLValuableExpr, SQLReplaceable {
    protected SQLDataType dataType;
    protected Object value;

    public SQLTypeExpr(SQLDataType sqlDataType) {
        this.dataType = sqlDataType;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
        if (value != null) {
            value.setParent(this);
        }
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    @Override
    public abstract Object getValue();

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }
    @Override
    public SQLDataType computeDataType() {
        return dataType;
    }
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        return false;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        SQLTypeExpr other = (SQLTypeExpr) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public abstract SQLTypeExpr clone();
}
