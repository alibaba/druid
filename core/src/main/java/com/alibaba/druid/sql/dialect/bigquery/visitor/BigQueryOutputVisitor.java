package com.alibaba.druid.sql.dialect.bigquery.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQuerySelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class BigQueryOutputVisitor extends SQLASTOutputVisitor
        implements BigQueryVisitor {
    public BigQueryOutputVisitor(StringBuilder appender) {
        super(appender, DbType.db2);
    }

    public BigQueryOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        this.dbType = DbType.bigquery;
    }

    protected void printPartitionedBy(SQLCreateTableStatement x) {
        List<SQLColumnDefinition> partitionColumns = x.getPartitionColumns();
        int partitionSize = partitionColumns.size();
        if (partitionSize == 0) {
            return;
        }

        println();
        print0(ucase ? "PARTITION BY (" : "partition by (");
        this.indentCount++;
        println();
        for (int i = 0; i < partitionSize; ++i) {
            SQLColumnDefinition column = partitionColumns.get(i);
            printPartitoinedByColumn(column);

            if (i != partitionSize - 1) {
                print(',');
            }
            if (this.isPrettyFormat() && column.hasAfterComment()) {
                print(' ');
                printlnComment(column.getAfterCommentsDirect());
            }

            if (i != partitionSize - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        print(')');
    }

    protected void printPartitoinedByColumn(SQLColumnDefinition column) {
        String function = (String) column.getName().getAttribute("function");
        if (function != null) {
            print0(function);
            print('(');
        }
        column.accept(this);
        if (function != null) {
            print(')');
        }
    }

    protected void printCreateTableLike(SQLCreateTableStatement x) {
        SQLExprTableSource like = x.getLike();
        if (like == null) {
            return;
        }
        println();
        print0(ucase ? "CLONE " : "clone ");
        like.accept(this);
    }

    public boolean visit(BigQuerySelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    protected void printSelectListBefore(SQLSelectQueryBlock x) {
        if (x instanceof BigQuerySelectQueryBlock) {
            printSelectListBefore((BigQuerySelectQueryBlock) x);
            return;
        }

        super.printSelectListBefore(x);
    }

    protected void printSelectListBefore(BigQuerySelectQueryBlock x) {
        BigQuerySelectQueryBlock.DifferentialPrivacy privacy = x.getDifferentialPrivacy();
        if (privacy != null) {
            incrementIndent();
            println();
            privacy.accept(this);
            decrementIndent();
        } else {
            print(' ');
        }
    }

    public boolean visit(BigQuerySelectQueryBlock.DifferentialPrivacy x) {
        print0(ucase ? "WITH DIFFERENTIAL_PRIVACY" : "with differential_privacy");
        println();
        print0(ucase ? "OPTIONS (" : "options (");
        printAndAccept(x.getOptions(), ",");
        print(')');
        println();
        return false;
    }
}
