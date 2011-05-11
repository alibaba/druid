package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleTableTypeDef extends OracleTypeDef {
    private static final long serialVersionUID = 1L;

    private boolean ref = false;
    private SQLName name;
    private boolean type = false;

    private boolean notNull = false;

    public OracleTableTypeDef() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }

        visitor.endVisit(this);
    }

    public boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public SQLName getName() {
        return this.name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public boolean isType() {
        return this.type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isRef() {
        return this.ref;
    }

    public void setRef(boolean ref) {
        this.ref = ref;
    }

    public void output(StringBuffer buf) {
        if (this.ref) {
            buf.append("REF ");
        }

        buf.append(super.toString());
    }
}
