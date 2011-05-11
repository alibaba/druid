package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleReferencesConstaint extends OracleConstraint {
    private static final long serialVersionUID = 1L;

    private SQLName refObject;
    private final List<SQLName> refColumns = new ArrayList<SQLName>();

    public OracleReferencesConstaint() {

    }

    public SQLName getRefObject() {
        return this.refObject;
    }

    public void setRefObject(SQLName refObject) {
        this.refObject = refObject;
    }

    public List<SQLName> getRefColumns() {
        return this.refColumns;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.name);
            acceptChild(visitor, this.refObject);
            acceptChild(visitor, this.refColumns);
            acceptChild(visitor, this.state);
        }

        visitor.endVisit(this);
    }
}
