package com.alibaba.druid.sql.dialect.gaussdb.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbPartitionValue;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class GaussDbOutputVisitor extends SQLASTOutputVisitor implements GaussDbASTVisitor {
    public GaussDbOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.gaussdb;
    }

    public GaussDbOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.gaussdb;
    }

    @Override
    public boolean visit(GaussDbCreateTableStatement x) {
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

        printTableElements(x);

        printComment(x.getComment());

        printAutoIncrement(x.getAutoIncrement());

        printCharSet(x.getCharset());

        printCollate(x.getCollate());

        printEngine(x);

        printTableOptions(x);

        if (x.getDistributeBy() != null) {
            printDistributeBy(x.getDistributeBy());
        }

        printPartitionedBy(x);

        printCreateTableLike(x);
        return false;
    }

    public void printDistributeBy(GaussDbDistributeBy x) {
        x.accept(this);
    }

    @Override
    public boolean visit(SQLPartitionByRange x) {
        print0(ucase ? "RANGE" : "range");
        printColumns(x.getColumns());
        if (!x.getPartitions().isEmpty()) {
            printPartitionsValue(x.getPartitions());
        }
        return false;
    }

    @Override
    public boolean visit(SQLPartitionByList x) {
        print0(ucase ? "LIST" : "list");
        printColumns(x.getColumns());
        if (!x.getPartitions().isEmpty()) {
            printPartitionsValue(x.getPartitions());
        }
        return false;
    }

    @Override
    protected void printTableOptionsPrefix(SQLCreateTableStatement x) {
        println();
        print0(ucase ? "WITH (" : "with (");
        incrementIndent();
        println();
    }

    @Override
    protected void printEngine(SQLCreateTableStatement x) {
        if (x instanceof GaussDbCreateTableStatement) {
            SQLExpr engine = ((GaussDbCreateTableStatement) x).getEngine();
            if (engine != null) {
                println();
                print0(ucase ? "ENGINE = " : "engine = ");
                engine.accept(this);
            }
        }
    }

    @Override
    protected void printComment(SQLExpr comment) {
        if (comment == null) {
            return;
        }
        println();
        print0(ucase ? "COMMENT = " : "comment = ");
        comment.accept(this);
    }

    protected void printAutoIncrement(SQLExpr autoIncrement) {
        if (autoIncrement == null) {
            return;
        }
        println();
        print0(ucase ? "AUTO_INCREMENT = " : "auto_increment = ");
        autoIncrement.accept(this);
    }

    protected void printCharSet(SQLExpr charset) {
        if (charset == null) {
            return;
        }
        println();
        print0(ucase ? "CHARSET = " : "charset = ");
        charset.accept(this);
    }

    protected void printCollate(SQLExpr collate) {
        if (collate == null) {
            return;
        }
        println();
        print0(ucase ? "COLLATE = " : "collate = ");
        collate.accept(this);
    }

    @Override
    protected void printPartitionedBy(SQLCreateTableStatement x) {
        if (x instanceof GaussDbCreateTableStatement) {
            SQLPartitionBy partitionBy = ((GaussDbCreateTableStatement) x).getPartitionBy();
            if (partitionBy == null) {
                return;
            }
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
        }
    }

    protected void printTableElements(GaussDbCreateTableStatement x) {
        int size = x.getTableElementList().size();
        if (size == 0) {
            return;
        }

        print0(" (");

        this.indentCount++;
        println();
        for (int i = 0; i < size; ++i) {
            printTableElement(x.getTableElementList(), i);
        }
        this.indentCount--;
        if (!x.getClusteredBy().isEmpty()) {
            print(',');
            println();
            print(" ");
            List<SQLExpr> clusterBy = x.getClusteredBy().stream().map(k -> k.getExpr()).collect(Collectors.toList());
            print0(ucase ? "  PARTIAL CLUSTER KEY " : "  partial cluster key");
            printColumns(clusterBy);

        }
        println();
        print(')');
    }

    @Override
    public boolean visit(SQLPartitionValue value) {
        if (value instanceof GaussDbPartitionValue) {
            GaussDbPartitionValue x = (GaussDbPartitionValue) value;
            if (x.getOperator() == SQLPartitionValue.Operator.LessThan) {
                print0(ucase ? "VALUES LESS THAN" : "values less than");
                print0(" (");
                if (x.getItems().size() == 1) {
                    // for single specific value
                    printExpr(x.getItems().get(0), parameterized);
                } else {
                    print("(");
                    printAndAccept(x.getItems(), ", ", false);
                    print(')');
                }
                print(")");
            } else if (x.getOperator() == SQLPartitionValue.Operator.StartEndEvery) {
                if (x.getStart() != null) {
                    print0(ucase ? " START " : " start ");
                    x.getStart().accept(this);
                }
                if (x.getEnd() != null) {
                    print0(ucase ? " END " : " end ");
                    x.getEnd().accept(this);
                }
                if (x.getEvery() != null) {
                    print0(ucase ? " EVERY " : " every ");
                    x.getEvery().accept(this);
                }
            } else if (x.getOperator() == SQLPartitionValue.Operator.List) {
                print0(ucase ? "VALUES " : "valuse ");
                print0(" (");
                if (x.getItems().size() == 1) {
                    // for single specific value
                    printExpr(x.getItems().get(0), parameterized);
                } else {
                    print("(");
                    printAndAccept(x.getItems(), ", ", false);
                    print(')');
                }
                print0(")");
            }
            if (x.getSpaceName() != null) {
                print0(ucase ? " TABLESPACE " : " tablespace ");
                x.getSpaceName().accept(this);
            } else if (x.getDataNode() != null) {
                print0(ucase ? " DATANODE " : " datanode ");
                x.getDataNode().accept(this);
            }
        } else {
            super.visit(value);
        }
        return false;
    }

    @Override
    public boolean visit(SQLPartition x) {
        GaussDbPartitionValue values = (GaussDbPartitionValue) x.getValues();
        if (values.getDistribute()) {
            print0(ucase ? "SLICE " : "slice ");
        } else {
            print0(ucase ? "PARTITION " : "partition ");
        }
        x.getName().accept(this);
        print0(" ");
        values.accept(this);
        return false;
    }

    public void printColumns(List<SQLExpr> columns) {
        for (SQLExpr column : columns) {
            if (!(column instanceof SQLName)) {
                break;
            }
        }
        if (columns.size() == 1) {
            print0(" (");
            columns.get(0).accept(this);
            print(')');
        } else {
            print0(" (");
            printAndAccept(columns, ", ");
            print(')');
        }
    }

    @Override
    public boolean visit(SQLColumnDefinition x) {
        super.visit(x);
        if (x.getOnUpdate() != null) {
            print0(ucase ? " ON UPDATE " : " on update ");
            x.getOnUpdate().accept(this);
        }
        return false;
    }

    public void printPartitionsValue(List<SQLPartition> partitions) {
        print(" (");
        this.indentCount++;
        for (int i = 0, size = partitions.size(); i < size; ++i) {
            if (i != 0) {
                print(',');
            }
            println();
            SQLPartition sqlPartition = partitions.get(i);
            sqlPartition.accept(this);
        }
        this.indentCount--;
        println();
        print(')');
    }

    @Override
    public boolean visit(GaussDbDistributeBy x) {
        println();
        print0(ucase ? "DISTRIBUTE BY " : "distribute by ");
        x.getType().accept(this);
        printColumns(x.getColumns());
        if (!x.getDistributions().isEmpty()) {
            printPartitionsValue(x.getDistributions());
        }
        return false;
    }
}
