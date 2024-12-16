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
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveLoadDataStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.odps.ast.*;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.FnvHash;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public OdpsOutputVisitor() {
        this(new StringBuilder());
    }

    public OdpsOutputVisitor(boolean ucase) {
        this(new StringBuilder());
        config(VisitorFeature.OutputUCase, ucase);
    }

    public OdpsOutputVisitor(StringBuilder appender) {
        super(appender, DbType.odps);
    }

    @Override
    public boolean visit(SQLMergeStatement.WhenUpdate x) {
        print0(ucase ? "WHEN MATCHED" : "when matched");
        this.indentCount++;

        SQLExpr where = x.getWhere();
        if (where != null) {
            this.indentCount++;
            if (SQLBinaryOpExpr.isAnd(where)) {
                println();
            } else {
                print(' ');
            }

            print0(ucase ? "AND " : "and ");

            printExpr(where, parameterized);
            this.indentCount--;
            println();
        } else {
            print(' ');
        }
        println(ucase ? "THEN UPDATE" : "then update");
        incrementIndent();
        print(ucase ? "SET " : "set ");
        printlnAndAccept(x.getItems(), ",");
        decrementIndent();
        this.indentCount--;

        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        if (x instanceof OdpsCreateTableStatement) {
            return visit((OdpsCreateTableStatement) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(OdpsCreateTableStatement x) {
        List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                visit((SQLCommentHint) hint);
                println();
            }
        }

        if (x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "CREATE " : "create ");

        printCreateTableFeatures(x);

        if (x.isIfNotExists()) {
            print0(ucase ? "TABLE IF NOT EXISTS " : "table if not exists ");
        } else {
            print0(ucase ? "TABLE " : "table ");
        }

        x.getName().accept(this);
        printCreateTableLike(x);
        printTableElementsWithComment(x);
        printComment(x.getComment());
        printPartitionedBy(x);
        printClusteredBy(x);
        printSortedBy(x.getSortedBy());
        printIntoBuckets(x.getBuckets());
        printIntoShards(x.getShards());
        printRowFormat(x);
        printStoredBy(x.getStoredBy());
        printStoredAs(x);
        printSerdeProperties(x);
        printLocation(x);
        printTableOptions(x);
        printLifeCycle(x.getLifeCycle());
        printUsing(x);
        printSelectAs(x, true);
        return false;
    }

    protected void printPartitionedBy(OdpsCreateTableStatement x) {
        super.printPartitionedBy(x);
        SQLAliasedExpr autoPartitionedBy = x.getAutoPartitionedBy();
        if (autoPartitionedBy != null) {
            println();
            print0(ucase ? "AUTO PARTITIONED BY (" : "auto partitioned by (");
            autoPartitionedBy.accept(this);
            print(")");
        }
    }

    protected void printSerdeProperties(OdpsCreateTableStatement x) {
        List<SQLExpr> withSerdeproperties = x.getWithSerdeproperties();
        if (withSerdeproperties.size() > 0) {
            println();
            print0(ucase ? "WITH SERDEPROPERTIES (" : "with serdeproperties (");
            printAndAccept(withSerdeproperties, ", ");
            print(')');
        }
    }

    public boolean visit(SQLDecimalExpr x) {
        BigDecimal value = x.getValue();
        print(value.toString());
        print("BD");

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

        SQLPivot pivot = x.getPivot();
        if (pivot != null) {
            println();
            pivot.accept(this);
        }

        SQLUnpivot unpivot = x.getUnpivot();
        if (unpivot != null) {
            println();
            unpivot.accept(this);
        }

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        return false;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        SQLTableSource left = x.getLeft();
        left.accept(this);

        SQLTableSource right = x.getRight();
        JoinType joinType = x.getJoinType();
        if (joinType == JoinType.CROSS_JOIN
                && right instanceof SQLUnnestTableSource
        ) {
            SQLUnnestTableSource unnest = (SQLUnnestTableSource) right;
            if (unnest.isOrdinality()) {
                print0(ucase ? " LATERAL VIEW POSEXPLODE(" : " lateral view posexplode(");
            } else {
                print0(ucase ? " LATERAL VIEW EXPLODE(" : " lateral view explode(");
            }
            List<SQLExpr> items = unnest.getItems();
            printAndAccept(items, ", ");
            print(')');

            if (right.getAlias() != null) {
                print(' ');
                print0(right.getAlias());
            }

            final List<SQLName> columns = unnest.getColumns();
            if (columns != null && columns.size() > 0) {
                print0(ucase ? " AS " : " as ");
                printAndAccept(unnest.getColumns(), ", ");
            }

            return false;
        }

        if (joinType == JoinType.COMMA) {
            print(',');
        } else {
            println();
            printJoinType(joinType);
        }

        if (!(right instanceof SQLLateralViewTableSource)) {
            print(' ');
        }
        right.accept(this);

        if (x.getCondition() != null) {
            println();
            print0(ucase ? "ON " : "on ");
            this.indentCount++;
            x.getCondition().accept(this);
            this.indentCount--;
            if (x.getAfterCommentsDirect() != null) {
                printAfterComments(x.getAfterCommentsDirect());
                println();
            }
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

        printFrom(x);
        printWhere(x);
        printGroupBy(x);
        printWindow(x);
        printQualify(x);
        printOrderBy(x);

        SQLZOrderBy zorderBy = x.getZOrderBy();
        if (zorderBy != null) {
            println();
            zorderBy.accept(this);
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

        printLimit(x);

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

    public boolean visit(SQLZOrderBy x) {
        int itemSize = x.getItems().size();
        if (itemSize > 0) {
            print0(ucase ? "ZORDER BY " : "zorder by ");
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
        List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                visit((SQLCommentHint) hint);
                println();
            }
        }

        if (x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

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
        if (owner instanceof SQLMethodInvokeExpr || owner instanceof SQLPropertyExpr) {
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
            print0(ucase ? joinType.name : joinType.nameLCase);
        }
    }

    public boolean visit(SQLDataType x) {
        String dataTypeName = x.getName();
        if (dataTypeName.indexOf('<') != -1 || dataTypeName.equals("Object")) {
            print0(dataTypeName);
        } else {
            print0(ucase ? dataTypeName.toUpperCase() : dataTypeName.toLowerCase());
        }

        if (x.getArguments().size() > 0) {
            print('(');
            printAndAccept(x.getArguments(), ", ");
            print(')');
        }

        return false;
    }

    protected void printFunctionName(String name) {
        if (name == null) {
            return;
        }

        String upperName = name.toUpperCase();
        if (builtInFunctions.contains(upperName)) {
            print0(ucase ? upperName : name);
        } else {
            print0(name);
        }
    }

    @Override
    public boolean visit(OdpsShowGrantsStmt x) {
        List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                visit((SQLCommentHint) hint);
                println();
            }
        }

        if (x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

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
            x.getLabel().accept(this);
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

        List<SQLAssignItem> partitions = x.getPartitions();
        if (partitions.size() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(partitions, ", ");
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

        SQLName toPackage = x.getToPackage();
        if (toPackage != null) {
            print0(ucase ? " TO PACKAGE " : " to package ");
            printExpr(toPackage);

            List<SQLPrivilegeItem> privileges = x.getPrivileges();
            if (!privileges.isEmpty()) {
                print0(ucase ? " WITH PRIVILEGES " : " with privileges ");
                printAndAccept(privileges, ", ");
            }
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
    public boolean visit(OdpsAlterTableChangeOwner x) {
        print0(ucase ? "CHANGEOWNER TO " : "changeowner to ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsAlterTableSetFileFormat x) {
        print0(ucase ? "SET FILEFORMAT " : "set fileformat ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(OdpsCountStatement x) {
        List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                visit((SQLCommentHint) hint);
                println();
            }
        }

        if (x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "COUNT " : "count ");
        x.getTable().accept(this);

        List<SQLAssignItem> partitions = x.getPartitions();
        if (partitions.size() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(partitions, ", ");
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

        List<SQLColumnDefinition> outputColumns = x.getOutputColumns();
        if (!outputColumns.isEmpty()) {
            println();
            print0(ucase ? "AS (" : "as (");
            printAndAccept(outputColumns, ", ");
            print(')');
        }

        SQLExternalRecordFormat inputRowFormat = x.getInputRowFormat();
        if (inputRowFormat != null) {
            println();
            print0(ucase ? "ROW FORMAT DELIMITED" : "row format delimited");
            inputRowFormat.accept(this);
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

        printStoredBy(x.getStoredBy());

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
            printExpr(storedAs);
        }

        SQLExpr using = x.getUsing();
        if (using != null) {
            println();
            print0(ucase ? "USING " : "using ");
            printExpr(using);
        }

        return false;
    }

    @Override
    public boolean visit(OdpsNewExpr x) {
        print0("new ");
        super.visit((SQLMethodInvokeExpr) x);
        return false;
    }

    public boolean visit(OdpsInstallPackageStatement x) {
        print0(ucase ? "INSTALL PACKAGE " : "install package ");
        printExpr(x.getPackageName());
        return false;
    }

    public boolean visit(OdpsPAIStmt x) {
        print0(ucase ? "PAI " : "pai ");
        print0(x.getArguments());
        return false;
    }

    public boolean visit(OdpsCopyStmt x) {
        print0(ucase ? "COPY " : "copy ");
        print0(x.getArguments());
        return false;
    }

    @Override
    public boolean visit(SQLCurrentTimeExpr x) {
        final SQLCurrentTimeExpr.Type type = x.getType();
        print(ucase ? type.name : type.nameLCase);
        print0("()");
        return false;
    }

    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        print0(ucase ? "DROP " : "drop ");

        if (x.isPhysical()) {
            print0(ucase ? "PHYSICAL " : "physical ");
        }

        print0(ucase ? "SCHEMA " : "schema ");

        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }

        x.getDatabase().accept(this);

        final Boolean restrict = x.getRestrict();
        if (restrict != null && restrict.booleanValue()) {
            print0(ucase ? " RESTRICT" : " restrict");
        }

        if (x.isCascade()) {
            print0(ucase ? " CASCADE" : " cascade");
        }

        return false;
    }

    protected void printMethodParameters(SQLMethodInvokeExpr x) {
        List<SQLExpr> arguments = x.getArguments();

        boolean needPrintLine = false;
        if (arguments.size() > 10
                && (arguments.size() % 2) == 0
                && (x.methodNameHashCode64() == FnvHash.Constants.NAMED_STRUCT
                || x.methodNameHashCode64() == FnvHash.Constants.MAP)
        ) {
            needPrintLine = true;
        }
        if (needPrintLine) {
            print0('(');
            incrementIndent();
            println();
            for (int i = 0, size = arguments.size(); i < size; i += 2) {
                if (i != 0) {
                    print0(',');
                    println();
                }

                SQLExpr arg0 = arguments.get(i);
                SQLExpr arg1 = arguments.get(i + 1);
                printExpr(arg0);
                this.print0(", ");
                printExpr(arg1);
            }
            decrementIndent();
            println();
            print0(')');
            return;
        }
        super.printMethodParameters(x);
    }

    @Override
    public void printMergeInsertRow() {
        print(" *");
    }
}
