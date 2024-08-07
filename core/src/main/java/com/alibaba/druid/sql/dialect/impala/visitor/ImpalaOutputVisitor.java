package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaSQLPartitionValue;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;

import java.util.List;

public class ImpalaOutputVisitor extends HiveOutputVisitor {
    public ImpalaOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.impala;
    }

    public ImpalaOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.impala;
    }

    @Override
    protected void printJoinHint(SQLJoinTableSource x) {
        if (!x.getHints().isEmpty()) {
            print(' ');
            for (SQLHint joinHint : x.getHints()) {
                if (joinHint instanceof SQLCommentHint) {
                    print0((joinHint).toString());
                } else if (joinHint instanceof SQLExprHint) {
                    print0("[");
                    joinHint.accept(this);
                    print0("]");
                }
            }
        }
    }

    @Override
    protected void printCached(SQLCreateTableStatement x) {
        ImpalaCreateTableStatement createTable = (ImpalaCreateTableStatement) x;
        if (createTable.isCached()) {
            println();
            print0(ucase ? "CACHED IN " : "cached in ");
            createTable.getCachedPool().accept(this);
            if (createTable.getCachedReplication() != -1) {
                print0(" WITH REPLICATION = ");
                print0(String.valueOf(createTable.getCachedReplication()));
            }
        }
        if (createTable.isUnCached()) {
            println();
            print0(ucase ? "UNCACHED" : "uncached");
        }
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        printCreateTable((ImpalaCreateTableStatement) x, true, false);
        return false;
    }

    @Override
    protected void printSortedBy(List<SQLSelectOrderByItem> sortedBy) {
        if (sortedBy.size() > 0) {
            println();
            print0(ucase ? "SORT BY (" : "sort by (");
            printAndAccept(sortedBy, ", ");
            print(')');
        }
    }

    @Override
    protected void printPartitionBy(SQLCreateTableStatement x) {
        SQLPartitionBy partitionBy = x.getPartitioning();
        if (partitionBy == null) {
            return;
        }
        println();
        print0(ucase ? "PARTITION BY " : "partition by ");
        partitionBy.accept(this);
    }

    @Override
    public boolean visit(SQLPartition x) {
        ImpalaSQLPartitionValue values = (ImpalaSQLPartitionValue) x.getValues();
        values.accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLPartitionValue x) {
        ImpalaSQLPartitionValue partitionValue = (ImpalaSQLPartitionValue) x;
        print0(ucase ? " PARTITION " : " partition ");
        if (partitionValue.getOperator() == SQLPartitionValue.Operator.Equal) {
            print0(ucase ? "VALUE" : "value");
            print0(" = ");
            if (partitionValue.getItems().size() == 1) {
                // for single specific value
                printExpr(partitionValue.getItems().get(0), parameterized);
            } else {
                print("(");
                printAndAccept(partitionValue.getItems(), ", ", false);
                print(')');
            }
        } else {
            if (partitionValue.getLeftBound() != null) {
                print(partitionValue.getLeftBound());
                printOperator(partitionValue.getLeftOperator());
            }
            print0(ucase ? "VALUES" : "values");
            if (partitionValue.getRightBound() != null) {
                printOperator(partitionValue.getRightOperator());
                print(partitionValue.getRightBound());
            }
        }

        return false;
    }

    private void printOperator(SQLPartitionValue.Operator operator) {
        switch (operator) {
            case LessThan:
                print0(" < ");
                break;
            case LessThanEqual:
                print0(" <= ");
                break;
            case In:
                print0(" IN ");
                break;
            case List:
                print0(" LIST ");
                break;
            default:
                throw new IllegalArgumentException("operator not support");
        }
    }
}
