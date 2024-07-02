package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKAlterTableUpdateStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKCreateTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class CKOutputVisitor extends SQLASTOutputVisitor implements CKVisitor {
    public CKOutputVisitor(StringBuilder appender) {
        super(appender, DbType.clickhouse);
    }

    public CKOutputVisitor(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
    }

    public CKOutputVisitor(StringBuilder appender, boolean parameterized) {
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
    public boolean visit(CKCreateTableStatement x) {
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

    @Override
    public boolean visit(SQLArrayDataType x) {
        final List<SQLExpr> arguments = x.getArguments();
        if (Boolean.TRUE.equals(x.getAttribute("ads.arrayDataType"))) {
            x.getComponentType().accept(this);
            print('[');
            printAndAccept(arguments, ", ");
            print(']');
        } else {
            SQLDataType componentType = x.getComponentType();
            if (componentType != null) {
                print0(ucase ? "Array<" : "array<");
                componentType.accept(this);
                print('>');
            } else {
                print0(ucase ? "Array" : "array");
            }

            if (arguments.size() > 0) {
                print('(');
                printAndAccept(arguments, ", ");
                print(')');
            }
        }
        return false;
    }

    @Override
    public boolean visit(CKAlterTableUpdateStatement x) {
        print0(ucase ? "ALTER TABLE " : "alter table ");
        printExpr(x.getTableName());
        if (x.getClusterName() != null) {
            print0(ucase ? " ON CLUSTER " : " on cluster ");
            if (parameterized) {
                print('?');
            } else {
                printExpr(x.getClusterName());
            }
        }
        print0(ucase ? " UPDATE " : " update ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLUpdateSetItem item = x.getItems().get(i);
            visit(item);
        }
        if (x.getPartitionId() != null) {
            print0(ucase ? " IN PARTITION " : " in partition ");
            if (parameterized) {
                print('?');
            } else {
                printExpr(x.getPartitionId());
            }
        }
        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    protected void printWhere(SQLSelectQueryBlock queryBlock) {
        if (queryBlock instanceof CKSelectQueryBlock) {
            SQLExpr preWhere = ((CKSelectQueryBlock) queryBlock).getPreWhere();
            if (preWhere != null) {
                println();
                print0(ucase ? "PREWHERE " : "prewhere ");
                printExpr(preWhere);
            }
        }

        SQLExpr where = queryBlock.getWhere();
        if (where == null) {
            return;
        }

        println();
        print0(ucase ? "WHERE " : "where ");

        List<String> beforeComments = where.getBeforeCommentsDirect();
        if (beforeComments != null && !beforeComments.isEmpty() && isPrettyFormat()) {
            printlnComments(beforeComments);
        }
        printExpr(where, parameterized);
    }
}
