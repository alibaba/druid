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
package com.alibaba.druid.sql.dialect.oracle.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.*;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.*;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.NestedTablePartitionSpec;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.TableSpaceItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.UpdateIndexesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleFunctionDataType;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleProcedureDataType;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class OracleOutputVisitor extends SQLASTOutputVisitor implements OracleASTVisitor {

    private final boolean printPostSemi;
    {
        this.dbType = DbType.oracle;
    }

    public OracleOutputVisitor(Appendable appender){
        this(appender, true);
    }

    public OracleOutputVisitor(Appendable appender, boolean printPostSemi){
        super(appender);
        this.printPostSemi = printPostSemi;
    }

    public boolean isPrintPostSemi() {
        return printPostSemi;
    }

    private void printHints(List<SQLHint> hints) {
        if (hints.size() > 0) {
            print0("/*+ ");
            printAndAccept(hints, ", ");
            print0(" */");
        }
    }

    public boolean visit(OracleAnalytic x) {
        print0(ucase ? "(" : "(");
        
        boolean space = false;
        if (x.getPartitionBy().size() > 0) {
            print0(ucase ? "PARTITION BY " : "partition by ");
            printAndAccept(x.getPartitionBy(), ", ");

            space = true;
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            if (space) {
                print(' ');
            }
            visit(orderBy);
            space = true;
        }

        OracleAnalyticWindowing windowing = x.getWindowing();
        if (windowing != null) {
            if (space) {
                print(' ');
            }
            visit(windowing);
        }

        if (x.isWindowingPreceding()) {
            print0(ucase ? " PRECEDING" : " preceding");
        }

        print(')');
        
        return false;
    }

    public boolean visit(OracleAnalyticWindowing x) {
        print0(x.getType().name().toUpperCase());
        print(' ');
        x.getExpr().accept(this);
        return false;
    }



    public boolean visit(OracleDeleteStatement x) {
        print0(ucase ? "DELETE " : "delete ");

        SQLTableSource tableSource = x.getTableSource();
        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

        print0(ucase ? "FROM " : "from ");
        if (x.isOnly()) {
            print0(ucase ? "ONLY (" : "only (");
            x.getTableName().accept(this);
            print(')');

            printAlias(x.getAlias());
        } else {
            x.getTableSource().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            this.indentCount++;
            print0(ucase ? "WHERE " : "where ");
            x.getWhere().accept(this);
            this.indentCount--;
        }

        if (x.getReturning() != null) {
            println();
            x.getReturning().accept(this);
        }

        return false;
    }

    public boolean visit(OracleIntervalExpr x) {
        SQLExpr value = x.getValue();
        if (value instanceof SQLLiteralExpr || value instanceof SQLVariantRefExpr) {
            print0(ucase ? "INTERVAL " : "interval ");
            value.accept(this);
            print(' ');
        } else {
            print('(');
            value.accept(this);
            print0(") ");
        }

        print0(x.getType().name());

        if (x.getPrecision() != null) {
            print('(');
            printExpr(x.getPrecision(), parameterized);
            if (x.getFactionalSecondsPrecision() != null) {
                print0(", ");
                print(x.getFactionalSecondsPrecision().intValue());
            }
            print(')');
        }

        if (x.getToType() != null) {
            print0(ucase ? " TO " : " to ");
            print0(x.getToType().name());
            if (x.getToFactionalSecondsPrecision() != null) {
                print('(');
                printExpr(x.getToFactionalSecondsPrecision(), parameterized);
                print(')');
            }
        }

        return false;
    }

    public boolean visit(OracleOuterExpr x) {
        x.getExpr().accept(this);
        print0("(+)");
        return false;
    }

    public boolean visit(SQLScriptCommitStatement astNode) {
        print('/');
        println();
        return false;
    }

    public boolean visit(SQLSelect x) {
        SQLWithSubqueryClause with = x.getWithSubQuery();
        if (with != null) {
            with.accept(this);
            println();
        }

        SQLSelectQuery query = x.getQuery();
        query.accept(this);

        if (x.getRestriction() != null) {
            println();
            print("WITH ");
            x.getRestriction().accept(this);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            boolean hasFirst = false;
            if (query instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                hasFirst = queryBlock.getFirst() != null;
            }

            if (!hasFirst) {
                println();
                orderBy.accept(this);
            }
        }

        return false;
    }

    public boolean visit(OracleSelectJoin x) {
        x.getLeft().accept(this);
        SQLTableSource right = x.getRight();

        if (x.getJoinType() == JoinType.COMMA) {
            print0(", ");
            x.getRight().accept(this);
        } else {
            boolean isRoot = x.getParent() instanceof SQLSelectQueryBlock;
            if (isRoot) {
                this.indentCount++;
            }

            println();
            print0(ucase ? x.getJoinType().name : x.getJoinType().name_lcase);
            print(' ');

            if (right instanceof SQLJoinTableSource) {
                print('(');
                right.accept(this);
                print(')');
            } else {
                right.accept(this);
            }

            if (isRoot) {
                this.indentCount--;
            }

            if (x.getCondition() != null) {
                print0(ucase ? " ON " : " on ");
                incrementIndent();
                x.getCondition().accept(this);
                decrementIndent();
                print(' ');
            }

            if (x.getUsing().size() > 0) {
                print0(ucase ? " USING (" : " using (");
                printAndAccept(x.getUsing(), ", ");
                print(')');
            }

            printFlashback(x.getFlashback());
        }

        OracleSelectPivotBase pivot = x.getPivot();
        if (pivot != null) {
            println();
            pivot.accept(this);
        }

        return false;
    }

    public boolean visit(SQLSelectOrderByItem x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(' ');

            String typeName = x.getType().name();
            print0(ucase ? typeName.toUpperCase() : typeName.toLowerCase());
        }

        if (x.getNullsOrderType() != null) {
            print(' ');
            print0(x.getNullsOrderType().toFormalString());
        }

        return false;
    }

    public boolean visit(OracleSelectPivot x) {
        print0(ucase ? "PIVOT" : "pivot");
        if (x.isXml()) {
            print0(ucase ? " XML" : " xml");
        }
        print0(" (");
        printAndAccept(x.getItems(), ", ");

        if (x.getPivotFor().size() > 0) {
            print0(ucase ? " FOR " : " for ");
            if (x.getPivotFor().size() == 1) {
                ((SQLExpr) x.getPivotFor().get(0)).accept(this);
            } else {
                print('(');
                printAndAccept(x.getPivotFor(), ", ");
                print(')');
            }
        }

        if (x.getPivotIn().size() > 0) {
            print0(ucase ? " IN (" : " in (");
            printAndAccept(x.getPivotIn(), ", ");
            print(')');
        }

        print(')');

        return false;
    }

    public boolean visit(Item x) {
        x.getExpr().accept(this);
        if ((x.getAlias() != null) && (x.getAlias().length() > 0)) {
            print0(ucase ? " AS " : " as ");
            print0(x.getAlias());
        }
        return false;
    }

    public boolean visit(SQLSelectQueryBlock select) {
        if (select instanceof OracleSelectQueryBlock) {
            return visit((OracleSelectQueryBlock) select);
        }

        return super.visit(select);
    }

    public boolean visit(OracleSelectQueryBlock x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "SELECT " : "select ");

        if (x.getHintsSize() > 0) {
            printAndAccept(x.getHints(), ", ");
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

        if (x.getInto() != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);
        }

        println();
        print0(ucase ? "FROM " : "from ");
        if (x.getFrom() == null) {
            print0(ucase ? "DUAL" : "dual");
        } else {
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            x.getWhere().accept(this);
        }

        printHierarchical(x);

        if (x.getGroupBy() != null) {
            println();
            x.getGroupBy().accept(this);
        }

        if (x.getModelClause() != null) {
            println();
            x.getModelClause().accept(this);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        printFetchFirst(x);

        if (x.isForUpdate()) {
            println();
            print0(ucase ? "FOR UPDATE" : "for update");
            if (x.getForUpdateOfSize() > 0) {
                print('(');
                printAndAccept(x.getForUpdateOf(), ", ");
                print(')');
            }

            if (x.isNoWait()) {
                print0(ucase ? " NOWAIT" : " nowait");
            } else if (x.isSkipLocked()) {
                print0(ucase ? " SKIP LOCKED" : " skip locked");
            } else if (x.getWaitTime() != null) {
                print0(ucase ? " WAIT " : " wait ");
                x.getWaitTime().accept(this);
            }
        }

        return false;
    }

    public boolean visit(CheckOption x) {
        print0(ucase ? "CHECK OPTION" : "check option");
        if (x.getConstraint() != null) {
            print0(ucase ? " CONSTRAINT" : " constraint");
            print(' ');
            x.getConstraint().accept(this);
        }
        return false;
    }

    public boolean visit(ReadOnly x) {
        print0(ucase ? "READ ONLY" : "read only");
        if (x.getConstraint() != null) {
            print0(ucase ? " CONSTRAINT" : " constraint");
            print(' ');
            x.getConstraint().accept(this);
        }
        return false;
    }

    public boolean visit(OracleSelectSubqueryTableSource x) {
        print('(');
        this.indentCount++;
        println();
        x.getSelect().accept(this);
        this.indentCount--;
        println();
        print(')');

        OracleSelectPivotBase pivot = x.getPivot();
        if (pivot != null) {
            println();
            pivot.accept(this);
        }

        printFlashback(x.getFlashback());

        if ((x.getAlias() != null) && (x.getAlias().length() != 0)) {
            print(' ');
            print0(x.getAlias());
        }

        return false;
    }

    public boolean visit(OracleSelectTableReference x) {
        if (x.isOnly()) {
            print0(ucase ? "ONLY (" : "only (");
            printTableSourceExpr(x.getExpr());

            if (x.getPartition() != null) {
                print(' ');
                x.getPartition().accept(this);
            }

            print(')');
        } else {
            printTableSourceExpr(x.getExpr());

            if (x.getPartition() != null) {
                print(' ');
                x.getPartition().accept(this);
            }
        }

        if (x.getHints().size() > 0) {
            this.printHints(x.getHints());
        }

        if (x.getSampleClause() != null) {
            print(' ');
            x.getSampleClause().accept(this);
        }

        if (x.getPivot() != null) {
            println();
            x.getPivot().accept(this);
        }

        printFlashback(x.getFlashback());

        printAlias(x.getAlias());

        return false;
    }

    private void printFlashback(SQLExpr flashback) {
        if (flashback == null) {
            return;
        }

        println();

        if (flashback instanceof SQLBetweenExpr) {
            flashback.accept(this);
        } else {
            print0(ucase ? "AS OF " : "as of ");
            flashback.accept(this);
        }
    }

    public boolean visit(OracleSelectUnPivot x) {
        print0(ucase ? "UNPIVOT" : "unpivot");
        if (x.getNullsIncludeType() != null) {
            print(' ');
            print0(OracleSelectUnPivot.NullsIncludeType.toString(x.getNullsIncludeType(), ucase));
        }

        print0(" (");
        if (x.getItems().size() == 1) {
            ((SQLExpr) x.getItems().get(0)).accept(this);
        } else {
            print0(" (");
            printAndAccept(x.getItems(), ", ");
            print(')');
        }

        if (x.getPivotFor().size() > 0) {
            print0(ucase ? " FOR " : " for ");
            if (x.getPivotFor().size() == 1) {
                ((SQLExpr) x.getPivotFor().get(0)).accept(this);
            } else {
                print('(');
                printAndAccept(x.getPivotFor(), ", ");
                print(')');
            }
        }

        if (x.getPivotIn().size() > 0) {
            print0(ucase ? " IN (" : " in (");
            printAndAccept(x.getPivotIn(), ", ");
            print(')');
        }

        print(')');
        return false;
    }

    public boolean visit(OracleUpdateStatement x) {
        print0(ucase ? "UPDATE " : "update ");
        
        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

        if (x.isOnly()) {
            print0(ucase ? "ONLY (" : "only (");
            x.getTableSource().accept(this);
            print(')');
        } else {
            x.getTableSource().accept(this);
        }

        printAlias(x.getAlias());

        println();

        print0(ucase ? "SET " : "set ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getItems().get(i).accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            this.indentCount++;
            x.getWhere().accept(this);
            this.indentCount--;
        }

        if (x.getReturning().size() > 0) {
            println();
            print0(ucase ? "RETURNING " : "returning ");
            printAndAccept(x.getReturning(), ", ");
            print0(ucase ? " INTO " : " into ");
            printAndAccept(x.getReturningInto(), ", ");
        }

        return false;
    }

    @Override
    public boolean visit(SampleClause x) {
        print0(ucase ? "SAMPLE " : "sample ");

        if (x.isBlock()) {
            print0(ucase ? "BLOCK " : "block ");
        }

        print('(');
        printAndAccept(x.getPercent(), ", ");
        print(')');

        if (x.getSeedValue() != null) {
            print0(ucase ? " SEED (" : " seed (");
            x.getSeedValue().accept(this);
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(PartitionExtensionClause x) {
        if (x.isSubPartition()) {
            print0(ucase ? "SUBPARTITION " : "subpartition ");
        } else {
            print0(ucase ? "PARTITION " : "partition ");
        }

        if (x.getPartition() != null) {
            print('(');
            x.getPartition().accept(this);
            print(')');
        } else {
            print0(ucase ? "FOR (" : "for (");
            printAndAccept(x.getFor(), ",");
            print(')');
        }
        return false;
    }

//    @Override
//    public boolean visit(VersionsFlashbackQueryClause x) {
//        print0(ucase ? "VERSIONS BETWEEN " : "versions between ");
//        print0(x.getType().name());
//        print(' ');
//        x.getBegin().accept(this);
//        print0(ucase ? " AND " : " and ");
//        x.getEnd().accept(this);
//        return false;
//    }
//
//    @Override
//    public void endVisit(VersionsFlashbackQueryClause x) {
//
//    }
//
//    @Override
//    public boolean visit(AsOfFlashbackQueryClause x) {
//        print0(ucase ? "AS OF " : "as of ");
//        print0(x.getType().name());
//        print0(" (");
//        x.getExpr().accept(this);
//        print(')');
//        return false;
//    }
//
//    @Override
//    public void endVisit(AsOfFlashbackQueryClause x) {
//
//    }

    @Override
    public boolean visit(OracleWithSubqueryEntry x) {
        print0(x.getAlias());

        if (x.getColumns().size() > 0) {
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }

        print0(ucase ? " AS " : " as ");
        print('(');
        this.indentCount++;
        println();
        x.getSubQuery().accept(this);
        this.indentCount--;
        println();
        print(')');

        if (x.getSearchClause() != null) {
            println();
            x.getSearchClause().accept(this);
        }

        if (x.getCycleClause() != null) {
            println();
            x.getCycleClause().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SearchClause x) {
        print0(ucase ? "SEARCH " : "search ");
        print0(x.getType().name());
        print0(ucase ? " FIRST BY " : " first by ");
        printAndAccept(x.getItems(), ", ");
        print0(ucase ? " SET " : " set ");
        x.getOrderingColumn().accept(this);

        return false;
    }

    @Override
    public boolean visit(CycleClause x) {
        print0(ucase ? "CYCLE " : "cycle ");
        printAndAccept(x.getAliases(), ", ");
        print0(ucase ? " SET " : " set ");
        x.getMark().accept(this);
        print0(ucase ? " TO " : " to ");
        x.getValue().accept(this);
        print0(ucase ? " DEFAULT " : " default ");
        x.getDefaultValue().accept(this);

        return false;
    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {
        print0(x.getValue().toString());
        print('F');
        return false;
    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {
        print0(x.getValue().toString());
        print('D');
        return false;
    }

    @Override
    public boolean visit(OracleIsSetExpr x) {
        x.getNestedTable().accept(this);
        print0(ucase ? " IS A SET" : " is a set");
        return false;
    }

    @Override
    public boolean visit(ReturnRowsClause x) {
        if (x.isAll()) {
            print0(ucase ? "RETURN ALL ROWS" : "return all rows");
        } else {
            print0(ucase ? "RETURN UPDATED ROWS" : "return updated rows");
        }
        return false;
    }

    @Override
    public boolean visit(ModelClause x) {
        print0(ucase ? "MODEL" : "model");

        this.indentCount++;
        for (CellReferenceOption opt : x.getCellReferenceOptions()) {
            print(' ');
            print0(opt.name);
        }

        if (x.getReturnRowsClause() != null) {
            print(' ');
            x.getReturnRowsClause().accept(this);
        }

        for (ReferenceModelClause item : x.getReferenceModelClauses()) {
            print(' ');
            item.accept(this);
        }

        x.getMainModel().accept(this);
        this.indentCount--;

        return false;
    }

    @Override
    public boolean visit(MainModelClause x) {
        if (x.getMainModelName() != null) {
            print0(ucase ? " MAIN " : " main ");
            x.getMainModelName().accept(this);
        }

        println();
        x.getModelColumnClause().accept(this);

        for (CellReferenceOption opt : x.getCellReferenceOptions()) {
            println();
            print0(opt.name);
        }

        println();
        x.getModelRulesClause().accept(this);

        return false;
    }

    @Override
    public boolean visit(ModelColumnClause x) {
        if (x.getQueryPartitionClause() != null) {
            x.getQueryPartitionClause().accept(this);
            println();
        }

        print0(ucase ? "DIMENSION BY (" : "dimension by (");
        printAndAccept(x.getDimensionByColumns(), ", ");
        print(')');

        println();
        print0(ucase ? "MEASURES (" : "measures (");
        printAndAccept(x.getMeasuresColumns(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(QueryPartitionClause x) {
        print0(ucase ? "PARTITION BY (" : "partition by (");
        printAndAccept(x.getExprList(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(ModelColumn x) {
        x.getExpr().accept(this);
        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }
        return false;
    }

    @Override
    public boolean visit(ModelRulesClause x) {
        if (x.getOptions().size() > 0) {
            print0(ucase ? "RULES" : "rules");
            for (ModelRuleOption opt : x.getOptions()) {
                print(' ');
                print0(opt.name);
            }
        }

        if (x.getIterate() != null) {
            print0(ucase ? " ITERATE (" : " iterate (");
            x.getIterate().accept(this);
            print(')');

            if (x.getUntil() != null) {
                print0(ucase ? " UNTIL (" : " until (");
                x.getUntil().accept(this);
                print(')');
            }
        }

        print0(" (");
        printAndAccept(x.getCellAssignmentItems(), ", ");
        print(')');
        return false;

    }

    @Override
    public boolean visit(CellAssignmentItem x) {
        if (x.getOption() != null) {
            print0(x.getOption().name);
            print(' ');
        }

        x.getCellAssignment().accept(this);

        if (x.getOrderBy() != null) {
            print(' ');
            x.getOrderBy().accept(this);
        }

        print0(" = ");
        x.getExpr().accept(this);

        return false;
    }

    @Override
    public boolean visit(CellAssignment x) {
        x.getMeasureColumn().accept(this);
        print0("[");
        printAndAccept(x.getConditions(), ", ");
        print0("]");
        return false;
    }

    @Override
    public boolean visit(OracleReturningClause x) {
        print0(ucase ? "RETURNING " : "returning ");
        printAndAccept(x.getItems(), ", ");
        print0(ucase ? " INTO " : " into ");
        printAndAccept(x.getValues(), ", ");

        return false;
    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        //visit((SQLInsertStatement) x);
        
        print0(ucase ? "INSERT " : "insert ");
        
        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

        print0(ucase ? "INTO " : "into ");
        
        x.getTableSource().accept(this);

        printInsertColumns(x.getColumns());

        if (x.getValues() != null) {
            println();
            print0(ucase ? "VALUES " : "values ");
            x.getValues().accept(this);
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().accept(this);
            }
        }

        if (x.getReturning() != null) {
            println();
            x.getReturning().accept(this);
        }

        if (x.getErrorLogging() != null) {
            println();
            x.getErrorLogging().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(InsertIntoClause x) {
        print0(ucase ? "INTO " : "into ");

        x.getTableSource().accept(this);

        if (x.getColumns().size() > 0) {
            this.indentCount++;
            println();
            print('(');
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }
                x.getColumns().get(i).accept(this);
            }
            print(')');
            this.indentCount--;
        }

        if (x.getValues() != null) {
            println();
            print0(ucase ? "VALUES " : "values ");
            x.getValues().accept(this);
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().accept(this);
            }
        }

        return false;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        print0(ucase ? "INSERT " : "insert ");

        if (x.getHints().size() > 0) {
            this.printHints(x.getHints());
        }

        if (x.getOption() != null) {
            print0(x.getOption().name());
            print(' ');
        }

        for (int i = 0, size = x.getEntries().size(); i < size; ++i) {
            this.indentCount++;
            println();
            x.getEntries().get(i).accept(this);
            this.indentCount--;
        }

        println();
        x.getSubQuery().accept(this);

        return false;
    }

    @Override
    public boolean visit(ConditionalInsertClause x) {
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }

            ConditionalInsertClauseItem item = x.getItems().get(i);

            item.accept(this);
        }

        if (x.getElseItem() != null) {
            println();
            print0(ucase ? "ELSE" : "else");
            this.indentCount++;
            println();
            x.getElseItem().accept(this);
            this.indentCount--;
        }

        return false;
    }

    @Override
    public boolean visit(ConditionalInsertClauseItem x) {
        print0(ucase ? "WHEN " : "when ");
        x.getWhen().accept(this);
        print0(ucase ? " THEN" : " then");
        this.indentCount++;
        println();
        x.getThen().accept(this);
        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        print0(ucase ? "LOCK TABLE " : "lock table ");
        x.getTable().accept(this);
        if (x.getPartition() != null) {
            print0(" PARTITION (");
            x.getPartition().accept(this);
            print0(") ");
        }
        print0(ucase ? " IN " : " in ");
        print0(x.getLockMode().toString());
        print0(ucase ? " MODE " : " mode ");
        if (x.isNoWait()) {
            print0(ucase ? "NOWAIT" : "nowait");
        } else if (x.getWait() != null) {
            print0(ucase ? "WAIT " : "wait ");
            x.getWait().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        print0(ucase ? "ALTER SESSION SET " : "alter session set ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        x.getExpr().accept(this);
        SQLExpr timeZone = x.getTimeZone();

        if (timeZone instanceof SQLIdentifierExpr) {
            if (((SQLIdentifierExpr) timeZone).getName().equalsIgnoreCase("LOCAL")) {
                print0(ucase ? " AT LOCAL" : "alter session set ");
                return false;
            }
        }

        print0(ucase ? " AT TIME ZONE " : " at time zone ");
        timeZone.accept(this);

        return false;
    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        print0(ucase ? "SYSDATE" : "sysdate");
        if (x.getOption() != null) {
            print('@');
            print0(x.getOption());
        }
        return false;
    }

    @Override
    public boolean visit(OracleExceptionStatement.Item x) {
        print0(ucase ? "WHEN " : "when ");
        x.getWhen().accept(this);
        print0(ucase ? " THEN" : " then");

        this.indentCount++;
        if (x.getStatements().size() > 1) {
            println();
        } else {
            if (x.getStatements().size() == 1
                    && x.getStatements().get(0) instanceof SQLIfStatement) {
                println();
            } else {
                print(' ');
            }
        }

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            if (i != 0 && size > 1) {
                println();
            }
            SQLStatement stmt = x.getStatements().get(i);
            stmt.accept(this);
        }

        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(OracleExceptionStatement x) {
        print0(ucase ? "EXCEPTION" : "exception");
        this.indentCount++;
        List<OracleExceptionStatement.Item> items = x.getItems();
        for (int i = 0, size = items.size(); i < size; ++i) {
            println();
            OracleExceptionStatement.Item item = items.get(i);
            item.accept(this);
        }
        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        print0(x.getArgumentName());
        print0(" => ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        if (x.isReadOnly()) {
            print0(ucase ? "SET TRANSACTION READ ONLY" : "set transaction read only");
        } else {
            print0(ucase ? "SET TRANSACTION" : "set transaction");
        }

        SQLExpr name = x.getName();
        if (name != null) {
            print0(ucase ? " NAME " : " name ");
            name.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        print0(ucase ? "EXPLAIN PLAN" : "explain plan");
        this.indentCount++;
        println();
        if (x.getStatementId() != null) {
            print0(ucase ? "SET STATEMENT_ID = " : "set statement_id = ");
            x.getStatementId().accept(this);
            println();
        }

        if (x.getInto() != null) {
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);
            println();
        }

        print0(ucase ? "FOR" : "for");
        println();
        x.getStatement().accept(this);

        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(SQLAlterProcedureStatement x) {
        print0(ucase ? "ALTER PROCEDURE " : "alter procedure ");
        x.getName().accept(this);
        if (x.isCompile()) {
            print0(ucase ? " COMPILE" : " compile");
        }
        if (x.isReuseSettings()) {
            print0(ucase ? " REUSE SETTINGS" : " reuse settings");
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        print0(ucase ? "DROP PARTITION " : "drop partition ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        if (x.getItems().size() == 1) {
            SQLAlterTableItem item = x.getItems().get(0);
            if (item instanceof SQLAlterTableRename) {
                SQLExpr to = ((SQLAlterTableRename) item).getTo().getExpr();

                print0(ucase ? "RENAME " : "rename ");
                x.getName().accept(this);
                print0(ucase ? " TO " : "to ");
                to.accept(this);
                return false;
            }
        }

        print0(ucase ? "ALTER TABLE " : "alter table ");
        printTableSourceExpr(x.getName());
        this.indentCount++;
        for (SQLAlterTableItem item : x.getItems()) {
            println();
            item.accept(this);
        }
        if (x.isUpdateGlobalIndexes()) {
            println();
            print0(ucase ? "UPDATE GLOABL INDEXES" : "update gloabl indexes");
        }
        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        print0(ucase ? "TRUNCATE PARTITION " : "truncate partition ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(TableSpaceItem x) {
        print0(ucase ? "TABLESPACE " : "tablespace ");
        x.getTablespace().accept(this);
        return false;
    }

    @Override
    public boolean visit(UpdateIndexesClause x) {
        print0(ucase ? "UPDATE INDEXES" : "update indexes");
        if (x.getItems().size() > 0) {
            print('(');
            printAndAccept(x.getItems(), ", ");
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        print0(ucase ? "SPLIT PARTITION " : "split partition ");
        x.getName().accept(this);

        if (x.getAt().size() > 0) {
            this.indentCount++;
            println();
            print0(ucase ? "AT (" : "at (");
            printAndAccept(x.getAt(), ", ");
            print(')');
            this.indentCount--;
        }

        if (x.getInto().size() > 0) {
            println();
            this.indentCount++;
            print0(ucase ? "INTO (" : "into (");
            printAndAccept(x.getInto(), ", ");
            print(')');
            this.indentCount--;
        }

        if (x.getUpdateIndexes() != null) {
            println();
            this.indentCount++;
            x.getUpdateIndexes().accept(this);
            this.indentCount--;
        }
        return false;
    }

    @Override
    public boolean visit(NestedTablePartitionSpec x) {
        print0(ucase ? "PARTITION " : "partition ");
        x.getPartition().accept(this);
        for (SQLObject item : x.getSegmentAttributeItems()) {
            print(' ');
            item.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableModify x) {
        print0(ucase ? "MODIFY (" : "modify (");
        this.indentCount++;
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            println();
            SQLColumnDefinition column = x.getColumns().get(i);
            column.accept(this);
            if (i != size - 1) {
                print0(", ");
            }
        }
        this.indentCount--;
        println();
        print(')');

        return false;
    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.getType() != null) {
            print0(x.getType());
            print(' ');
        }

        print0(ucase ? "INDEX " : "index ");

        x.getName().accept(this);
        print0(ucase ? " ON " : " on ");

        if (x.isCluster()) {
            print0(ucase ? "CLUSTER " : "cluster ");
        }

        x.getTable().accept(this);

        List<SQLSelectOrderByItem> items = x.getItems();
        if (items.size() > 0) {
            print('(');
            printAndAccept(items, ", ");
            print(')');
        }

        if (x.isIndexOnlyTopLevel()) {
            println();
            print0(ucase ? "INDEX ONLY TOPLEVEL" : "index only toplevel");
        }

        if (x.isComputeStatistics()) {
            println();
            print0(ucase ? "COMPUTE STATISTICS" : "compute statistics");
        }

        if (x.isReverse()) {
            println();
            print0(ucase ? "REVERSE" : "reverse");
        }

        this.printOracleSegmentAttributes(x);

        if (x.isOnline()) {
            print0(ucase ? " ONLINE" : " online");
        }

        if (x.isNoParallel()) {
            print0(ucase ? " NOPARALLEL" : " noparallel");
        } else if (x.getParallel() != null) {
            print0(ucase ? " PARALLEL " : " parallel ");
            x.getParallel().accept(this);
        }

        Boolean sort = x.getSort();
        if (sort != null) {
            if (sort.booleanValue()) {
                print0(ucase ? " SORT" : " sort");
            } else {
                print0(ucase ? " NOSORT" : " nosort");
            }
        }

        if (x.getLocalPartitions().size() > 0) {
            println();
            print0(ucase ? "LOCAL (" : "local (");
            this.indentCount++;
            println();
            printlnAndAccept(x.getLocalPartitions(), ",");
            this.indentCount--;
            println();
            print(')');
        } else if (x.isLocal()) {
            print0(ucase ? " LOCAL" : " local");
        }

        List<SQLName> localStoreIn = x.getLocalStoreIn();
        if (localStoreIn.size() > 0) {
            print0(ucase ? " STORE IN (" : " store in (");
            printAndAccept(localStoreIn, ", ");
            print(')');
        }

        List<SQLPartitionBy> globalPartitions = x.getGlobalPartitions();
        if (globalPartitions.size() > 0) {
            for (SQLPartitionBy globalPartition : globalPartitions) {
                println();
                print0(ucase ? "GLOBAL " : "global ");
                print0(ucase ? "PARTITION BY " : "partition by ");
                globalPartition.accept(this);
            }

        } else {
            if (x.isGlobal()) {
                print0(ucase ? " GLOBAL" : " global");
            }
        }

        return false;
    }

    @Override
    public boolean visit(OracleForStatement x) {
        boolean all = x.isAll();
        if (all) {
            print0(ucase ? "FORALL " : "forall ");
        } else {
            print0(ucase ? "FOR " : "for ");
        }
        x.getIndex().accept(this);
        print0(ucase ? " IN " : " in ");

        SQLExpr range = x.getRange();
        range.accept(this);

        if (!all) {
            println();
            print0(ucase ? "LOOP" : "loop");
        }
        this.indentCount++;
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement stmt = x.getStatements().get(i);
            stmt.accept(this);
            if (!all) {
                if (i != size - 1) {
                    println();
                }
            }
        }

        this.indentCount--;
        if (!all) {
            println();
            print0(ucase ? "END LOOP" : "end loop");
            SQLName endLabel = x.getEndLabel();
            if (endLabel != null) {
                print(' ');
                endLabel.accept(this);
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        print0(ucase ? "ELSE" : "else");
        this.indentCount++;
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
        }

        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        print0(ucase ? "ELSE IF " : "else if ");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
        this.indentCount++;

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            println();
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
        }

        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement x) {
        print0(ucase ? "IF " : "if ");
        int lines = this.lines;
        this.indentCount++;
        x.getCondition().accept(this);
        this.indentCount--;

        if (lines != this.lines) {
            println();
        } else {
            print(' ');
        }
        print0(ucase ? "THEN" : "then");

        this.indentCount++;
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            println();
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
        }
        this.indentCount--;

        for (SQLIfStatement.ElseIf elseIf : x.getElseIfList()) {
            println();
            elseIf.accept(this);
        }

        if (x.getElseItem() != null) {
            println();
            x.getElseItem().accept(this);
        }
        println();
        print0(ucase ? "END IF" : "end if");
        return false;
    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        x.getLowBound().accept(this);
        print0("..");
        x.getUpBound().accept(this);
        return false;
    }

    protected void visitColumnDefault(SQLColumnDefinition x) {
        if (x.getParent() instanceof SQLBlockStatement) {
            print0(" := ");
        } else {
            print0(ucase ? " DEFAULT " : " default ");
        }
        printExpr(x.getDefaultExpr(), false);
    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "PRIMARY KEY (" : "primary key (");
        printAndAccept(x.getColumns(), ", ");
        print(')');

        Boolean rely = x.getRely();
        if (rely != null) {
            if (rely.booleanValue()) {
                print0(ucase ? " RELY" : " rely");
            }
        }

        printConstraintState(x);

        Boolean validate = x.getValidate();
        if (validate != null) {
            if (validate.booleanValue()) {
                print0(ucase ? " VALIDATE" : " validate");
            } else {
                print0(ucase ? " NOVALIDATE" : " novalidate");
            }
        }

        return false;
    }

    protected void printConstraintState(OracleConstraint x) {
        this.indentCount++;
        if (x.getUsing() != null) {
            println();
            x.getUsing().accept(this);
        }

        if (x.getExceptionsInto() != null) {
            println();
            print0(ucase ? "EXCEPTIONS INTO " : "exceptions into ");
            x.getExceptionsInto().accept(this);
        }

        Boolean enable = x.getEnable();
        if (enable != null) {
            if (enable.booleanValue()) {
                print0(ucase ? " ENABLE" : " enable");
            } else {
                print0(ucase ? " DISABLE" : " disable");
            }
        }

        if (x.getInitially() != null) {
            print0(ucase ? " INITIALLY " : " initially ");
            print0(x.getInitially().name());
        }

        if (x.getDeferrable() != null) {
            if (x.getDeferrable().booleanValue()) {
                print0(ucase ? " DEFERRABLE" : " deferrable");
            } else {
                print0(ucase ? " NOT DEFERRABLE" : " not deferrable");
            }
        }
        this.indentCount--;
    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        printCreateTable(x, false);

        if (x.getOf() != null) {
            println();
            print0(ucase ? "OF " : "of ");
            x.getOf().accept(this);
        }

        OracleCreateTableStatement.OIDIndex oidIndex = x.getOidIndex();
        if (oidIndex != null) {
            println();
            oidIndex.accept(this);
        }

        OracleCreateTableStatement.Organization organization = x.getOrganization();
        if (organization != null) {
            println();
            this.indentCount++;
            organization.accept(this);
            this.indentCount--;
        }

        if (x.getIncluding().size() > 0) {
            print0(ucase ? " INCLUDING " : " including ");
            printAndAccept(x.getIncluding(), ", ");
            print0(ucase ? " OVERFLOW " : " overflow ");
        }

        printOracleSegmentAttributes(x);

        if (x.isInMemoryMetadata()) {
            println();
            print0(ucase ? "IN_MEMORY_METADATA" : "in_memory_metadata");
        }

        if (x.isCursorSpecificSegment()) {
            println();
            print0(ucase ? "CURSOR_SPECIFIC_SEGMENT" : "cursor_specific_segment");
        }

        if (x.getParallel() == Boolean.TRUE) {
            println();
            print0(ucase ? "PARALLEL" : "parallel");

            final SQLExpr parallelValue = x.getParallelValue();
            if (parallelValue != null) {
                print(' ');
                printExpr(parallelValue);
            }
        } else if (x.getParallel() == Boolean.FALSE) {
            println();
            print0(ucase ? "NOPARALLEL" : "noparallel");
        }

        if (x.getCache() == Boolean.TRUE) {
            println();
            print0(ucase ? "CACHE" : "cache");
        } else if (x.getCache() == Boolean.FALSE) {
            println();
            print0(ucase ? "NOCACHE" : "nocache");
        }

        if (x.getLobStorage() != null) {
            println();
            x.getLobStorage().accept(this);
        }

        if (x.isOnCommitPreserveRows()) {
            println();
            print0(ucase ? "ON COMMIT PRESERVE ROWS" : "on commit preserve rows");
        } else if (x.isOnCommitDeleteRows()) {
            println();
            print0(ucase ? "ON COMMIT DELETE ROWS" : "on commit delete rows");
        }

        if (x.isMonitoring()) {
            println();
            print0(ucase ? "MONITORING" : "monitoring");
        }

        if (x.getPartitioning() != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            x.getPartitioning().accept(this);
        }

        if (x.getCluster() != null) {
            println();
            print0(ucase ? "CLUSTER " : "cluster ");
            x.getCluster().accept(this);
            print0(" (");
            printAndAccept(x.getClusterColumns(), ",");
            print0(")");
        }

        final OracleXmlColumnProperties xmlTypeColumnProperties = x.getXmlTypeColumnProperties();
        if (xmlTypeColumnProperties != null) {
            println();
            xmlTypeColumnProperties.accept(this);
        }

        final SQLSelect select = x.getSelect();
        if (select != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            select.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleStorageClause x) {
        print0(ucase ? "STORAGE (" : "storage (");

        this.indentCount++;
        final SQLExpr initial = x.getInitial();
        if (initial != null) {
            println();
            print0(ucase ? "INITIAL " : "initial ");
            printExpr(initial, false);
        }

        final SQLExpr next = x.getNext();
        if (next != null) {
            println();
            print0(ucase ? "NEXT " : "next ");
            printExpr(next, false);
        }

        final SQLExpr minExtents = x.getMinExtents();
        if (minExtents != null) {
            println();
            print0(ucase ? "MINEXTENTS " : "minextents ");
            printExpr(minExtents, false);
        }

        final SQLExpr maxExtents = x.getMaxExtents();
        if (maxExtents != null) {
            println();
            print0(ucase ? "MAXEXTENTS " : "maxextents ");
            printExpr(maxExtents, false);
        }

        final SQLExpr pctIncrease = x.getPctIncrease();
        if (pctIncrease != null) {
            println();
            print0(ucase ? "PCTINCREASE " : "pctincrease ");
            printExpr(pctIncrease, false);
        }

        final SQLExpr maxSize = x.getMaxSize();
        if (maxSize != null) {
            println();
            print0(ucase ? "MAXSIZE " : "maxsize ");
            printExpr(maxSize, false);
        }

        final SQLExpr freeLists = x.getFreeLists();
        if (freeLists != null) {
            println();
            print0(ucase ? "FREELISTS " : "freelists ");
            printExpr(freeLists, false);
        }

        final SQLExpr freeListGroups = x.getFreeListGroups();
        if (freeListGroups != null) {
            println();
            print0(ucase ? "FREELIST GROUPS " : "freelist groups ");
            printExpr(freeListGroups, false);
        }

        final SQLExpr bufferPool = x.getBufferPool();
        if (bufferPool != null) {
            println();
            print0(ucase ? "BUFFER_POOL " : "buffer_pool ");
            printExpr(bufferPool, false);
        }

        final SQLExpr objno = x.getObjno();
        if (objno != null) {
            println();
            print0(ucase ? "OBJNO " : "objno ");
            printExpr(objno, false);
        }

        if (x.getFlashCache() != null) {
            println();
            print0(ucase ? "FLASH_CACHE " : "flash_cache ");
            print0(ucase ? x.getFlashCache().name() : x.getFlashCache().name().toLowerCase());
        }

        if (x.getCellFlashCache() != null) {
            println();
            print0(ucase ? "CELL_FLASH_CACHE " : "cell_flash_cache ");
            print0(ucase ? x.getCellFlashCache().name() : x.getCellFlashCache().name().toLowerCase());
        }
        this.indentCount--;
        println();
        print(')');

        return false;
    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        print0(ucase ? "GOTO " : "GOTO ");
        x.getLabel().accept(this);
        return false;
    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        print0("<<");
        x.getLabel().accept(this);
        print0(">>");
        return false;
    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        print0(ucase ? "ALTER TRIGGER " : "alter trigger ");
        x.getName().accept(this);

        if (x.isCompile()) {
            print0(ucase ? " COMPILE" : " compile");
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print0(ucase ? "ENABLE" : "enable");
            } else {
                print0(ucase ? "DISABLE" : "disable");
            }
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        print0(ucase ? "ALTER SYNONYM " : "alter synonym ");
        x.getName().accept(this);

        if (x.isCompile()) {
            print0(ucase ? " COMPILE" : " compile");
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print0(ucase ? "ENABLE" : "enable");
            } else {
                print0(ucase ? "DISABLE" : "disable");
            }
        }
        return false;
    }

//    @Override
//    public boolean visit(AsOfSnapshotClause x) {
//        print0(ucase ? "AS OF SNAPSHOT(" : "as of snapshot(");
//        x.getExpr().accept(this);
//        print(')');
//        return false;
//    }
//
//    @Override
//    public void endVisit(AsOfSnapshotClause x) {
//
//    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        print0(ucase ? "ALTER VIEW " : "alter view ");
        x.getName().accept(this);

        if (x.isCompile()) {
            print0(ucase ? " COMPILE" : " compile");
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print0(ucase ? "ENABLE" : "enable");
            } else {
                print0(ucase ? "DISABLE" : "disable");
            }
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        print0(ucase ? " MOVE TABLESPACE " : " move tablespace ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        printAndAccept(x.getFileNames(), ", ");

        if (x.getSize() != null) {
            print0(ucase ? " SIZE " : " size ");
            x.getSize().accept(this);
        }

        if (x.isAutoExtendOff()) {
            print0(ucase ? " AUTOEXTEND OFF" : " autoextend off");
        } else if (x.getAutoExtendOn() != null) {
            print0(ucase ? " AUTOEXTEND ON " : " autoextend on ");
            x.getAutoExtendOn().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        print0(ucase ? "ADD DATAFILE" : "add datafile");
        this.indentCount++;
        for (OracleFileSpecification file : x.getFiles()) {
            println();
            file.accept(this);
        }
        this.indentCount--;
        return false;
    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        print0(ucase ? "ALTER TABLESPACE " : "alter tablespace ");
        x.getName().accept(this);
        println();
        x.getItem().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        print0(ucase ? "TRUNCATE TABLE " : "truncate table ");
        printAndAccept(x.getTableSources(), ", ");

        if (x.isPurgeSnapshotLog()) {
            print0(ucase ? " PURGE SNAPSHOT LOG" : " purge snapshot log");
        }
        return false;
    }

    @Override
    public boolean visit(OracleExitStatement x) {
        print0(ucase ? "EXIT" : "exit");

        if (x.getLabel() != null) {
            print(' ');
            print0(x.getLabel());
        }

        if (x.getWhen() != null) {
            print0(ucase ? " WHEN " : " when ");
            x.getWhen().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleContinueStatement x) {
        print0(ucase ? "CONTINUE" : "continue");

        String label = x.getLabel();
        if (label != null) {
            print(' ');
            print0(label);
        }

        if (x.getWhen() != null) {
            print0(ucase ? " WHEN " : " when ");
            x.getWhen().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleRaiseStatement x) {
        print0(ucase ? "RAISE" : "raise");
        if (x.getException() != null) {
            print(' ');
            x.getException().accept(this);
        }
        print(';');
        return false;
    }

    @Override
    public boolean visit(SQLSavePointStatement x) {
        print0(ucase ? "SAVEPOINT" : "savepoint");
        if (x.getName() != null) {
            print0(ucase ? " TO " : " to ");
            x.getName().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
        boolean create = x.isCreate();
        if (!create) {
            print0(ucase ? "FUNCTION " : "function ");
        } else if (x.isOrReplace()) {
            print0(ucase ? "CREATE OR REPLACE FUNCTION " : "create or replace function ");
        } else {
            print0(ucase ? "CREATE FUNCTION " : "create function ");
        }
        x.getName().accept(this);

        int paramSize = x.getParameters().size();

        if (paramSize > 0) {
            print0(" (");
            this.indentCount++;
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            this.indentCount--;
            println();
            print(')');
        }

        String wrappedSource = x.getWrappedSource();
        if (wrappedSource != null) {
            print0(ucase ? " WRAPPED " : " wrapped ");
            print0(wrappedSource);

            if (x.isAfterSemi()) {
                print(';');
            }
            return false;
        }

        println();
        print(ucase ? "RETURN " : "return ");
        x.getReturnDataType().accept(this);

        if (x.isPipelined()) {
            print(ucase ? "PIPELINED " : "pipelined ");
        }

        if (x.isDeterministic()) {
            print(ucase ? "DETERMINISTIC " : "deterministic ");
        }

        SQLName authid = x.getAuthid();
        if (authid != null) {
            print(ucase ? " AUTHID " : " authid ");
            authid.accept(this);
        }

        SQLStatement block = x.getBlock();

        if (block != null && !create) {
            println();
            println("IS");
        } else {
            println();
            if (block instanceof SQLBlockStatement) {
                SQLBlockStatement blockStatement = (SQLBlockStatement) block;
                if (blockStatement.getParameters().size() > 0 || authid != null) {
                    println(ucase ? "AS" : "as");
                }
            }
        }

        String javaCallSpec = x.getJavaCallSpec();
        if (javaCallSpec != null) {
            print0(ucase ? "LANGUAGE JAVA NAME '" : "language java name '");
            print0(javaCallSpec);
            print('\'');
            return false;
        }

        if (x.isParallelEnable()) {
            print0(ucase ? "PARALLEL_ENABLE" : "parallel_enable");
            println();
        }

        if (x.isAggregate()) {
            print0(ucase ? "AGGREGATE" : "aggregate");
            println();
        }

        SQLName using = x.getUsing();
        if (using != null) {
            print0(ucase ? "USING " : "using ");
            using.accept(this);
        }

        if (block != null) {
            block.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isShared()) {
            print0(ucase ? "SHARE " : "share ");
        }

        if (x.isPublic()) {
            print0(ucase ? "PUBLIC " : "public ");
        }

        print0(ucase ? "DATABASE LINK " : "database link ");

        x.getName().accept(this);

        if (x.getUser() != null) {
            print0(ucase ? " CONNECT TO " : " connect to ");
            x.getUser().accept(this);

            if (x.getPassword() != null) {
                print0(ucase ? " IDENTIFIED BY " : " identified by ");
                print0(x.getPassword());
            }
        }

        if (x.getAuthenticatedUser() != null) {
            print0(ucase ? " AUTHENTICATED BY " : " authenticated by ");
            x.getAuthenticatedUser().accept(this);
            if (x.getAuthenticatedPassword() != null) {
                print0(ucase ? " IDENTIFIED BY " : " identified by ");
                print0(x.getAuthenticatedPassword());
            }
        }

        if (x.getUsing() != null) {
            print0(ucase ? " USING " : " using ");
            x.getUsing().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        print0(ucase ? "DROP " : "drop ");
        if (x.isPublic()) {
            print0(ucase ? "PUBLIC " : "public ");
        }
        print0(ucase ? "DATABASE LINK " : "database link ");
        x.getName().accept(this);

        return false;
    }

    public boolean visit(SQLCharacterDataType x) {
        print0(x.getName());
        final List<SQLExpr> arguments = x.getArguments();
        if (arguments.size() > 0) {
            print('(');
            SQLExpr arg0 = arguments.get(0);
            printExpr(arg0, false);
            if (x.getCharType() != null) {
                print(' ');
                print0(x.getCharType());
            }
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        print0(x.getName());
        if (x.getArguments().size() > 0) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }

        print0(ucase ? " TO MONTH" : " to month");

        return false;
    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        print0(x.getName());
        if (x.getArguments().size() > 0) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }

        print0(ucase ? " TO SECOND" : " to second");

        if (x.getFractionalSeconds().size() > 0) {
            print('(');
            x.getFractionalSeconds().get(0).accept(this);
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        print0(ucase ? "USING INDEX" : "using index");
        final SQLObject index = x.getIndex();
        if (index != null) {
            print(' ');
            if (index instanceof SQLCreateIndexStatement) {
                print('(');
                index.accept(this);
                print(')');
            } else {
                index.accept(this);
            }
        }

        printOracleSegmentAttributes(x);

        if (x.isComputeStatistics()) {
            println();
            print0(ucase ? "COMPUTE STATISTICS" : "compute statistics");
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                println();
                print0(ucase ? "ENABLE" : "enable");
            } else {
                println();
                print0(ucase ? "DISABLE" : "disable");
            }
        }

        if (x.isReverse()) {
            println();
            print0(ucase ? "REVERSE" : "reverse");
        }


        return false;
    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        print0(ucase ? "LOB (" : "lob (");
        printAndAccept(x.getItems(), ",");
        print0(ucase ? ") STORE AS" : ") store as");


        if (x.isSecureFile()) {
            print0(ucase ? " SECUREFILE" : " securefile");
        }

        if (x.isBasicFile()) {
            print0(ucase ? " BASICFILE" : " basicfile");
        }

        SQLName segementName = x.getSegementName();
        if (segementName != null) {
            print(' ');
            segementName.accept(this);
        }
        print0(" (");
        this.indentCount++;
        printOracleSegmentAttributes(x);

        if (x.getEnable() != null) {
            println();
            if (x.getEnable().booleanValue()) {
                print0(ucase ? "ENABLE STORAGE IN ROW" : "enable storage in row");
            } else {
                print0(ucase ? "DISABLE STORAGE IN ROW" : "disable storage in row");
            }
        }

        if (x.getChunk() != null) {
            println();
            print0(ucase ? "CHUNK " : "chunk ");
            x.getChunk().accept(this);
        }

        if (x.getCache() != null) {
            println();
            if (x.getCache().booleanValue()) {
                print0(ucase ? "CACHE" : "cache");
            } else {
                print0(ucase ? "NOCACHE" : "nocache");
            }
        }

        if (x.getKeepDuplicate() != null) {
            println();
            if (x.getKeepDuplicate().booleanValue()) {
                print0(ucase ? "KEEP_DUPLICATES" : "keep_duplicates");
            } else {
                print0(ucase ? "DEDUPLICATE" : "deduplicate");
            }
        }

        if (x.isRetention()) {
            println();
            print0(ucase ? "RETENTION" : "retention");
        }

        this.indentCount--;
        println();
        print(')');
        return false;
    }

    @Override
    public boolean visit(OracleUnique x) {
        visit((SQLUnique) x);

        printConstraintState(x);
        return false;
    }

    @Override
    public boolean visit(OracleForeignKey x) {
        visit((SQLForeignKeyImpl) x);

        printConstraintState(x);
        return false;
    }

    @Override
    public boolean visit(OracleCheck x) {
        visit((SQLCheck) x);

        printConstraintState(x);
        return false;
    }

    @Override
    protected void printCascade() {
        print0(ucase ? " CASCADE CONSTRAINTS" : " cascade constraints");
    }

    public boolean visit(SQLCharExpr x, boolean parameterized) {
        if (x.getText() != null && x.getText().length() == 0) {
            print0(ucase ? "NULL" : "null");
        } else {
            super.visit(x, parameterized);
        }

        return false;
    }

    @Override
    public boolean visit(OracleSupplementalIdKey x) {
        print0(ucase ? "SUPPLEMENTAL LOG DATA (" : "supplemental log data (");

        int count = 0;

        if (x.isAll()) {
            print0(ucase ? "ALL" : "all");
            count++;
        }

        if (x.isPrimaryKey()) {
            if (count != 0) {
                print0(", ");
            }
            print0(ucase ? "PRIMARY KEY" : "primary key");
            count++;
        }

        if (x.isUnique()) {
            if (count != 0) {
                print0(", ");
            }
            print0(ucase ? "UNIQUE" : "unique");
            count++;
        }

        if (x.isUniqueIndex()) {
            if (count != 0) {
                print0(", ");
            }
            print0(ucase ? "UNIQUE INDEX" : "unique index");
            count++;
        }

        if (x.isForeignKey()) {
            if (count != 0) {
                print0(", ");
            }
            print0(ucase ? "FOREIGN KEY" : "foreign key");
            count++;
        }

        print0(ucase ? ") COLUMNS" : ") columns");
        return false;
    }

    @Override
    public boolean visit(OracleSupplementalLogGrp x) {
        print0(ucase ? "SUPPLEMENTAL LOG GROUP " : "supplemental log group ");
        x.getGroup().accept(this);
        print0(" (");
        printAndAccept(x.getColumns(), ", ");
        print(')');
        if (x.isAlways()) {
            print0(ucase ? " ALWAYS" : " always");
        }
        return false;
    }

    public boolean visit(OracleCreateTableStatement.Organization x) {

        String type = x.getType();

        print0(ucase ? "ORGANIZATION " : "organization ");
        print0(ucase ? type : type.toLowerCase());

        printOracleSegmentAttributes(x);

        if (x.getPctthreshold() != null) {
            println();
            print0(ucase ? "PCTTHRESHOLD " : "pctthreshold ");
            print(x.getPctthreshold());
        }

        if ("EXTERNAL".equalsIgnoreCase(type)) {
            print0(" (");

            this.indentCount++;
            if (x.getExternalType() != null) {
                println();
                print0(ucase ? "TYPE " : "type ");
                x.getExternalType().accept(this);
            }

            if (x.getExternalDirectory() != null) {
                println();
                print0(ucase ? "DEFAULT DIRECTORY " : "default directory ");
                x.getExternalDirectory().accept(this);
            }

            if (x.getExternalDirectoryRecordFormat() != null) {
                println();
                this.indentCount++;
                print0(ucase ? "ACCESS PARAMETERS (" : "access parameters (");
                x.getExternalDirectoryRecordFormat().accept(this);
                this.indentCount--;
                println();
                print(')');
            }

            if (x.getExternalDirectoryLocation().size() > 0) {
                println();
                print0(ucase ? "LOCATION (" : " location(");
                printAndAccept(x.getExternalDirectoryLocation(), ", ");
                print(')');
            }

            this.indentCount--;
            println();
            print(')');

            if (x.getExternalRejectLimit() != null) {
                println();
                print0(ucase ? "REJECT LIMIT " : "reject limit ");
                x.getExternalRejectLimit().accept(this);
            }
        }

        return false;
    }

    public boolean visit(OracleCreateTableStatement.OIDIndex x) {
        print0(ucase ? "OIDINDEX" : "oidindex");

        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }
        print(" (");
        this.indentCount++;
        printOracleSegmentAttributes(x);
        this.indentCount--;
        println();
        print(")");
        return false;
    }

    @Override
    public boolean visit(OracleCreatePackageStatement x) {
        if (x.isOrReplace()) {
            print0(ucase ? "CREATE OR REPLACE PACKAGE " : "create or replace procedure ");
        } else {
            print0(ucase ? "CREATE PACKAGE " : "create procedure ");
        }

        if (x.isBody()) {
            print0(ucase ? "BODY " : "body ");
        }

        x.getName().accept(this);

        if (x.isBody()) {
            println();
            print0(ucase ? "BEGIN" : "begin");
        }

        this.indentCount++;

        List<SQLStatement> statements = x.getStatements();
        for (int i = 0, size = statements.size(); i < size; ++i) {
            println();
            SQLStatement stmt = statements.get(i);
            stmt.accept(this);
        }

        this.indentCount--;

        if (x.isBody() || statements.size() > 0) {
            println();
            print0(ucase ? "END " : "end ");
            x.getName().accept(this);
            print(';');
        }

        return false;
    }

    @Override
    public boolean visit(SQLAssignItem x) {
        x.getTarget().accept(this);
        print0(" := ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(OracleExecuteImmediateStatement x) {
        print0(ucase ? "EXECUTE IMMEDIATE " : "execute immediate ");
        x.getDynamicSql().accept(this);

        List<SQLExpr> into = x.getInto();
        if (into.size() > 0) {
            print0(ucase ? " INTO " : " into ");
            printAndAccept(into, ", ");
        }

        List<SQLArgument> using = x.getArguments();
        if (using.size() > 0) {
            print0(ucase ? " USING " : " using ");
            printAndAccept(using, ", ");
        }

        List<SQLExpr> returnInto = x.getReturnInto();
        if (returnInto.size() > 0) {
            print0(ucase ? " RETURNNING INTO " : " returnning into ");
            printAndAccept(returnInto, ", ");
        }
        return false;
    }

    @Override
    public boolean visit(OracleTreatExpr x) {
        print0(ucase ? "TREAT (" : "treat (");
        x.getExpr().accept(this);
        print0(ucase ? " AS " : " as ");
        if (x.isRef()) {
            print0(ucase ? "REF " : "ref ");
        }
        x.getType().accept(this);
        print(')');
        return false;
    }

    @Override
    public boolean visit(OracleCreateSynonymStatement x) {
        if (x.isOrReplace()) {
            print0(ucase ? "CREATE OR REPLACE " : "create or replace ");
        } else {
            print0(ucase ? "CREATE " : "create ");
        }

        if (x.isPublic()) {
            print0(ucase ? "PUBLIC " : "public ");
        }

        print0(ucase ? "SYNONYM " : "synonym ");

        x.getName().accept(this);

        print0(ucase ? " FOR " : " for ");
        x.getObject().accept(this);

        return false;
    }

    @Override
    public boolean visit(OracleCreateTypeStatement x) {
        if (x.isOrReplace()) {
            print0(ucase ? "CREATE OR REPLACE TYPE " : "create or replace type ");
        } else {
            print0(ucase ? "CREATE TYPE " : "create type ");
        }

        if (x.isBody()) {
            print0(ucase ? "BODY " : "body ");
        }

        x.getName().accept(this);

        SQLName under = x.getUnder();
        if (under != null) {
            print0(ucase ? " UNDER " : " under ");
            under.accept(this);
        }

        SQLName authId = x.getAuthId();
        if (authId != null) {
            print0(ucase ? " AUTHID " : " authid ");
            authId.accept(this);
        }

        if (x.isForce()) {
            print0(ucase ? "FORCE " : "force ");
        }

        List<SQLParameter> parameters = x.getParameters();
        SQLDataType tableOf = x.getTableOf();

        if (x.isObject()) {
            print0(" AS OBJECT");
        }

        if (parameters.size() > 0) {
            if (x.isParen()) {
                print(" (");
            } else {
                print0(ucase ? " IS" : " is");
            }
            indentCount++;
            println();

            for (int i = 0; i < parameters.size(); ++i) {
                SQLParameter param = parameters.get(i);
                param.accept(this);

                SQLDataType dataType = param.getDataType();

                if (i < parameters.size() - 1) {
                    if (dataType instanceof OracleFunctionDataType
                            && ((OracleFunctionDataType) dataType).getBlock() != null) {
                        // skip
                        println();
                    } else  if (dataType instanceof OracleProcedureDataType
                            && ((OracleProcedureDataType) dataType).getBlock() != null) {
                        // skip
                        println();
                    } else {
                        println(", ");
                    }
                }
            }

            indentCount--;
            println();

            if (x.isParen()) {
                print0(")");
            } else {
                print0("END");
            }
        } else if (tableOf != null) {
            print0(ucase ? " AS TABLE OF " : " as table of ");
            tableOf.accept(this);
        } else if (x.getVarraySizeLimit() != null) {
            print0(ucase ? " VARRAY (" : " varray (");
            x.getVarraySizeLimit().accept(this);
            print0(ucase ? ") OF " : ") of ");
            x.getVarrayDataType().accept(this);
        }

        Boolean isFinal = x.getFinal();
        if (isFinal != null) {
            if (isFinal.booleanValue()) {
                print0(ucase ? " FINAL" : " final");
            } else {
                print0(ucase ? " NOT FINAL" : " not final");
            }
        }

        Boolean instantiable = x.getInstantiable();
        if (instantiable != null) {
            if (instantiable.booleanValue()) {
                print0(ucase ? " INSTANTIABLE" : " instantiable");
            } else {
                print0(ucase ? " NOT INSTANTIABLE" : " not instantiable");
            }
        }

        String wrappedSource = x.getWrappedSource();
        if (wrappedSource != null) {
            print0(ucase ? " WRAPPED" : " wrapped");
            print0(wrappedSource);
        }

        return false;
    }

    @Override
    public boolean visit(OraclePipeRowStatement x) {
        print0(ucase ? "PIPE ROW(" : "pipe row(");
        printAndAccept(x.getParameters(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(OracleIsOfTypeExpr x) {
        printExpr(x.getExpr());
        print0(ucase ? " IS OF TYPE (" : " is of type (");

        List<SQLExpr> types = x.getTypes();
        for (int i = 0, size = types.size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLExpr type = types.get(i);
            if (Boolean.TRUE == type.getAttribute("ONLY")) {
                print0(ucase ? "ONLY " : "only ");
            }
            type.accept(this);
        }

        print(')');
        return false;
    }

    @Override
    public boolean visit(OracleRunStatement x) {
        print0("@@");
        printExpr(x.getExpr());
        return false;
    }

    @Override
    public boolean visit(OracleXmlColumnProperties x) {
        print0(ucase ? "XMLTYPE " : "xmltype ");
        x.getColumn().accept(this);

        final OracleXmlColumnProperties.OracleXMLTypeStorage storage = x.getStorage();
        if (storage != null) {
            storage.accept(this);
        }

        final Boolean allowNonSchema = x.getAllowNonSchema();
        if (allowNonSchema != null) {
            if (allowNonSchema.booleanValue()) {
                print0(ucase ? " ALLOW NONSCHEMA" : " allow nonschema");
            } else {
                print0(ucase ? " DISALLOW NONSCHEMA" : " disallow nonschema");
            }
        }

        final Boolean allowAnySchema = x.getAllowAnySchema();
        if (allowAnySchema != null) {
            if (allowAnySchema.booleanValue()) {
                print0(ucase ? " ALLOW ANYSCHEMA" : " allow anyschema");
            } else {
                print0(ucase ? " DISALLOW ANYSCHEMA" : " disallow anyschema");
            }
        }

        return false;
    }

    @Override
    public boolean visit(OracleXmlColumnProperties.OracleXMLTypeStorage x) {
        return false;
    }

    public boolean visit(SQLSubPartition x) {
        super.visit(x);
        incrementIndent();
        printOracleSegmentAttributes(x);
        decrementIndent();
        return false;
    }

    public boolean visit(SQLPartitionValue x) {
        super.visit(x);
        incrementIndent();
        printOracleSegmentAttributes(x);
        decrementIndent();
        return false;
    }
}
