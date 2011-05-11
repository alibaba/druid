package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleConstraintState extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    private final List<SQLObject> states = new ArrayList<SQLObject>();

    public OracleConstraintState() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.states);
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getStates() {
        return this.states;
    }
}
