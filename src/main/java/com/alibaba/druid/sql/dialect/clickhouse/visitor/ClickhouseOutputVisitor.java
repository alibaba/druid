package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class ClickhouseOutputVisitor extends SQLASTOutputVisitor implements ClickhouseVisitor {
    public ClickhouseOutputVisitor(Appendable appender) {
        super(appender, DbType.clickhouse);
    }

    public ClickhouseOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public ClickhouseOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        if (x.getExpr() != null) {
            x.getExpr().accept(this);
        } else if (x.getSubQuery() != null) {
            print('(');
            println();
            SQLSelect query = x.getSubQuery();
            if (query != null) {
                query.accept(this);
            } else {
                x.getReturningStatement().accept(this);
            }
            println();
            print(')');
        }
        print(' ');
        print0(ucase ? "AS " : "as ");
        print0(x.getAlias());

        return false;
    }

    public boolean visit(SQLStructDataType x) {
        print0(ucase ? "NESTED (" : "nested (");
        incrementIndent();
        println();
        printlnAndAccept(x.getFields(), ",");
        decrementIndent();
        println();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLStructDataType.Field x) {
        SQLName name = x.getName();
        if (name != null) {
            name.accept(this);
        }
        SQLDataType dataType = x.getDataType();

        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(ClickhouseCreateTableStatement x) {
        super.visit((SQLCreateTableStatement) x);

        SQLExpr partitionBy = x.getPartitionBy();
        if (partitionBy != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        SQLExpr sampleBy = x.getSampleBy();
        if (sampleBy != null) {
            println();
            print0(ucase ? "SAMPLE BY " : "sample by ");
            sampleBy.accept(this);
        }

        List<SQLAssignItem> settings = x.getSettings();
        if (!settings.isEmpty()) {
            println();
            print0(ucase ? "SETTINGS " : "settings ");
            printAndAccept(settings, ", ");
        }
        return false;
    }

    public boolean visit(SQLAlterTableAddColumn x) {
        print0(ucase ? "ADD COLUMN " : "add column ");
        printAndAccept(x.getColumns(), ", ");
        return false;
    }
}
