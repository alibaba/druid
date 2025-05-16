package com.alibaba.druid.sql.dialect.hologres.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;

public class HologresOutputVisitor extends PGOutputVisitor {
    public HologresOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.hologres;
    }

    public HologresOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.hologres;
    }

    @Override
    protected void printPartitionBy(SQLCreateTableStatement x) {
        SQLPartitionBy partitionBy = x.getPartitioning();
        if (partitionBy == null) {
            return;
        }
        println();
        if (partitionBy.getLogical()) {
            print0(ucase ? "LOGICAL " : "logical ");
        }
        print0(ucase ? "PARTITION BY " : "partition by ");
        partitionBy.accept(this);
    }
}
