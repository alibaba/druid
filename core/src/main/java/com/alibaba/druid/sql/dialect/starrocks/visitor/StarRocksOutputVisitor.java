package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;
import com.alibaba.druid.sql.ast.DistributedByType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.dialect.starrocks.StarRocks;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksAggregateKey;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksDuplicateKey;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksIndexDefinition;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcUtils;

import java.util.List;
import java.util.Locale;

public class StarRocksOutputVisitor extends SQLASTOutputVisitor implements StarRocksASTVisitor {
    {
        this.shardingSupport = true;
    }

    public StarRocksOutputVisitor(StringBuilder appender) {
        super(appender, DbType.starrocks, StarRocks.DIALECT);
    }

    public StarRocksOutputVisitor(StringBuilder appender, DbType dbType, SQLDialect dialect) {
        super(appender, dbType, dialect);
    }

    public StarRocksOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.starrocks, StarRocks.DIALECT, parameterized);
    }

    public StarRocksOutputVisitor(StringBuilder appender, DbType dbType, SQLDialect dialect, boolean parameterized) {
        super(appender, dbType, dialect, parameterized);
    }

    /**
     * Print StarRocks Join Hint in bracket syntax: [BROADCAST], [SHUFFLE], [BUCKET], [COLOCATE].
     *
     * @see <a href="https://docs.starrocks.io/zh/docs/3.3/administration/Query_planning/">StarRocks Query Hint</a>
     */
    @Override
    protected void printJoinHint(SQLJoinTableSource x) {
        if (x.getHintsSize() > 0) {
            for (SQLHint hint : x.getHints()) {
                if (hint instanceof SQLExprHint) {
                    print0(" [");
                    ((SQLExprHint) hint).getExpr().accept(this);
                    print0("]");
                }
            }
        }
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
        return visit((SQLCreateTableStatement) x);
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
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
                    if (createTable.isAutoBucket()) {
                        print0(ucase ? " BUCKETS AUTO" : " buckets auto");
                    } else if (createTable.getBuckets() > 0) {
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
        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        String columnName = replaceQuota(x.getName().getSimpleName());
        printName0(columnName);

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

    public boolean visit(StarRocksCreateCatalogStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }
        if (x.isExternal()) {
            print0(ucase ? "EXTERNAL " : "external ");
        }

        print0(ucase ? "CATALOG " : "catalog ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        x.getName().accept(this);

        if (x.getComment() != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }
        return false;
    }

    public boolean visit(StarRocksCreateMaterializedViewStatement x) {
        print0(ucase ? "CREATE MATERIALIZED VIEW " : "create materialized view ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        x.getName().accept(this);

        if (!x.getColumns().isEmpty()) {
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print0(")");
        }

        if (x.getComment() != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }

        if (!x.getDistributedBy().isEmpty()) {
            println();
            print0(ucase ? "DISTRIBUTED BY HASH (" : "distributed by hash (");
            printAndAccept(x.getDistributedBy(), ", ");
            print0(")");
            if (x.getBuckets() != null) {
                print0(ucase ? " BUCKETS " : " buckets ");
                x.getBuckets().accept(this);
            }
        }

        if (x.getPartitionBy() != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            x.getPartitionBy().accept(this);
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        boolean hasRefresh = x.isRefreshAsync() || x.isRefreshManual()
                || x.isRefreshImmediate() || x.isRefreshDeferred()
                || x.getRefreshEvery() != null || x.getRefreshStart() != null;
        if (hasRefresh) {
            println();
            print0(ucase ? "REFRESH" : "refresh");
            if (x.isRefreshImmediate()) {
                print0(ucase ? " IMMEDIATE" : " immediate");
            } else if (x.isRefreshDeferred()) {
                print0(ucase ? " DEFERRED" : " deferred");
            }
            if (x.isRefreshAsync()) {
                print0(ucase ? " ASYNC" : " async");
            } else if (x.isRefreshManual()) {
                print0(ucase ? " MANUAL" : " manual");
            }
            if (x.getRefreshStart() != null) {
                print0(ucase ? " START(" : " start(");
                x.getRefreshStart().accept(this);
                print0(")");
            }
            if (x.getRefreshEvery() != null) {
                print0(ucase ? " EVERY(" : " every(");
                x.getRefreshEvery().accept(this);
                print0(")");
            }
        }

        if (!x.getMvProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getMvProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }

        println();
        print0(ucase ? "AS" : "as");
        println();
        x.getQuery().accept(this);

        return false;
    }

    public boolean visit(StarRocksCreatePipeStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }
        print0(ucase ? "PIPE " : "pipe ");
        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        x.getName().accept(this);

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }

        if (x.getBody() != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            x.getBody().accept(this);
        }
        return false;
    }

    public boolean visit(StarRocksCreateDictionaryStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }
        print0(ucase ? "DICTIONARY " : "dictionary ");
        x.getName().accept(this);
        print0(ucase ? " USING " : " using ");
        x.getSourceTable().accept(this);

        print0(" (");
        for (int i = 0; i < x.getColumnMappings().size(); i++) {
            if (i > 0) {
                print0(", ");
            }
            SQLAssignItem item = x.getColumnMappings().get(i);
            item.getTarget().accept(this);
            print(' ');
            if (item.getValue() != null) {
                item.getValue().accept(this);
            }
        }
        print0(")");

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }
        return false;
    }

    public boolean visit(StarRocksCreateStorageVolumeStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }
        print0(ucase ? "STORAGE VOLUME " : "storage volume ");
        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        x.getName().accept(this);
        println();
        print0(ucase ? "TYPE = " : "type = ");
        x.getType().accept(this);
        println();
        print0(ucase ? "LOCATIONS = (" : "locations = (");
        printAndAccept(x.getLocations(), ", ");
        print0(")");

        if (x.getComment() != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }
        return false;
    }

    public boolean visit(StarRocksLoadStatement x) {
        print0(ucase ? "LOAD LABEL " : "load label ");
        x.getLabel().accept(this);
        println();
        print0("(");
        incrementIndent();
        for (int i = 0; i < x.getDataDescriptions().size(); i++) {
            if (i > 0) {
                print0(",");
            }
            println();
            StarRocksLoadStatement.DataDescription desc = x.getDataDescriptions().get(i);
            print0(ucase ? "DATA INFILE (" : "data infile (");
            printAndAccept(desc.getFilePaths(), ", ");
            print0(")");
            println();
            print0(ucase ? "INTO TABLE " : "into table ");
            desc.getTableName().accept(this);
            if (desc.getPartitions() != null && !desc.getPartitions().isEmpty()) {
                println();
                print0(ucase ? "PARTITION (" : "partition (");
                printAndAccept(desc.getPartitions(), ", ");
                print0(")");
            }
            if (desc.getColumnTerminatedBy() != null) {
                println();
                print0(ucase ? "COLUMNS TERMINATED BY " : "columns terminated by ");
                desc.getColumnTerminatedBy().accept(this);
            }
            if (desc.getFormat() != null) {
                println();
                print0(ucase ? "FORMAT AS " : "format as ");
                desc.getFormat().accept(this);
            }
            if (desc.getColumnList() != null && !desc.getColumnList().isEmpty()) {
                println();
                print0("(");
                printAndAccept(desc.getColumnList(), ", ");
                print0(")");
            }
            if (desc.getColumnMappings() != null && !desc.getColumnMappings().isEmpty()) {
                println();
                print0(ucase ? "SET (" : "set (");
                printAndAccept(desc.getColumnMappings(), ", ");
                print0(")");
            }
            if (desc.getWhereCondition() != null) {
                println();
                print0(ucase ? "WHERE " : "where ");
                desc.getWhereCondition().accept(this);
            }
        }
        decrementIndent();
        println();
        print0(")");

        if (!x.getBrokerProperties().isEmpty()) {
            println();
            print0(ucase ? "WITH BROKER (" : "with broker (");
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

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }
        return false;
    }

    public boolean visit(StarRocksCreateRoutineLoadStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }
        print0(ucase ? "ROUTINE LOAD " : "routine load ");
        x.getName().accept(this);
        print0(ucase ? " ON " : " on ");
        x.getTableName().accept(this);

        if (!x.getColumns().isEmpty()) {
            println();
            print0(ucase ? "COLUMNS (" : "columns (");
            printAndAccept(x.getColumns(), ", ");
            print0(")");
        }

        if (x.getWhereCondition() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            x.getWhereCondition().accept(this);
        }

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }

        if (x.getDataSourceType() != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            x.getDataSourceType().accept(this);
            if (!x.getDataSourceProperties().isEmpty()) {
                print0(" (");
                incrementIndent();
                println();
                int i = 0;
                for (SQLAssignItem property : x.getDataSourceProperties()) {
                    printTableOption(property.getTarget(), property.getValue(), i);
                    ++i;
                }
                decrementIndent();
                println();
                print0(")");
            }
        }
        return false;
    }

    public boolean visit(StarRocksBackupStatement x) {
        print0(ucase ? "BACKUP SNAPSHOT " : "backup snapshot ");
        x.getSnapshotName().accept(this);
        print0(ucase ? " TO " : " to ");
        x.getRepository().accept(this);

        if (!x.getOnTables().isEmpty()) {
            println();
            print0(ucase ? "ON (" : "on (");
            printAndAccept(x.getOnTables(), ", ");
            print0(")");
        }

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }
        return false;
    }

    public boolean visit(StarRocksRestoreStatement x) {
        print0(ucase ? "RESTORE SNAPSHOT " : "restore snapshot ");
        x.getSnapshotName().accept(this);
        print0(ucase ? " FROM " : " from ");
        x.getRepository().accept(this);

        if (!x.getOnTables().isEmpty()) {
            println();
            print0(ucase ? "ON (" : "on (");
            printAndAccept(x.getOnTables(), ", ");
            print0(")");
        }

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }
        return false;
    }

    public boolean visit(StarRocksCreateResourceStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }
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

    public boolean visit(StarRocksSubmitTaskStatement x) {
        print0(ucase ? "SUBMIT TASK" : "submit task");

        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }

        if (x.getScheduleStart() != null || x.getScheduleEvery() != null) {
            println();
            print0(ucase ? "SCHEDULE " : "schedule ");
            if (x.getScheduleStart() != null) {
                print0(ucase ? "START(" : "start(");
                x.getScheduleStart().accept(this);
                print0(")");
                if (x.getScheduleEvery() != null) {
                    print(' ');
                }
            }
            if (x.getScheduleEvery() != null) {
                print0(ucase ? "EVERY(" : "every(");
                x.getScheduleEvery().accept(this);
                print0(")");
            }
        }

        if (!x.getProperties().isEmpty()) {
            println();
            print0(ucase ? "PROPERTIES (" : "properties (");
            incrementIndent();
            println();
            int i = 0;
            for (SQLAssignItem property : x.getProperties()) {
                printTableOption(property.getTarget(), property.getValue(), i);
                ++i;
            }
            decrementIndent();
            println();
            print0(")");
        }

        if (x.getBody() != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            x.getBody().accept(this);
        }

        return false;
    }

    public boolean visit(StarRocksIndexDefinition x) {
        print0(ucase ? "INDEX " : "index ");
        x.getIndexName().accept(this);
        print('(');
        printAndAccept(x.getColumns(), ", ");
        print(')');
        if (x.getIndexType() != null) {
            print0(ucase ? " USING " : " using ");
            print0(x.getIndexType());
            if (!x.getIndexOption().isEmpty()) {
                print0("(");
                int i = 0;
                for (SQLAssignItem sqlAssignItem : x.getIndexOption()) {
                    printIndexOption(sqlAssignItem.getTarget(), sqlAssignItem.getValue(), i);
                    i++;
                }
                print0(")");
            }
        }
        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }
        return false;
    }

    protected void printIndexOption(SQLExpr name, SQLExpr value, int index) {
        if (index != 0) {
            print(", ");
        }

        String key = name.toString();

        boolean unquote = false;

        print0(key);
        if (unquote) {
            print('\'');
        }

        print0(" = ");
        value.accept(this);
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
