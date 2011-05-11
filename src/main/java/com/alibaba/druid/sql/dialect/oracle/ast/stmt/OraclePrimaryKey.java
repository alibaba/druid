package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OraclePrimaryKey extends OracleConstraint {
    private static final long serialVersionUID = 1L;

    private final List<SQLName> columns = new ArrayList<SQLName>();

    public OraclePrimaryKey() {

    }

    public List<SQLName> getColumns() {
        return this.columns;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.name);
            acceptChild(visitor, this.columns);
            acceptChild(visitor, this.state);
        }

        visitor.endVisit(this);
    }
}
