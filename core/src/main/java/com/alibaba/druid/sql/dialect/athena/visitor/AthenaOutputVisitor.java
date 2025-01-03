package com.alibaba.druid.sql.dialect.athena.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.athena.ast.stmt.AthenaCreateTableStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoOutputVisitor;

public class AthenaOutputVisitor extends PrestoOutputVisitor implements AthenaASTVisitor {
    public AthenaOutputVisitor(StringBuilder appender) {
        super(appender, DbType.athena);
    }

    public AthenaOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.athena, parameterized);
    }

    protected void printCreateTable(SQLCreateTableStatement x, boolean printSelect) {
        printCreateTable((AthenaCreateTableStatement) x, printSelect);
    }
    protected void printCreateTable(AthenaCreateTableStatement x, boolean printSelect) {
        print0(ucase ? "CREATE " : "create ");
        printCreateTableFeatures(x);
        print0(ucase ? "TABLE " : "table ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        printTableSourceExpr(x.getTableSource().getExpr());
        printCreateTableAfterName(x);
        printTableElements(x.getTableElementList());
        printComment(x.getComment());
        printPartitionedBy(x);
        printClusteredBy(x);
        printIntoBuckets(x.getBuckets());
        printRowFormat(x);
        printStoredAs(x);
        printSerdeProperties(x.getSerdeProperties());
        printLocation(x);
    }

    @Override
    protected void printTableOptionsPrefix(SQLCreateTableStatement x) {
        println();
        print0(ucase ? "TBLPROPERTIES (" : "tblproperties (");
        incrementIndent();
        println();
    }
}
