package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLConstaint;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterTableAddConstaint extends OracleAlterTableItem {

    private static final long serialVersionUID = 1L;

    private SQLConstaint      constraint;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, constraint);
        }
        visitor.endVisit(this);
    }

    public SQLConstaint getConstraint() {
        return constraint;
    }

    public void setConstraint(SQLConstaint constraint) {
        this.constraint = constraint;
    }

}
