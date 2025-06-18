package com.alibaba.druid.sql.dialect.synapse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.dialect.synapse.ast.stmt.SynapseCreateTableStatement;

public class SynapseOutputVisitor extends SQLServerOutputVisitor implements SynapseASTVisitor {
    public SynapseOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.synapse;
    }

    public SynapseOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.synapse;
    }

    @Override
    public boolean visit(SynapseCreateTableStatement x) {
        print0(ucase ? "CREATE TABLE " : "create table ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printTableSourceExpr(x.getTableSource().getExpr());

        printTableElements(x.getTableElementList());

        if (x.getSelect() != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            x.getSelect().accept(this);
        }

        return false;
    }
}
