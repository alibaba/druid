package com.alibaba.druid.sql.dialect.gaussdb.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbPartitionValue;
import com.alibaba.druid.sql.dialect.gaussdb.ast.stmt.GaussDbCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.stmt.GaussDbInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGVacuumStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class GaussDbOutputVisitor extends PGOutputVisitor implements GaussDbASTVisitor {
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
        printServer(x);

        printCreateTableLike(x);

        printTableOptions(x);

        printForeignMode(x);

        printOnCommit(x);

        printCompressType(x);

        printDistributeBy(x);

        printToGroup(x);

        printToNode(x);

        printPartitionBy(x);

        printRowMovement(x);

        printComment(x.getComment());
        return false;
    }

    public void printForeignMode(GaussDbCreateTableStatement x) {
        if (x.getForeignTableMode() != null) {
            switch (x.getForeignTableMode()) {
                case WRITE_ONLY:
                    print0(ucase ? "WRITE ONLY" : "write only");
                    break;
                case READ_ONLY:
                    print0(ucase ? "READ ONLY" : "read only");
                    break;
                case READ_WRITE:
                    print0(ucase ? "READ WRITE" : "read write");
                    break;
            }
        }
    }
    public void printServer(GaussDbCreateTableStatement x) {
        if (x.getServer() != null) {
            println();
            print0(ucase ? "SERVER " : "server ");
            x.getServer().accept(this);
            println();
        }
    }
    public void printRowMovement(GaussDbCreateTableStatement x) {
        if (x.getRowMovementType() != null) {
            println();
            x.getRowMovementType().accept(this);
            print(ucase ? " ROW MOVEMENT" : " row movement");
        }
    }

    public void printCompressType(GaussDbCreateTableStatement x) {
        if (x.getCompressType() != null) {
            println();
            x.getCompressType().accept(this);
        }
    }
    public void printOnCommit(GaussDbCreateTableStatement x) {
        if (x.getOnCommitExpr() != null) {
            println();
            print0(ucase ? "ON COMMIT " : "on commit ");
            x.getOnCommitExpr().accept(this);
            print0(ucase ? " ROWS" : " rows");
        }
    }

    public void printDistributeBy(GaussDbCreateTableStatement x) {
        if (x.getDistributeBy() != null) {
            x.getDistributeBy().accept(this);
        }
    }

    public void printToGroup(GaussDbCreateTableStatement x) {
        if (x.getToGroup() != null) {
            println();
            print0(ucase ? "TO GROUP " : "to group ");
            x.getToGroup().accept(this);
        }
    }

    public void printToNode(GaussDbCreateTableStatement x) {
        if (x.getToNode() != null) {
            println();
            print0(ucase ? "TO NODE " : "to node ");
            x.getToNode().accept(this);
        }
    }

    @Override
    public boolean visit(SQLPartitionByRange x) {
        print0(ucase ? "RANGE" : "range");
        printColumns(x.getColumns());
        printPartitionsValue(x.getPartitions());
        return false;
    }

    @Override
    public boolean visit(SQLPartitionByList x) {
        print0(ucase ? "LIST" : "list");
        printColumns(x.getColumns());
        printPartitionsValue(x.getPartitions());
        return false;
    }

    @Override
    protected void printTableOptionsPrefix(SQLCreateTableStatement x) {
        println();
        print0(ucase ? "WITH (" : "with (");
        incrementIndent();
        println();
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
    public boolean visit(SQLPartitionSingle x) {
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
        if (!x.getColumns().isEmpty()) {
            printColumns(x.getColumns());
        }
        if (!x.getDistributions().isEmpty()) {
            printPartitionsValue(x.getDistributions());
        }
        return false;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        if (x instanceof GaussDbInsertStatement) {
            return visit((GaussDbInsertStatement) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(PGInsertStatement x) {
        if (x instanceof GaussDbInsertStatement) {
            return visit((GaussDbInsertStatement) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(GaussDbInsertStatement x) {
        List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        if (x.getInsertBeforeCommentsDirect() != null) {
            printlnComments(x.getInsertBeforeCommentsDirect());
        }

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            visit(with);
            println();
        }
        print0(ucase ? "INSERT " : "insert ");
        if (x.isOverwrite()) {
            print0(ucase ? "OVERWRITE " : "overwrite ");
        } else if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }
        print0(ucase ? "INTO " : "into ");

        x.getTableSource().accept(this);

        String columnsString = x.getColumnsString();
        if (columnsString != null) {
            print0(columnsString);
        } else {
            printInsertColumns(x.getColumns());
        }

        if (x.isDefaultValues()) {
            println();
            print0(ucase ? "DEFAULT VALUES" : "default values");
        }

        printValuesOrQuery(x);

        List<SQLExpr> duplicateKeyUpdate = x.getDuplicateKeyUpdate();
        if (!duplicateKeyUpdate.isEmpty()) {
            println();
            print0(ucase ? "ON DUPLICATE KEY UPDATE " : "on duplicate key update ");
            for (int i = 0, size = duplicateKeyUpdate.size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }
                duplicateKeyUpdate.get(i).accept(this);
            }
        }

        printOnConflict(x);

        printReturning(x);
        return false;
    }

    protected void visitAggregateRest(SQLAggregateExpr x) {
        super.visitAggregateRest(x);
        Object value = x.getAttribute("SEPARATOR");
        if (value != null) {
            print0(ucase ? " SEPARATOR " : " separator ");
            ((SQLObject) value).accept(this);
        }
    }

    protected void printCompression(SQLColumnDefinition x) {
        if (x.getCompression() != null) {
            print0(ucase ? " COMPRESS_MODE " : " compress_mode ");
            x.getCompression().accept(this);
        }
    }

    @Override
    public boolean visit(SQLIntervalExpr x) {
        print0(ucase ? "INTERVAL " : "interval ");
        SQLExpr value = x.getValue();

        boolean str = value instanceof SQLCharExpr;
        if (!str) {
            print('\'');
        }
        value.accept(this);

        SQLIntervalUnit unit = x.getUnit();
        if (unit != null) {
            print(' ');
            print0(ucase ? unit.name : unit.nameLCase);
            if (value instanceof SQLIntegerExpr) {
                SQLIntegerExpr integerExpr = (SQLIntegerExpr) value;
                if (integerExpr.getNumber().intValue() > 1) {
                    print(ucase ? 'S' : 's');
                }
            }
        }
        if (!str) {
            print('\'');
        }
        return false;
    }

    @Override
    protected void printVacuumRest(PGVacuumStatement x) {
        if (x.isDeltaMerge()) {
            print0(ucase ? "DELTAMERGE " : "deltamerge ");
        }
        if (x.isHdfsDirectory()) {
            print0(ucase ? "HDFSDIRECTORY " : "hdfsdirectory ");
        }
    }

    protected void printCreateTableFeatures(SQLCreateTableStatement x) {
        if (x.isEnabled(SQLCreateTableStatement.Feature.OrReplace)) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }
        SQLCreateTableStatement.Feature[] features = {
                SQLCreateTableStatement.Feature.Global,
                SQLCreateTableStatement.Feature.Local,
                SQLCreateTableStatement.Feature.Temporary,
                SQLCreateTableStatement.Feature.Shadow,
                SQLCreateTableStatement.Feature.External,
                SQLCreateTableStatement.Feature.Transactional,
                SQLCreateTableStatement.Feature.Dimension,
                SQLCreateTableStatement.Feature.Unlogged
        };

        for (SQLCreateTableStatement.Feature feature : features) {
            if (x.isEnabled(feature)) {
                String name;
                if (feature.equals(SQLCreateTableStatement.Feature.External)) {
                    name = "FOREIGN";
                } else {
                    name = feature.name();
                }
                print0(ucase ? name.toUpperCase() : name.toLowerCase());
                print(' ');
            }
        }
    }

    protected void printTableOptions(SQLCreateTableStatement x) {
        List<SQLAssignItem> tblProperties = x.getTableOptions();
        if (tblProperties.isEmpty()) {
            return;
        }
        if (x.isEnabled(SQLCreateTableStatement.Feature.External)) {
            print0(ucase ? "OPTIONS (" : "options (");
            println();
            indentCount++;
            int i = 0;
            for (SQLAssignItem property : tblProperties) {
                if (i != 0) {
                    print(",");
                    println();
                }
                property.getTarget().accept(this);
                print0(" ");
                property.getValue().accept(this);
                i++;
            }
            indentCount--;
            println();
            print0(")");
            println();
        } else {
            printTableOptionsPrefix(x);
            int i = 0;
            for (SQLAssignItem property : tblProperties) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            printTableOptionsPostfix(x);
        }
    }
}
