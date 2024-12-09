package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaSQLPartitionValue;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaInsertStatement;

import java.util.List;

public class ImpalaOutputVisitor extends HiveOutputVisitor implements ImpalaASTVisitor {
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
            printHints(x.getHints());
        }
    }

    @Override
    protected void printCached(SQLCreateTableStatement x) {
        if (x instanceof ImpalaCreateTableStatement) {
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
    }

    @Override
    public boolean visit(ImpalaCreateTableStatement x) {
        printCreateTable(x, true, false);
        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        if (x instanceof ImpalaCreateTableStatement) {
            return visit((ImpalaCreateTableStatement) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        if (x instanceof ImpalaInsertStatement) {
            return visit((ImpalaInsertStatement) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(ImpalaInsertStatement x) {
        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            visit(with);
            println();
        }

        if (x.isUpsert()) {
            print0(ucase ? "UPSERT " : "upsert ");
            printHint(x, true);
            print0(ucase ? "INTO " : "into ");
        } else {
            print0(ucase ? "INSERT " : "insert ");
            printHint(x, true);
            if (x.isOverwrite()) {
                print0(ucase ? "OVERWRITE " : "overwrite ");
            } else {
                print0(ucase ? "INTO " : "into ");
            }
        }

        x.getTableSource().accept(this);

        String columnsString = x.getColumnsString();
        if (columnsString != null) {
            print0(columnsString);
        } else {
            printInsertColumns(x.getColumns());
        }

        if (!x.getValuesList().isEmpty()) {
            println();
            print0(ucase ? "VALUES " : "values ");
            printAndAccept(x.getValuesList(), ", ");
        } else {
            if (x.getQuery() != null) {
                println();
                printHint(x, false);
                x.getQuery().accept(this);
            }
        }

        return false;
    }

    private void printHint(ImpalaInsertStatement x, boolean isInsert) {
        List<SQLHint> hints = isInsert ? x.getInsertHints() : x.getSelectHints();
        if (!hints.isEmpty()) {
            printHints(hints);
            print(' ');
        }
    }

    private void printHints(List<SQLHint> hints) {
        for (SQLHint hint : hints) {
            if (hint instanceof SQLCommentHint) {
                print0((hint).toString());
            } else if (hint instanceof SQLExprHint) {
                print0("[");
                hint.accept(this);
                print0("]");
            }
        }
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
    public boolean visit(SQLPartitionSingle x) {
        ImpalaSQLPartitionValue values = (ImpalaSQLPartitionValue) x.getValues();
        values.accept(this);
        return false;
    }

    protected void printEncoding(SQLColumnDefinition x) {
        if (x.getEncode() != null) {
            print0(ucase ? " ENCODING " : " encoding ");
            x.getEncode().accept(this);
        }
    }

    protected void printCompression(SQLColumnDefinition x) {
        if (x.getCompression() != null) {
            print0(ucase ? " COMPRESSION " : " compression ");
            x.getCompression().accept(this);
        }
    }

    @Override
    public boolean visit(SQLPartitionValue x) {
        if (x instanceof ImpalaSQLPartitionValue) {
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

    @Override
    public void printSqlSetQuantifier(SQLSelectQueryBlock x) {
        final int distinctOption = x.getDistionOption();
        if (SQLSetQuantifier.STRAIGHT_JOIN == distinctOption) {
            print0(ucase ? "STRAIGHT_JOIN " : "straight_join ");
        } else {
            super.printSqlSetQuantifier(x);
        }
    }
}
