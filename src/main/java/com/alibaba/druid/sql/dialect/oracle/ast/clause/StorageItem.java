package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class StorageItem extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;
    private SQLName           name;
    private SQLExpr           value;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, value);
        }
        visitor.endVisit(this);
    }

}
