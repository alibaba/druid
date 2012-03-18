package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterTablespaceStatement extends OracleStatementImpl {

    private static final long         serialVersionUID = 1L;

    private SQLName                   name;
    private OracleAlterTablespaceItem item;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, item);
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public OracleAlterTablespaceItem getItem() {
        return item;
    }

    public void setItem(OracleAlterTablespaceItem item) {
        this.item = item;
    }

}
