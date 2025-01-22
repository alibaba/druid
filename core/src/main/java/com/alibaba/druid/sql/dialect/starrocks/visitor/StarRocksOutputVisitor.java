package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.DistributedByType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksAggregateKey;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksDuplicateKey;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksIndexDefinition;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateResourceStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcUtils;

import java.util.List;
import java.util.Locale;

public class StarRocksOutputVisitor extends SQLASTOutputVisitor implements StarRocksASTVisitor {
    {
        this.shardingSupport = true;
        this.quote = '`';
    }

    public StarRocksOutputVisitor(StringBuilder appender) {
        super(appender, DbType.starrocks);
    }

    public StarRocksOutputVisitor(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
    }

    public StarRocksOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.starrocks, parameterized);
    }

    @Override
    protected void printEngine(SQLCreateTableStatement x) {
        if (x instanceof StarRocksCreateTableStatement) {
            SQLExpr engine = ((StarRocksCreateTableStatement) x).getEngine();
            if (engine != null) {
                print0(ucase ? " ENGINE = " : " engine = ");
                engine.accept(this);
            }
        }
    }

    public boolean visit(StarRocksCreateTableStatement x) {
        printCreateTable(x, false);
        printEngine(x);
        printUniqueKey(x);
        printComment(x.getComment());
        printPartitionBy(x);
        printDistributedBy(x);
        printOrderBy(x);
        printTableOptions(x);
        printSelectAs(x, true);
        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        if (x instanceof StarRocksCreateTableStatement) {
            return visit((StarRocksCreateTableStatement) x);
        } else {
            return super.visit(x);
        }
    }
    protected void printCreateTable(SQLCreateTableStatement x, boolean printSelect) {
        print0(ucase ? "CREATE " : "create ");
        printCreateTableFeatures(x);
        print0(ucase ? "TABLE " : "table ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printTableSourceExpr(
                x.getTableSource()
                        .getExpr());

        printCreateTableAfterName(x);
        printTableElements(x.getTableElementList());
    }
    protected void printUniqueKey(SQLCreateTableStatement x) {
        if (x.getUnique() != null) {
            println();
            if (x.getUnique() instanceof StarRocksAggregateKey) {
                print0(ucase ? "AGGREGATE KEY (" : "aggregate key (");
            } else if (x.getUnique() instanceof StarRocksDuplicateKey) {
                print0(ucase ? "DUPLICATE KEY (" : "duplicate key (");
            } else if (x.getUnique() instanceof SQLPrimaryKeyImpl) {
                print0(ucase ? "PRIMARY KEY (" : "primary key (");
            } else {
                print0(ucase ? "UNIQUE KEY (" : "unique key (");
            }
            printAndAccept(x.getUnique().getColumns(), ", ");
            print0(")");
        }
    }

    protected void printDistributedBy(SQLCreateTableStatement x) {
        if (x instanceof StarRocksCreateTableStatement) {
            StarRocksCreateTableStatement createTable = (StarRocksCreateTableStatement) x;
            if (createTable.getDistributedByType() != null) {
                println();
                print0(ucase ? "DISTRIBUTED BY " : "distributed by ");
                DistributedByType distributedByType = createTable.getDistributedByType();
                if (DistributedByType.Random.equals(distributedByType)) {
                    print0(ucase ? "RANDOM BUCKETS" : "random buckets");
                    if (createTable.getBuckets() > 0) {
                        print0(" ");
                        print0(String.valueOf(createTable.getBuckets()));
                    }
                } else if (DistributedByType.Hash.equals(distributedByType) && !createTable.getDistributedBy().isEmpty()) {
                    print0(ucase ? "HASH (" : "hash (");
                    printAndAccept(createTable.getDistributedBy(), ", ");
                    print0(")");
                    if (createTable.getBuckets() > 0) {
                        print0(ucase ? " BUCKETS " : " buckets ");
                        print0(String.valueOf(createTable.getBuckets()));
                    }
                }
            }
        }
    }

    protected void printOrderBy(SQLCreateTableStatement x) {
        if (x instanceof StarRocksCreateTableStatement) {
            StarRocksCreateTableStatement createTable = (StarRocksCreateTableStatement) x;
            printOrderBy(createTable.getOrderBy());
        }
    }

    @Override
    protected void printTableOptionsPrefix(SQLCreateTableStatement x) {
        println();
        print0(ucase ? "PROPERTIES (" : "properties (");
        incrementIndent();
        println();
    }
    @Override
    protected void printTableOptions(SQLCreateTableStatement statement) {
        super.printTableOptions(statement);
        if (statement instanceof StarRocksCreateTableStatement) {
            StarRocksCreateTableStatement x = (StarRocksCreateTableStatement) statement;
            if (!x.getBrokerProperties().isEmpty()) {
                println();
                print0(ucase ? "BROKER PROPERTIES (" : "broker properties (");
                incrementIndent();
                println();
                int i = 0;
                for (SQLAssignItem property : x.getBrokerProperties()) {
                    printTableOption(property.getTarget(), property.getValue(), i);
                    ++i;
                }
                decrementIndent();
                println();
                print0(")");
            }
        }
    }

    protected void print(List<? extends SQLExpr> exprList) {
        int size = exprList.size();
        if (size == 0) {
            return;
        }

        print0(" (");

        this.indentCount++;
        println();
        for (int i = 0; i < size; ++i) {
            SQLExpr element = exprList.get(i);

            if (element instanceof SQLArrayExpr) {
                SQLArrayExpr array = ((SQLArrayExpr) element);
                SQLExpr expr = array.getExpr();

                if (expr instanceof SQLIdentifierExpr
                        && ((SQLIdentifierExpr) expr).nameHashCode64() == FnvHash.Constants.ARRAY
                        && printNameQuote
                ) {
                    print0(((SQLIdentifierExpr) expr).getName());
                } else if (expr != null) {
                    expr.accept(this);
                }

                print('[');
                printAndAccept(array.getValues(), ", ");

                if (i != size - 1) {
                    print0(",");
                }

                print(']');
            } else {
                element.accept(this);
            }

            if (i != size - 1 && !(element instanceof SQLArrayExpr)) {
                print(',');
            }

            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        print(')');
    }

    public boolean visit(SQLColumnDefinition x) {
        x.getName().accept(this);
        final SQLDataType dataType = x.getDataType();

        if (dataType != null) {
            if (JdbcUtils.isPgsqlDbType(dbType) && x.getParent() instanceof SQLAlterTableAlterColumn) {
                print0(ucase ? " TYPE " : " type ");
            } else {
                print(' ');
            }
            dataType.accept(this);
        }

        if (x.getAggType() != null) {
            visitAggType(x);
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            if (item instanceof SQLNullConstraint || item instanceof SQLNotNullConstraint) {
                print(' ');
                item.accept(this);
            }
        }

        if (x.getDefaultExpr() != null) {
            visitColumnDefault(x);
        }

        if (x.isAutoIncrement()) {
            printAutoIncrement();
        }

        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        if (x.getAsExpr() != null) {
            print(' ');
            print0(ucase ? "AS " : "as ");
            x.getAsExpr().accept(this);
        }
        if (x.getBitmap() != null) {
            print(' ');
            print0(ucase ? "USING " : "using ");
            print0(ucase ? x.getBitmap().getText().toUpperCase(Locale.ROOT) : x.getBitmap().getText().toLowerCase(Locale.ROOT));
        }
        if (x.getIndexComment() != null) {
            print(' ');
            print0(ucase ? "COMMENT " : "comment ");
            x.getIndexComment().accept(this);
        }
        return false;
    }

    public boolean visit(StarRocksCreateResourceStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isExternal()) {
            print0(ucase ? "EXTERNAL " : "external ");
        }

        print0(ucase ? "RESOURCE " : "resource ");
        x.getName().accept(this);
        println();

        print0(ucase ? "PROPERTIES" : "properties");
        print(x.getProperties());
        return false;
    }

    public boolean visit(StarRocksIndexDefinition x) {
        print0(ucase ? "INDEX " : "index ");
        x.getIndexName().accept(this);
        print('(');
        printAndAccept(x.getColumns(), ", ");
        print(')');
        if (x.isUsingBitmap()) {
            print0(ucase ? " USING BITMAP" : " using bitmap");
        }
        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }
        return false;
    }

    @Override
    protected void printSQLPartitions(List<SQLPartition> partitions) {
        int partitionsSize = partitions.size();
        print0(" (");
        if (partitionsSize > 0) {
            this.indentCount++;
            for (int i = 0; i < partitionsSize; ++i) {
                println();
                partitions.get(i).accept(this);
                if (i != partitionsSize - 1) {
                    print0(",");
                }
            }
            this.indentCount--;
            println();
        }
        print(')');
    }
}
