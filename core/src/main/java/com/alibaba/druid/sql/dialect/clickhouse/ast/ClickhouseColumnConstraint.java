package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLConstraintImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class ClickhouseColumnConstraint extends SQLConstraintImpl implements SQLColumnConstraint {
    public ClickhouseColumnConstraint() {
        dbType = DbType.clickhouse;
    }
    @Override
    protected void accept0(SQLASTVisitor v) {}

    @Override
    public ClickhouseColumnConstraint clone() {
        ClickhouseColumnConstraint clickhouseColumnConstraint = new ClickhouseColumnConstraint();
        cloneTo(clickhouseColumnConstraint);
        return clickhouseColumnConstraint;
    }

    public void cloneTo(ClickhouseColumnConstraint x) {
        super.cloneTo(x);
        x.dbType = dbType;
    }
}
