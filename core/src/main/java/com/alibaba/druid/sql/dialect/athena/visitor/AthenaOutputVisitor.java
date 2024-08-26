package com.alibaba.druid.sql.dialect.athena.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitionOf;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.athena.ast.stmt.AthenaCreateTableStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoOutputVisitor;

public class AthenaOutputVisitor extends PrestoOutputVisitor implements AthenaASTVisitor {
    public AthenaOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.athena;
    }

    public AthenaOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.athena;
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
        printIntoBuckets(x);
        printRowFormat(x);
        printStoredAs(x);
        printSerdeProperties(x.getSerdeProperties());
        printLocation(x);
    }
    public boolean visit(SQLCreateTableStatement x) {
        printCreateTable(x, false);

        SQLPartitionOf partitionOf = x.getPartitionOf();
        if (partitionOf != null) {
            println();
            print0(ucase ? "PARTITION OF " : "partition of ");
            partitionOf.accept(this);
        }
        printEngine(x);
        printPartitionBy(x);
        printTableOptions(x);

        SQLName tablespace = x.getTablespace();
        if (tablespace != null) {
            println();
            print0(ucase ? "TABLESPACE " : "tablespace ");
            tablespace.accept(this);
        }
        SQLSelect select = x.getSelect();
        if (select != null) {
            println();
            print0(ucase ? "AS" : "as");

            println();
            visit(select);
        }

        return false;
    }

    @Override
    protected void printTableOptionsPrefix(SQLCreateTableStatement x) {
        println();
        print0(ucase ? "TBLPROPERTIES (" : "tblproperties (");
        incrementIndent();
        println();
    }

    protected void printStoredAs(SQLCreateTableStatement x) {
        SQLExpr storedAs = x.getStoredAs();
        if (storedAs == null) {
            return;
        }
        print0(ucase ? "STORE AS " : "store as ");
        printExpr(storedAs, parameterized);
    }
}
