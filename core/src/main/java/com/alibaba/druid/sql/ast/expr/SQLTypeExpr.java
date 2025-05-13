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
        if (sqlDataType != null) {
            sqlDataType.setParent(this);
        }
        this.dataType = sqlDataType;
    }

    public void setValue(Object value) {
        this.value = value;
        if (value instanceof SQLExpr) {
            ((SQLExpr) value).setParent(this);
        }
    }

    public void setDataType(SQLDataType dataType) {
        if (dataType != null) {
            dataType.setParent(this);
        }
        this.dataType = dataType;
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
        if (visitor.visit(this)) {
            acceptChild(visitor, this.dataType);
            if (this.value instanceof SQLExpr) {
                acceptChild(visitor, (SQLExpr) this.value);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == this.dataType && target instanceof SQLDataType) {
            setDataType((SQLDataType) target);
            return true;
        }
        if (this.value instanceof SQLExpr && expr == this.value) {
            this.setValue(target);
            return true;
        }
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
