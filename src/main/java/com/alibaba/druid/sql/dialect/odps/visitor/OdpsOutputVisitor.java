/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveLoadDataStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.odps.ast.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.alibaba.druid.sql.dialect.odps.ast.OdpsAddFileStatement.FileType.JAR;

public class OdpsOutputVisitor extends HiveOutputVisitor implements OdpsASTVisitor {
    private Set<String> builtInFunctions = new HashSet<String>();

    {
        builtInFunctions.add("IF");
        builtInFunctions.add("COALESCE");
        builtInFunctions.add("TO_DATE");
        builtInFunctions.add("SUBSTR");
        builtInFunctions.add("INSTR");
        builtInFunctions.add("LENGTH");
        builtInFunctions.add("SPLIT");
        builtInFunctions.add("TOLOWER");
        builtInFunctions.add("TOUPPER");
        builtInFunctions.add("EXPLODE");
        builtInFunctions.add("LEAST");
        builtInFunctions.add("GREATEST");
        
        groupItemSingleLine = true;
    }

    public OdpsOutputVisitor(Appendable appender){
        super(appender, DbType.odps);
    }

    public boolean visit(OdpsCreateTableStatement x) {
        print0(ucase ? "CREATE " : "create ");

        if (x.isExternal()) {
            print0(ucase ? "EXTERNAL " : "external ");
        }

        if (x.isIfNotExists()) {
            print0(ucase ? "TABLE IF NOT EXISTS " : "table if not exists ");
        } else {
            print0(ucase ? "TABLE " : "table ");
        }

        x.getName().accept(this);

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        final List<SQLTableElement> tableElementList = x.getTableElementList();
        int size = tableElementList.size();
        if (size > 0) {
            print0(" (");
            
            if (this.isPrettyFormat() && x.hasBodyBeforeComment()) {
                print(' ');
                printlnComment(x.getBodyBeforeCommentsDirect());
            }
            
            this.indentCount++;
            println();
            for (int i = 0; i < size; ++i) {
                SQLTableElement element = tableElementList.get(i);
                element.accept(this);

                if (i != size - 1) {
                    print(',');
                }
                if (this.isPrettyFormat() && element.hasAfterComment()) {
                    print(' ');
                    printlnComment(element.getAfterCommentsDirect());
                }

                if (i != size - 1) {
                    println();
                }
            }
            this.indentCount--;
            println();
            print(')');
        }

        if (x.getComment() != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }

        int partitionSize = x.getPartitionColumns().size();
        if (partitionSize > 0) {
            println();
            print0(ucase ? "PARTITIONED BY (" : "partitioned by (");
            this.indentCount++;
            println();
            for (int i = 0; i < partitionSize; ++i) {
                SQLColumnDefinition column = x.getPartitionColumns().get(i);
                column.accept(this);

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

        List<SQLSelectOrderByItem> clusteredBy = x.getClusteredBy();
        if (clusteredBy.size() > 0) {
            println();
            if (x.getClusteringType() == ClusteringType.Range) {
                print0(ucase ? "RANGE " : "range ");
            }
            print0(ucase ? "CLUSTERED BY (" : "clustered by (");
            printAndAccept(clusteredBy, ",");
            print(')');
        }

        List<SQLSelectOrderByItem> sortedBy = x.getSortedBy();
        if (sortedBy.size() > 0) {
            println();
            print0(ucase ? "SORTED BY (" : "sorted by (");
            printAndAccept(sortedBy, ", ");
            print(')');
        }

        int buckets = x.getBuckets();
        if (buckets > 0) {
            println();
            print0(ucase ? "INTO " : "into ");
            print(buckets);
            print0(ucase ? " BUCKETS" : " buckets");
        }

        int shards = x.getShards();
        if (shards > 0) {
            println();
            print0(ucase ? "INTO " : "into ");
            print(shards);
            print0(ucase ? " SHARDS" : " shards");
        }

        if (x.getLifecycle() != null) {
            println();
            print0(ucase ? "LIFECYCLE " : "lifecycle ");
            x.getLifecycle().accept(this);
        }

        SQLSelect select = x.getSelect();
        if (select != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            select.accept(this);
        }

        SQLExpr storedBy = x.getStoredBy();
        if (storedBy != null) {
            println();
            print0(ucase ? "STORED BY " : "stored by ");
            storedBy.accept(this);
        }

        SQLExpr storedAs = x.getStoredAs();
        if (storedAs != null) {
            println();
            print0(ucase ? "STORED AS " : "stored as ");
            storedAs.accept(this);
        }

        List<SQLExpr> withSerdeproperties = x.getWithSerdeproperties();
        if (withSerdeproperties.size() > 0) {
            println();
            print0(ucase ? "WITH SERDEPROPERTIES (" : "with serdeproperties (");
            printAndAccept(withSerdeproperties, ", ");
            print(')');
        }

        this.printTblProperties(x);

        SQLExpr location = x.getLocation();
        if (location != null) {
            println();
            print0(ucase ? "LOCATION " : "location ");
            location.accept(this);
        }

        SQLExpr using = x.getUsing();
        if (using != null) {
            println();
            print0(ucase ? "USING " : "using ");
            using.accept(this);
        }

        return false;
    }

//    protected void printSelectList(List<SQLSelectItem> selectList) {
//        this.indentCount++;
//        for (int i = 0, size = selectList.size(); i < size; ++i) {
//            SQLSelectItem selectItem = selectList.get(i);
//
//            if (i != 0) {
//                SQLSelectItem preSelectItem = selectList.get(i - 1);
//                if (preSelectItem.hasAfterComment()) {
//                    print(' ');
//                    printlnComment(preSelectItem.getAfterCommentsDirect());
//                }
//
//                println();
//                print0(", ");
//            }
//
//            selectItem.accept(this);
//
//            if (i == selectList.size() - 1 && selectItem.hasAfterComment()) {
//                print(' ');
//                printlnComments(selectItem.getAfterCommentsDirect());
//            }
//        }
//        this.indentCount--;
//    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        print('(');
        this.indentCount++;
        println();
        x.getSelect().accept(this);
        this.indentCount--;
        println();
        print(')');

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        return false;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        x.getLeft().accept(this);

        if (x.getJoinType() == JoinType.COMMA) {
            print(',');
        } else {
            println();
            printJoinType(x.getJoinType());
        }
        print(' ');
        x.getRight().accept(this);

        if (x.getCondition() != null) {
            println();
            print0(ucase ? "ON " : "on ");
            this.indentCount++;
            x.getCondition().accept(this);
            this.indentCount--;
        }

        if (x.getUsing().size() > 0) {
            print0(ucase ? " USING (" : " using (");
            printAndAccept(x.getUsing(), ", ");
            print(')');
        }

        if (x.getAlias() != null) {
            print0(ucase ? " AS " : " as ");
            print0(x.getAlias());
        }

        SQLJoinTableSource.UDJ udj = x.getUdj();
        if (udj != null) {
            println();
            udj.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(OdpsUDTFSQLSelectItem x) {
        x.getExpr().accept(this);

        println();
        print0(ucase ? "AS (" : "as (");

        int aliasSize = x.getAliasList().size();
        if (aliasSize > 5) {
            this.indentCount++;
            println();
        }

        for (int i = 0; i < aliasSize; ++i) {
            if (i != 0) {
                if (aliasSize > 5) {
                    println(",");
                } else {
                    print0(", ");
                }
            }
            print0(x.getAliasList().get(i));
        }

        if (aliasSize > 5) {
            this.indentCount--;
            println();
        }
        print(')');

        return false;
    }

    @Override
    public boolean visit(SQLShowStatisticStmt x) {
        print0(ucase ? "SHOW STATISTIC" : "show statistic");
        final SQLExprTableSource tableSource = x.getTableSource();
        if (tableSource != null) {
            print(' ');
            tableSource.accept(this);
        }

        List<SQLAssignItem> partitions = x.getPartitions();
        if (!partitions.isEmpty()) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(partitions, ", ");
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(OdpsSetLabelStatement x) {
        print0(ucase ? "SET LABEL " : "set label ");
        print0(x.getLabel());
        print0(ucase ? " TO " : " to ");

        if (x.getUser() != null) {
            print0(ucase ? "USER " : "user ");
            x.getUser().accept(this);
        } else if (x.getTable() != null) {
            print0(ucase ? "TABLE " : "table ");
            x.getTable().accept(this);
            if (x.getColumns().size() > 0) {
                print('(');
                printAndAccept(x.getColumns(), ", ");
                print(')');
            }
        }

        return false;
    }

    @Override
    public boolean visit(OdpsSelectQueryBlock x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "SELECT " : "select ");

        List<SQLCommentHint> hints = x.getHintsDirect();
        if (hints != null) {
            printAndAccept(hints, " ");
            print(' ');
        }

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        printSelectList(x.getSelectList());

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            from.accept(this);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            if (where.hasBeforeComment() && isPrettyFormat()) {
                printlnComments(x.getWhere().getBeforeCommentsDirect());
            }

            where.accept(this);
            if (where.hasAfterComment() && isPrettyFormat()) {
                print(' ');
                printlnComment(x.getWhere().getAfterCommentsDirect());
            }
        }

        if (x.getGroupBy() != null) {
            println();
            x.getGroupBy().accept(this);
        }

        final List<SQLWindow> windows = x.getWindows();
        if (windows != null && windows.size() > 0) {
            println();
            print0(ucase ? "WINDOW " : "window ");
            printAndAccept(windows, ", ");
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        final List<SQLSelectOrderByItem> distributeBy = x.getDistributeByDirect();
        if (distributeBy.size() > 0) {
            println();
            print0(ucase ? "DISTRIBUTE BY " : "distribute by ");
            printAndAccept(distributeBy, ", ");
        }

        final List<SQLSelectOrderByItem> sortBy = x.getSortByDirect();
        if (!sortBy.isEmpty()) {
            println();
            print0(ucase ? "SORT BY " : "sort by ");
            printAndAccept(sortBy, ", ");
        }

        final List<SQLSelectOrderByItem> clusterBy = x.getClusterByDirect();
        if (clusterBy.size() > 0) {
            println();
            print0(ucase ? "CLUSTER BY " : "cluster by ");
            printAndAccept(clusterBy, ", ");
        }

        if (x.getLimit() != null) {
            println();
            x.getLimit().accept(this);
        }

        return false;
    }

    public boolean visit(SQLOrderBy x) {
        int itemSize = x.getItems().size();
        if (itemSize > 0) {
            print0(ucase ? "ORDER BY " : "order by ");
            this.indentCount++;
            for (int i = 0; i < itemSize; ++i) {
                if (i != 0) {
                    println(", ");
                }
                x.getItems().get(i).accept(this);
            }
            this.indentCount--;
        }

        return false;
    }

    @Override
    public boolean visit(OdpsAddStatisticStatement x) {
        print0(ucase ? "ADD STATISTIC " : "add statistic ");
        x.getTable().accept(this);
        print(' ');
        x.getStatisticClause().accept(this);

        return false;
    }

    @Override
    public boolean visit(OdpsRemoveStatisticStatement x) {
        print0(ucase ? "REMOVE STATISTIC " : "remove statistic ");
        x.getTable().accept(this);
        print(' ');
        x.getStatisticClause().accept(this);

        return false;
    }

    @Override
    public boolean visit(OdpsStatisticClause.TableCount x) {
        print0(ucase ? "TABLE_COUNT" : "table_count");
        return false;
    }

    @Override
    public boolean visit(OdpsStatisticClause.ExpressionCondition x) {
        print0(ucase ? "EXPRESSION_CONDITION " : "expression_condition ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsStatisticClause.NullValue x) {
        print0(ucase ? "NULL_VALUE " : "null_value ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsStatisticClause.DistinctValue x) {
        print0(ucase ? "DISTINCT_VALUE " : "distinct_value ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsStatisticClause.ColumnSum x) {
        print0(ucase ? "COLUMN_SUM " : "column_sum ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsStatisticClause.ColumnMax x) {
        print0(ucase ? "COLUMN_MAX " : "column_max ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsStatisticClause.ColumnMin x) {
        print0(ucase ? "COLUMN_MIN " : "column_min ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsReadStatement x) {
        print0(ucase ? "READ " : "read ");
        x.getTable().accept(this);

        if (x.getColumns().size() > 0) {
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }

        if (x.getPartition().size() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(x.getPartition(), ", ");
            print(')');
        }

        if (x.getRowCount() != null) {
            print(' ');
            x.getRowCount().accept(this);
        }

        return false;
    }

    protected void printMethodOwner(SQLExpr owner) {
        owner.accept(this);
        if (owner instanceof SQLMethodInvokeExpr) {
            print('.');
        } else {
            print(':');
        }
    }

    protected void printJoinType(JoinType joinType) {
        if (joinType.equals(JoinType.LEFT_OUTER_JOIN)) {
            print0(ucase ? "LEFT OUTER JOIN" : "left outer join");
        } else if (joinType.equals(JoinType.RIGHT_OUTER_JOIN)) {
            print0(ucase ? "RIGHT OUTER JOIN" : "right outer join");
        } else if (joinType.equals(JoinType.FULL_OUTER_JOIN)) {
            print0(ucase ? "FULL OUTER JOIN" : "full outer join");
        } else {
            print0(ucase ? joinType.name : joinType.name_lcase);
        }
    }

    public boolean visit(SQLDataType x) {
        String dataTypeName = x.getName();
        print0(ucase ? dataTypeName.toUpperCase() : dataTypeName.toLowerCase());
        if (x.getArguments().size() > 0) {
            print('(');
            printAndAccept(x.getArguments(), ", ");
            print(')');
        }

        return false;
    }

    protected void printFunctionName(String name) {
        String upperName = name.toUpperCase();
        if (builtInFunctions.contains(upperName)) {
            print0(ucase ? upperName : name);
        } else {
            print0(name);
        }
    }

    @Override
    public boolean visit(OdpsShowGrantsStmt x) {
        if (x.isLabel()) {
            print0(ucase ? "SHOW LABEL GRANTS" : "show label grants");

            if (x.getObjectType() != null) {
                print0(ucase ? " ON TABLE " : " on table ");
                x.getObjectType().accept(this);
            }

            if (x.getUser() != null) {
                print0(ucase ? " FOR USER " : " for user ");
                x.getUser().accept(this);
            }
        } else {
            print0(ucase ? "SHOW GRANTS" : "show grants");

            if (x.getUser() != null) {
                print0(ucase ? " FOR " : " for ");
                x.getUser().accept(this);
            }

            if (x.getObjectType() != null) {
                print0(ucase ? " ON TYPE " : " on type ");
                x.getObjectType().accept(this);
            }
        }
        return false;
    }

    @Override
    public boolean visit(OdpsListStmt x) {
        print0(ucase ? "LIST " : "list ");
        if (x.getObject() != null) {
            x.getObject().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OdpsGrantStmt x) {
        print0(ucase ? "GRANT " : "grant ");
        if (x.isSuper()) {
            print0(ucase ? "SUPER " : "super ");
        }

        if (x.isLabel()) {
            print0(ucase ? "LABEL " : "label ");
            x.getLabel().accept(this);;
        } else {
            printAndAccept(x.getPrivileges(), ", ");
        }

        if (x.getResource() != null) {
            print0(ucase ? " ON " : " on ");
            if (x.getResourceType() != null) {
                print0(ucase ? x.getResourceType().name() : x.getResourceType().name().toLowerCase());
                print(' ');
            }
            x.getResource().accept(this);
            
            if (x.getColumns().size() > 0) {
                print('(');
                printAndAccept(x.getColumns(), ", ");
                print(')');
            }
        }

        if (x.getUsers() != null) {
            print0(ucase ? " TO " : " to ");
            if (x.getSubjectType() != null) {
                print0(x.getSubjectType().name());
                print(' ');
            }
            printAndAccept(x.getUsers(), ",");
        }

        if (x.getExpire() != null) {
            print0(ucase ? " WITH EXP " : " with exp ");
            x.getExpire().accept(this);
        }

        return false;
    }
    
    public boolean visit(SQLCharExpr x, boolean parameterized) {
        String text = x.getText();
        if (text == null) {
            print0(ucase ? "NULL" : "null");
        } else {
            StringBuilder buf = new StringBuilder(text.length() + 2);
            buf.append('\'');
            for (int i = 0; i < text.length(); ++i) {
                char ch = text.charAt(i);
                switch (ch) {
                    case '\\':
                        buf.append("\\\\");
                        break;
                    case '\'':
                        buf.append("\\'");
                        break;
                    case '\0':
                        buf.append("\\0");
                        break;
                    case '\n':
                        buf.append("\\n");
                        break;
                    default:
                        buf.append(ch);
                        break;
                }
            }
            buf.append('\'');

            print0(buf.toString());
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableRenameColumn x) {
        print0(ucase ? "CHANGE COLUMN " : "change column ");
        x.getColumn().accept(this);
        print0(ucase ? " RENAME TO " : " rename to ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsAddTableStatement x) {
        print0(ucase ? "ADD TABLE " : "add table ");
        x.getTable().accept(this);

        List<SQLAssignItem> partitoins = x.getPartitoins();
        if (partitoins.size() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(partitoins, ", ");
            print(')');
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            printExpr(comment);
        }

        if (x.isForce()) {
            print0(" -f");
        }

        return false;
    }

    @Override
    public boolean visit(OdpsAddFileStatement x) {
        print0(ucase ? "ADD " : "add ");

        OdpsAddFileStatement.FileType type = x.getType();
        switch (type) {
            case JAR:
                print0(ucase ? "JAR " : "jar ");
                break;
            case ARCHIVE:
                print0(ucase ? "ARCHIVE " : "archive ");
                break;
            case PY:
                print0(ucase ? "PY " : "py ");
                break;
            default:
                print0(ucase ? "FILE " : "file ");
                break;
        }

        print0(x.getFile());

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            printExpr(comment);
        }

        if (x.isForce()) {
            print0(" -f");
        }

        return false;
    }

    @Override
    public boolean visit(OdpsAddUserStatement x) {
        print0(ucase ? "ADD USER " : "add user ");
        printExpr(x.getUser());
        return false;
    }

    @Override
    public boolean visit(OdpsRemoveUserStatement x) {
        print0(ucase ? "REMOVE USER " : "remove user ");
        printExpr(x.getUser());
        return false;
    }

    @Override
    public boolean visit(SQLWhoamiStatement x) {
        print0(ucase ? "WHOAMI" : "whoami");
        return false;
    }

    @Override
    public boolean visit(OdpsAlterTableSetChangeLogs x) {
        print0(ucase ? "SET CHANGELOGS " : "set changelogs ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsCountStatement x) {
        print0(ucase ? "COUNT " : "count ");
        x.getTable().accept(this);

        List<SQLAssignItem> partitoins = x.getPartitoins();
        if (partitoins.size() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(partitoins, ", ");
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(OdpsQueryAliasStatement x) {
        print0(x.getVariant());
        print0(" := ");
        x.getStatement().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsTransformExpr x) {
        print0(ucase ? "TRANSFORM(" : "transform(");
        printAndAccept(x.getInputColumns(), ", ");
        print(')');

        SQLExpr using = x.getUsing();
        if (using != null) {
            println();
            print0(ucase ? "USING " : "using ");
            using.accept(this);
        }

        List<SQLExpr> resources = x.getResources();
        if (!resources.isEmpty()) {
            println();
            print0(ucase ? "RESOURCES " : "resources ");
            printAndAccept(resources, ", ");
        }

        List<SQLExpr> outputColumns = x.getOutputColumns();
        if (!outputColumns.isEmpty()) {
            println();
            print0(ucase ? "AS (" : "as (");
            printAndAccept(resources, ", ");
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(OdpsExstoreStatement x) {
        print0(ucase ? "EXSTORE " : "exstore ");
        x.getTable().accept(this);
        print0(ucase ? " PARTITION (" : " partition (");
        printAndAccept(x.getPartitions(), ", ");
        print(')');

        return false;
    }

    @Override
    public boolean visit(HiveLoadDataStatement x) {
        print0(ucase ? "LOAD " : "load ");

        if (x.isOverwrite()) {
            print0(ucase ? "OVERWRITE " : "overwrite ");
        }

        print0(ucase ? "INTO TABLE " : "into table ");

        x.getInto().accept(this);

        if (x.getPartition().size() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(x.getPartition(), ", ");
            print(')');
        }

        println();
        print0(ucase ? "LOCATION " : "location ");
        x.getInpath().accept(this);

        SQLExpr storedBy = x.getStoredBy();
        if (storedBy != null) {
            println();
            print0(ucase ? "STORED BY " : "stored by ");
            storedBy.accept(this);
        }

        SQLExpr rowFormat = x.getRowFormat();
        if (rowFormat != null) {
            println();
            print0(ucase ? "ROW FORMAT SERDE " : "row format serde ");
            rowFormat.accept(this);
        }

        printSerdeProperties(x.getSerdeProperties());

        SQLExpr storedAs = x.getStoredAs();
        if (storedAs != null) {
            println();
            print0(ucase ? "STORED AS " : "stored as ");
            storedAs.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(OdpsNewExpr x) {
        print0("new ");
        super.visit((SQLMethodInvokeExpr) x);
        return false;
    }
}
