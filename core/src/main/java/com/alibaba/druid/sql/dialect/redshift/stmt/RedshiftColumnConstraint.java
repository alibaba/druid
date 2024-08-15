package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLConstraintImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class RedshiftColumnConstraint extends SQLConstraintImpl implements SQLColumnConstraint {
    public RedshiftColumnConstraint() {
        dbType = DbType.redshift;
    }
    @Override
    protected void accept0(SQLASTVisitor v) {}

    @Override
    public RedshiftColumnConstraint clone() {
        RedshiftColumnConstraint redshiftColumnConstraint = new RedshiftColumnConstraint();
        cloneTo(redshiftColumnConstraint);
        return redshiftColumnConstraint;
    }

    public void cloneTo(RedshiftColumnConstraint x) {
        super.cloneTo(x);
        x.dbType = dbType;
    }
}
