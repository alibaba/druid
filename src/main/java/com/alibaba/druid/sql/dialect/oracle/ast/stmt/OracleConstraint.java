package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;

public abstract class OracleConstraint extends OracleSQLObject {
    private static final long serialVersionUID = 1L;

    protected OracleConstraintState state;
    protected SQLName name;

    public OracleConstraint() {

    }

    public SQLName getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null) this.name = null;
        else this.name = new SQLIdentifierExpr(name);
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public OracleConstraintState getState() {
        return this.state;
    }

    public void setState(OracleConstraintState state) {
        this.state = state;
    }
}
