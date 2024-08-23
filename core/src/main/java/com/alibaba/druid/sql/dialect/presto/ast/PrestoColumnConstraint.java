package com.alibaba.druid.sql.dialect.presto.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLConstraintImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PrestoColumnConstraint extends SQLConstraintImpl implements SQLColumnConstraint {
    public PrestoColumnConstraint() {
        dbType = DbType.presto;
    }
    @Override
    protected void accept0(SQLASTVisitor v) {}

    @Override
    public PrestoColumnConstraint clone() {
        PrestoColumnConstraint prestoColumnConstraint = new PrestoColumnConstraint();
        cloneTo(prestoColumnConstraint);
        return prestoColumnConstraint;
    }

    public void cloneTo(PrestoColumnConstraint x) {
        super.cloneTo(x);
        x.dbType = dbType;
    }
}
