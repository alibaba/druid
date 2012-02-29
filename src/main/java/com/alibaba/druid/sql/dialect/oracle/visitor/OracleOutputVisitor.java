/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLObjectCreateExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.VersionsFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.GroupingSetExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignment;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignmentItem;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellReferenceOption;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.MainModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumnClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelRuleOption;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelRulesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.QueryPartitionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ReferenceModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ReturnRowsClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SubqueryFactoringClause;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExtractExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.NestedTablePartitionSpec;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.TableSpaceItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.UpdateIndexesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleBlockStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintState;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleGrantStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeUpdateClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMethodInvokeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectForUpdate;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectHierachicalQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListMultiColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListSingleColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetValueClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class OracleOutputVisitor extends SQLASTOutputVisitor implements OracleASTVisitor {

    public OracleOutputVisitor(Appendable appender){
        super(appender);
    }

    public void postVisit(SQLObject x) {
        if (x instanceof SQLStatement) {
            if (x instanceof OraclePLSQLCommitStatement) {
                return;
            }

            if (x.getParent() instanceof SQLStatement) {
                print(";");
            } else {
                println(";");
            }
        }
    }

    private void printHints(List<OracleHint> hints) {
        if (hints.size() > 0) {
            print("/*+ ");
            printAndAccept(hints, ", ");
            print(" */");
        }
    }

    public boolean visit(OracleAggregateExpr expr) {
        expr.getMethodName().accept(this);
        print("(");
        if (expr.isUnique()) {
            print("UNIQUE ");
        }
        printAndAccept(expr.getArguments(), ", ");
        print(")");

        if (expr.getOver() != null) {
            print(" OVER (");
            expr.getOver().accept(this);
            print(")");
        }
        return false;
    }

    public boolean visit(SQLAllColumnExpr x) {
        print("*");
        return false;
    }

    public boolean visit(OracleAnalytic x) {
        boolean space = false;
        if (x.getPartitionBy().size() > 0) {
            print("PARTITION BY ");
            printAndAccept(x.getPartitionBy(), ", ");

            space = true;
        }

        if (x.getOrderBy() != null) {
            if (space) {
                print(" ");
            }
            x.getOrderBy().accept(this);
            space = true;
        }

        if (x.getWindowing() != null) {
            if (space) {
                print(" ");
            }
            x.getWindowing().accept(this);
        }

        return false;
    }

    public boolean visit(OracleAnalyticWindowing x) {
        print(x.getType().name().toUpperCase());
        print(" ");
        x.getExpr().accept(this);
        return false;
    }

    public boolean visit(OracleDateExpr x) {
        print("DATE '");
        print(x.getLiteral());
        print('\'');
        return false;
    }

    public boolean visit(OracleDbLinkExpr x) {
        x.getExpr().accept(this);
        print("@");
        print(x.getDbLink());
        return false;
    }

    public boolean visit(OracleDeleteStatement x) {
        print("DELETE ");
        printHints(x.getHints());

        print("FROM ");
        if (x.isOnly()) {
            print("ONLY (");
            x.getTableName().accept(this);
            print(")");
        } else {
            x.getTableName().accept(this);
        }

        printAlias(x.getAlias());

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().accept(this);
        }
        
        if (x.getReturning() != null) {
            println();
            x.getReturning().accept(this);
        }

        return false;
    }

    public boolean visit(OracleExtractExpr x) {
        print("EXTRACT(");
        print(x.getUnit().name());
        print(" FROM ");
        x.getFrom().accept(this);
        print(")");
        return false;
    }

    public boolean visit(OracleHint x) {
        print(x.getName());
        return false;
    }

    public boolean visit(OracleIntervalExpr x) {
        if (x.getValue() instanceof SQLLiteralExpr) {
            print("INTERVAL ");
            x.getValue().accept(this);
            print(" ");
        } else {
            print('(');
            x.getValue().accept(this);
            print(") ");
        }

        print(x.getType().name());

        if (x.getPrecision() != null) {
            print("(");
            print(x.getPrecision().intValue());
            if (x.getFactionalSecondsPrecision() != null) {
                print(", ");
                print(x.getFactionalSecondsPrecision().intValue());
            }
            print(")");
        }

        if (x.getToType() != null) {
            print(" TO ");
            print(x.getToType().name());
            if (x.getToFactionalSecondsPrecision() != null) {
                print("(");
                print(x.getToFactionalSecondsPrecision().intValue());
                print(")");
            }
        }

        return false;
    }

    public boolean visit(OracleOrderBy x) {
        if (x.getItems().size() > 0) {
            print("ORDER ");
            if (x.isSibings()) {
                print("SIBLINGS ");
            }
            print("BY ");

            printAndAccept(x.getItems(), ", ");
        }
        return false;
    }

    public boolean visit(OracleOuterExpr x) {
        x.getExpr().accept(this);
        print("(+)");
        return false;
    }

    public boolean visit(OraclePLSQLCommitStatement astNode) {
        print("/");
        return false;
    }

    public boolean visit(SQLSelect x) {
        if (x instanceof OracleSelect) {
            return visit((OracleSelect) x);
        }

        return super.visit(x);
    }

    public boolean visit(OracleSelect x) {
        if (x.getFactoring() != null) {
            x.getFactoring().accept(this);
            println();
        }

        x.getQuery().accept(this);

        if (x.getRestriction() != null) {
            print(" ");
            x.getRestriction().accept(this);
        }

        if (x.getForUpdate() != null) {
            println();
            x.getForUpdate().accept(this);
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        return false;
    }

    public boolean visit(OracleSelectForUpdate x) {
        print("FOR UPDATE");
        if (x.getOf().size() > 0) {
            print("(");
            printAndAccept(x.getOf(), ", ");
            print(")");
        }
        
        if (x.isNotWait()) {
            print(" NOWAIT");
        } else if (x.isSkipLocked()) {
            print(" SKIP LOCKED");
        } else if (x.getWait() != null) {
            print(" WAIT ");
            x.getWait().accept(this);
        }
        return false;
    }

    public boolean visit(OracleSelectHierachicalQueryClause x) {
        if (x.getStartWith() != null) {
            print("START WITH ");
            x.getStartWith().accept(this);
            println();
        }

        print("CONNECT BY ");

        if (x.isPrior()) {
            print("PRIOR ");
        }

        if (x.isNoCycle()) {
            print("NOCYCLE ");
        }

        x.getConnectBy().accept(this);

        return false;
    }

    public boolean visit(OracleSelectJoin x) {
        x.getLeft().accept(this);

        if (x.getJoinType() == JoinType.COMMA) {
            print(",");
        } else {
            print(" ");
            print(JoinType.toString(x.getJoinType()));
        }

        print(" ");
        x.getRight().accept(this);

        if (x.getCondition() != null) {
            print(" ON ");
            x.getCondition().accept(this);
            print(" ");
        }

        if (x.getUsing().size() > 0) {
            print(" USING (");
            printAndAccept(x.getUsing(), ", ");
            print(")");
        }
        
        if (x.getFlashback() != null) {
            println();
            x.getFlashback().accept(this);
        }
        
        return false;
    }

    public boolean visit(OracleOrderByItem x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(" ");
            print(x.getType().name().toUpperCase());
        }

        if (x.getNullsOrderType() != null) {
            print(" ");
            print(x.getNullsOrderType().toFormalString());
        }

        return false;
    }

    public boolean visit(OracleSelectPivot x) {
        print("PIVOT");
        if (x.isXml()) {
            print(" XML");
        }
        print(" (");
        printAndAccept(x.getItems(), ", ");

        if (x.getPivotFor().size() > 0) {
            print(" FOR ");
            if (x.getPivotFor().size() == 1) {
                ((SQLExpr) x.getPivotFor().get(0)).accept(this);
            } else {
                print("(");
                printAndAccept(x.getPivotFor(), ", ");
                print(")");
            }
        }

        if (x.getPivotIn().size() > 0) {
            print(" IN (");
            printAndAccept(x.getPivotIn(), ", ");
            print(")");
        }

        print(")");

        return false;
    }

    public boolean visit(OracleSelectPivot.Item x) {
        x.getExpr().accept(this);
        if ((x.getAlias() != null) && (x.getAlias().length() > 0)) {
            print(" AS ");
            print(x.getAlias());
        }
        return false;
    }

    public boolean visit(SQLSelectQueryBlock select) {
        if (select instanceof OracleSelectQueryBlock) {
            return visit((OracleSelectQueryBlock) select);
        }

        return super.visit(select);
    }

    public boolean visit(OracleSelectQueryBlock select) {
        print("SELECT ");

        if (SQLSetQuantifier.ALL == select.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == select.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.UNIQUE == select.getDistionOption()) {
            print("UNIQUE ");
        }

        if (select.getHints().size() > 0) {
            print("/*+");
            printAndAccept(select.getHints(), ", ");
            print("*/ ");
        }

        printSelectList(select.getSelectList());
        
        if (select.getInto() != null) {
            println();
            print("INTO ");
            select.getInto().accept(this);
        }

        println();
        print("FROM ");
        if (select.getFrom() == null) {
            print("DUAL");
        } else {
            select.getFrom().accept(this);
        }

        if (select.getWhere() != null) {
            println();
            print("WHERE ");
            select.getWhere().accept(this);
        }

        if (select.getHierachicalQueryClause() != null) {
            println();
            select.getHierachicalQueryClause().accept(this);
        }

        if (select.getGroupBy() != null) {
            println();
            select.getGroupBy().accept(this);
        }

        if (select.getModelClause() != null) {
            println();
            select.getModelClause().accept(this);
        }

        return false;
    }

    public boolean visit(OracleSelectRestriction.CheckOption x) {
        print("CHECK OPTION");
        if (x.getConstraint() != null) {
            print(" ");
            x.getConstraint().accept(this);
        }
        return false;
    }

    public boolean visit(OracleSelectRestriction.ReadOnly x) {
        print("READ ONLY");
        return false;
    }

    public boolean visit(OracleSelectSubqueryTableSource x) {
        print("(");
        incrementIndent();
        x.getSelect().accept(this);
        println();
        decrementIndent();
        print(")");
        
        if (x.getPivot() != null) {
            incrementIndent();

            println();
            x.getPivot().accept(this);

            decrementIndent();
        }
        
        if (x.getFlashback() != null) {
            println();
            x.getFlashback().accept(this);
        }

        if ((x.getAlias() != null) && (x.getAlias().length() != 0)) {
            print(" ");
            print(x.getAlias());
        }

        return false;
    }

    public boolean visit(OracleSelectTableReference x) {
        if (x.isOnly()) {
            print("ONLY (");
            x.getExpr().accept(this);

            if (x.getPartition() != null) {
                print(" ");
                x.getPartition().accept(this);
            }

            print(")");
        } else {
            x.getExpr().accept(this);

            if (x.getPartition() != null) {
                print(" ");
                x.getPartition().accept(this);
            }
        }
        
        if (x.getHints().size() > 0) {
            this.printHints(x.getHints());
        }

        if (x.getSampleClause() != null) {
            println();
            x.getSampleClause().accept(this);
        }

        if (x.getPivot() != null) {
            incrementIndent();

            println();
            x.getPivot().accept(this);

            decrementIndent();
        }

        if (x.getFlashback() != null) {
            println();
            x.getFlashback().accept(this);
        }

        printAlias(x.getAlias());

        return false;
    }

    public boolean visit(OracleSelectUnPivot x) {
        print("UNPIVOT");
        if (x.getNullsIncludeType() != null) {
            print(" ");
            print(OracleSelectUnPivot.NullsIncludeType.toString(x.getNullsIncludeType()));
        }

        print(" (");
        if (x.getItems().size() == 1) {
            ((SQLExpr) x.getItems().get(0)).accept(this);
        } else {
            print(" (");
            printAndAccept(x.getItems(), ", ");
            print(")");
        }

        if (x.getPivotFor().size() > 0) {
            print(" FOR ");
            if (x.getPivotFor().size() == 1) {
                ((SQLExpr) x.getPivotFor().get(0)).accept(this);
            } else {
                print("(");
                printAndAccept(x.getPivotFor(), ", ");
                print(")");
            }
        }

        if (x.getPivotIn().size() > 0) {
            print(" IN (");
            printAndAccept(x.getPivotIn(), ", ");
            print(")");
        }

        print(")");
        return false;
    }

    public boolean visit(OracleTableExpr x) {
        x.getTable().accept(this);

        if (x.getPartition() != null) {
            print(" PARTITION (");
            x.getPartition().accept(this);
            print(")");
        } else {
            if (x.getPartitionFor().size() > 0) {
                print(" PARTITION FOR (");
                for (int i = 0, size = x.getPartitionFor().size(); i < size; ++i) {
                    ((SQLName) x.getPartitionFor().get(i)).accept(this);
                }
                print(")");
            } else if (x.getSubPartition() != null) {
                print(" SUBPARTITION (");
                x.getSubPartition().accept(this);
                print(")");
            } else if (x.getSubPartitionFor().size() > 0) {
                print(" SUBPARTITION FOR (");
                for (int i = 0, size = x.getSubPartitionFor().size(); i < size; ++i) {
                    ((SQLName) x.getSubPartitionFor().get(i)).accept(this);
                }
                print(")");
            }
        }
        return false;
    }

    public boolean visit(OracleTimestampExpr x) {
        print("TIMESTAMP '");

        print(x.getLiteral());
        print('\'');

        if (x.getTimeZone() != null) {
            print(" AT TIME ZONE '");
            print(x.getTimeZone());
            print('\'');
        }

        return false;
    }

    public boolean visit(OracleUpdateSetListClause x) {
        print("SET ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    public boolean visit(OracleUpdateSetListMultiColumnItem x) {
        print("(");
        printAndAccept(x.getColumns(), ", ");
        print(") = (");
        x.getSubQuery().accept(this);
        print(")");
        return false;
    }

    public boolean visit(OracleUpdateSetListSingleColumnItem x) {
        x.getColumn().accept(this);
        print(" = ");
        x.getValue().accept(this);
        return false;
    }

    public boolean visit(OracleUpdateSetValueClause x) {
        throw new UnsupportedOperationException();
    }

    public boolean visit(OracleUpdateStatement x) {
        print("UPDATE ");
        printHints(x.getHints());

        if (x.isOnly()) {
            print("ONLY (");
            x.getTableSource().accept(this);
            print(")");
        } else {
            x.getTableSource().accept(this);
        }

        printAlias(x.getAlias());

        println();

        print("SET ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getItems().get(i).accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    // ///////////////////

    @Override
    public void endVisit(OracleAggregateExpr astNode) {

    }

    @Override
    public void endVisit(OracleConstraintState astNode) {

    }

    @Override
    public void endVisit(OraclePLSQLCommitStatement astNode) {

    }

    @Override
    public void endVisit(OracleAnalytic x) {

    }

    @Override
    public void endVisit(OracleAnalyticWindowing x) {

    }

    @Override
    public void endVisit(SQLDataType x) {

    }

    @Override
    public void endVisit(OracleDateExpr x) {

    }

    @Override
    public void endVisit(OracleDbLinkExpr x) {

    }

    @Override
    public void endVisit(OracleDeleteStatement x) {

    }

    @Override
    public void endVisit(OracleExtractExpr x) {

    }

    @Override
    public void endVisit(OracleHint x) {

    }

    @Override
    public void endVisit(OracleIntervalExpr x) {

    }

    @Override
    public void endVisit(SQLMethodInvokeExpr x) {

    }

    @Override
    public void endVisit(SQLObjectCreateExpr x) {

    }

    @Override
    public void endVisit(OracleOrderBy x) {

    }

    @Override
    public void endVisit(OracleOuterExpr x) {

    }

    @Override
    public void endVisit(OracleSelectForUpdate x) {

    }

    @Override
    public void endVisit(OracleSelectHierachicalQueryClause x) {

    }

    @Override
    public void endVisit(OracleSelectJoin x) {

    }

    @Override
    public void endVisit(OracleOrderByItem x) {

    }

    @Override
    public void endVisit(OracleSelectPivot x) {

    }

    @Override
    public void endVisit(Item x) {

    }

    @Override
    public void endVisit(CheckOption x) {

    }

    @Override
    public void endVisit(ReadOnly x) {

    }

    @Override
    public void endVisit(OracleSelectSubqueryTableSource x) {

    }

    @Override
    public void endVisit(OracleSelectUnPivot x) {

    }

    @Override
    public void endVisit(OracleTableExpr x) {

    }

    @Override
    public void endVisit(OracleTimestampExpr x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListClause x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListMultiColumnItem x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListSingleColumnItem x) {

    }

    @Override
    public void endVisit(OracleUpdateSetValueClause x) {

    }

    @Override
    public void endVisit(OracleUpdateStatement x) {

    }

    @Override
    public boolean visit(SampleClause x) {
        print("SAMPLE ");

        if (x.isBlock()) {
            print("BLOCK ");
        }

        print("(");
        printAndAccept(x.getPercent(), ", ");
        print(")");

        if (x.getSeedValue() != null) {
            print(" SEED (");
            x.getSeedValue().accept(this);
            print(")");
        }

        return false;
    }

    @Override
    public void endVisit(SampleClause x) {

    }

    @Override
    public void endVisit(OracleSelectTableReference x) {

    }

    @Override
    public boolean visit(PartitionExtensionClause x) {
        if (x.isSubPartition()) {
            print("SUBPARTITION ");
        } else {
            print("PARTITION ");
        }

        if (x.getPartition() != null) {
            print("(");
            x.getPartition().accept(this);
            print(")");
        } else {
            print("FOR (");
            printAndAccept(x.getFor(), ",");
            print(")");
        }
        return false;
    }

    @Override
    public void endVisit(PartitionExtensionClause x) {

    }

    @Override
    public boolean visit(VersionsFlashbackQueryClause x) {
        print("VERSIONS BETWEEN ");
        print(x.getType().name());
        print(" ");
        x.getBegin().accept(this);
        print(" AND ");
        x.getEnd().accept(this);
        return false;
    }

    @Override
    public void endVisit(VersionsFlashbackQueryClause x) {

    }

    @Override
    public boolean visit(AsOfFlashbackQueryClause x) {
        print("AS OF ");
        print(x.getType().name());
        print(" (");
        x.getExpr().accept(this);
        print(")");
        return false;
    }

    @Override
    public void endVisit(AsOfFlashbackQueryClause x) {

    }

    @Override
    public boolean visit(GroupingSetExpr x) {
        print("GROUPING SETS");
        print(" (");
        printAndAccept(x.getParameters(), ", ");
        print(")");
        return false;
    }

    @Override
    public void endVisit(GroupingSetExpr x) {

    }

    @Override
    public boolean visit(SubqueryFactoringClause.Entry x) {
        x.getName().accept(this);

        if (x.getColumns().size() > 0) {
            print(" (");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }
        println();
        print("AS");
        println();
        print("(");
        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(")");

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
    public void endVisit(SubqueryFactoringClause.Entry x) {

    }

    @Override
    public boolean visit(SubqueryFactoringClause x) {
        print("WITH");
        incrementIndent();
        println();
        printlnAndAccept(x.getEntries(), ", ");
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(SubqueryFactoringClause x) {

    }

    @Override
    public boolean visit(SearchClause x) {
        print("SEARCH ");
        print(x.getType().name());
        print(" FIRST BY ");
        printAndAccept(x.getItems(), ", ");
        print(" SET ");
        x.getOrderingColumn().accept(this);

        return false;
    }

    @Override
    public void endVisit(SearchClause x) {

    }

    @Override
    public boolean visit(CycleClause x) {
        print("CYCLE ");
        printAndAccept(x.getAliases(), ", ");
        print(" SET ");
        x.getMark().accept(this);
        print(" TO ");
        x.getValue().accept(this);
        print(" DEFAULT ");
        x.getDefaultValue().accept(this);

        return false;
    }

    @Override
    public void endVisit(CycleClause x) {

    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {
        print(x.getValue().toString());
        print('F');
        return false;
    }

    @Override
    public void endVisit(OracleBinaryFloatExpr x) {

    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {
        print(x.getValue().toString());
        print('D');
        return false;
    }

    @Override
    public void endVisit(OracleBinaryDoubleExpr x) {

    }

    @Override
    public void endVisit(OracleSelect x) {

    }

    @Override
    public boolean visit(OracleConstraintState x) {
        printlnAndAccept(x.getStates(), " ");
        return false;
    }

    @Override
    public boolean visit(OracleCursorExpr x) {
        print("CURSOR(");
        x.getQuery().accept(this);
        print(")");
        return false;
    }

    @Override
    public void endVisit(OracleCursorExpr x) {

    }

    @Override
    public boolean visit(OracleIsSetExpr x) {
        x.getNestedTable().accept(this);
        print(" IS A SET");
        return false;
    }

    @Override
    public void endVisit(OracleIsSetExpr x) {

    }

    @Override
    public boolean visit(ReturnRowsClause x) {
        if (x.isAll()) {
            print("RETURN ALL ROWS");
        } else {
            print("RETURN UPDATED ROWS");
        }
        return false;
    }

    @Override
    public void endVisit(ReturnRowsClause x) {

    }

    @Override
    public boolean visit(ModelClause x) {
        print("MODEL");

        incrementIndent();
        for (CellReferenceOption opt : x.getCellReferenceOptions()) {
            print(' ');
            print(opt.name);
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
        decrementIndent();

        return false;
    }

    @Override
    public void endVisit(ModelClause x) {

    }

    @Override
    public boolean visit(MainModelClause x) {
        if (x.getMainModelName() != null) {
            print(" MAIN ");
            x.getMainModelName().accept(this);
        }

        println();
        x.getModelColumnClause().accept(this);

        for (CellReferenceOption opt : x.getCellReferenceOptions()) {
            println();
            print(opt.name);
        }

        println();
        x.getModelRulesClause().accept(this);

        return false;
    }

    @Override
    public void endVisit(MainModelClause x) {

    }

    @Override
    public boolean visit(ModelColumnClause x) {
        if (x.getQueryPartitionClause() != null) {
            x.getQueryPartitionClause().accept(this);
            println();
        }

        print("DIMENSION BY (");
        printAndAccept(x.getDimensionByColumns(), ", ");
        print(")");

        println();
        print("MEASURES (");
        printAndAccept(x.getMeasuresColumns(), ", ");
        print(")");
        return false;
    }

    @Override
    public void endVisit(ModelColumnClause x) {

    }

    @Override
    public boolean visit(QueryPartitionClause x) {
        print("PARTITION BY (");
        printAndAccept(x.getExprList(), ", ");
        print(")");
        return false;
    }

    @Override
    public void endVisit(QueryPartitionClause x) {

    }

    @Override
    public boolean visit(ModelColumn x) {
        x.getExpr().accept(this);
        if (x.getAlias() != null) {
            print(" ");
            print(x.getAlias());
        }
        return false;
    }

    @Override
    public void endVisit(ModelColumn x) {

    }

    @Override
    public boolean visit(ModelRulesClause x) {
        if (x.getOptions().size() > 0) {
            print("RULES");
            for (ModelRuleOption opt : x.getOptions()) {
                print(" ");
                print(opt.name);
            }
        }

        if (x.getIterate() != null) {
            print(" ITERATE (");
            x.getIterate().accept(this);
            print(")");

            if (x.getUntil() != null) {
                print(" UNTIL (");
                x.getUntil().accept(this);
                print(")");
            }
        }

        print(" (");
        printAndAccept(x.getCellAssignmentItems(), ", ");
        print(")");
        return false;

    }

    @Override
    public void endVisit(ModelRulesClause x) {

    }

    @Override
    public boolean visit(CellAssignmentItem x) {
        if (x.getOption() != null) {
            print(x.getOption().name);
            print(" ");
        }

        x.getCellAssignment().accept(this);

        if (x.getOrderBy() != null) {
            print(" ");
            x.getOrderBy().accept(this);
        }

        print(" = ");
        x.getExpr().accept(this);

        return false;
    }

    @Override
    public void endVisit(CellAssignmentItem x) {

    }

    @Override
    public boolean visit(CellAssignment x) {
        x.getMeasureColumn().accept(this);
        print("[");
        printAndAccept(x.getConditions(), ", ");
        print("]");
        return false;
    }

    @Override
    public void endVisit(CellAssignment x) {

    }

    @Override
    public boolean visit(OracleMergeStatement x) {
        print("MERGE ");
        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(" ");
        }

        print("INTO ");
        x.getInto().accept(this);

        if (x.getAlias() != null) {
            print(" ");
            print(x.getAlias());
        }

        println();
        print("USING ");
        x.getUsing().accept(this);

        print(" ON ");
        x.getOn().accept(this);

        if (x.getUpdateClause() != null) {
            println();
            x.getUpdateClause().accept(this);
        }

        if (x.getInsertClause() != null) {
            println();
            x.getInsertClause().accept(this);
        }

        if (x.getErrorLoggingClause() != null) {
            println();
            x.getErrorLoggingClause().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OracleMergeStatement x) {

    }

    @Override
    public boolean visit(MergeUpdateClause x) {
        print("WHEN MATCHED THEN UPDATE SET ");
        printAndAccept(x.getItems(), ", ");
        if (x.getWhere() != null) {
            incrementIndent();
            println();
            print("WHERE ");
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.getDeleteWhere() != null) {
            incrementIndent();
            println();
            print("DELETE WHERE ");
            x.getDeleteWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    @Override
    public void endVisit(MergeUpdateClause x) {

    }

    @Override
    public boolean visit(MergeInsertClause x) {
        print("WHEN NOT MATCHED THEN INSERT");
        if (x.getColumns().size() > 0) {
            print(" ");
            printAndAccept(x.getColumns(), ", ");
        }
        print(" VALUES (");
        printAndAccept(x.getValues(), ", ");
        print(")");
        if (x.getWhere() != null) {
            incrementIndent();
            println();
            print("WHERE ");
            x.getWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    @Override
    public void endVisit(MergeInsertClause x) {

    }

    @Override
    public boolean visit(OracleErrorLoggingClause x) {
        print("LOG ERRORS ");
        if (x.getInto() != null) {
            print("INTO ");
            x.getInto().accept(this);
            print(" ");
        }

        if (x.getSimpleExpression() != null) {
            print("(");
            x.getSimpleExpression().accept(this);
            print(")");
        }

        if (x.getLimit() != null) {
            print(" REJECT LIMIT ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OracleErrorLoggingClause x) {

    }

    @Override
    public boolean visit(OracleReturningClause x) {
        print("RETURNING ");
        printAndAccept(x.getItems(), ", ");
        print(" INTO ");
        printAndAccept(x.getValues(), ", ");

        return false;
    }

    @Override
    public void endVisit(OracleReturningClause x) {

    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        visit((SQLInsertStatement) x);

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
    public void endVisit(OracleInsertStatement x) {
        endVisit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(InsertIntoClause x) {
        print("INTO ");

        x.getTableName().accept(this);

        if (x.getColumns().size() > 0) {
            incrementIndent();
            println();
            print("(");
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print(", ");
                }
                x.getColumns().get(i).accept(this);
            }
            print(")");
            decrementIndent();
        }

        if (x.getValues() != null) {
            println();
            print("VALUES ");
            x.getValues().accept(this);
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().setParent(x);
                x.getQuery().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(InsertIntoClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        print("INSERT ");

        if (x.getHints().size() > 0) {
            this.printHints(x.getHints());
        }

        if (x.getOption() != null) {
            print(x.getOption().name());
            print(" ");
        }

        for (int i = 0, size = x.getEntries().size(); i < size; ++i) {
            incrementIndent();
            println();
            x.getEntries().get(i).accept(this);
            decrementIndent();
        }

        println();
        x.getSubQuery().accept(this);

        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {

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
            print("ELSE");
            incrementIndent();
            println();
            x.getElseItem().accept(this);
            decrementIndent();
        }

        return false;
    }

    @Override
    public void endVisit(ConditionalInsertClause x) {

    }

    @Override
    public boolean visit(ConditionalInsertClauseItem x) {
        print("WHEN ");
        x.getWhen().accept(this);
        print(" THEN");
        incrementIndent();
        println();
        x.getThen().accept(this);
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(ConditionalInsertClauseItem x) {
        
    }

    @Override
    public void endVisit(OracleSelectQueryBlock x) {
        
    }

    @Override
    public boolean visit(OracleBlockStatement x) {
        print("BEGIN");
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatementList().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement stmt = x.getStatementList().get(i);
            stmt.setParent(x);
            stmt.accept(this);
        }
        decrementIndent();
        println();
        print("END");
        return false;
    }

    @Override
    public void endVisit(OracleBlockStatement x) {
        
    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        print("LOCK TABLE ");
        x.getTable().accept(this);
        print(" IN ");
        print(x.getLockMode().name());
        print(" MODE ");
        if (x.isNoWait()) {
            print("NOWAIT");
        } else if (x.getWait() != null){
            print("WAIT ");
            x.getWait().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleLockTableStatement x) {
        
    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        print("ALTER SESSION SET ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public void endVisit(OracleAlterSessionStatement x) {
        
    }

    @Override
    public boolean visit(OracleMethodInvokeStatement x) {
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleMethodInvokeStatement x) {
        
    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        x.getExpr().accept(this);
        SQLExpr timeZone = x.getTimeZone();
        
        if (timeZone instanceof SQLIdentifierExpr) {
            if (((SQLIdentifierExpr) timeZone).getName().equalsIgnoreCase("LOCAL")) {
                print(" AT LOCAL");
                return false;
            }
        }
        
        print(" AT TIME ZONE ");
        timeZone.accept(this);
        
        return false;
    }

    @Override
    public void endVisit(OracleDatetimeExpr x) {
        
    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        print("SYSDATE");
        if (x.getOption() != null) {
            print("@");
            print(x.getOption());
        }
        return false;
    }

    @Override
    public void endVisit(OracleSysdateExpr x) {
        
    }

    @Override
    public void endVisit(com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement.Item x) {
        
    }

    @Override
    public boolean visit(com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement.Item x) {
        print("WHEN ");
        x.getWhen().accept(this);
        incrementIndent();
        println();
        x.getStatement().accept(this);
        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(OracleExceptionStatement x) {
        print("EXCEPTION");
        incrementIndent();
        for (OracleExceptionStatement.Item item : x.getItems()) {
            println();
            item.accept(this);
        }
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(OracleExceptionStatement x) {
        
    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        print(x.getArgumentName());
        print(" => ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleArgumentExpr x) {
        
    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        print("SET TRANSACTION NAME ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleSetTransactionStatement x) {
        
    }

    @Override
    public boolean visit(OracleGrantStatement x) {
        print("GRANT ");
        for (int i = 0, size = x.getPrivileges().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            print(x.getPrivileges().get(i));
        }
        
        if (x.getOn() != null) {
            print(" ON ");
            x.getOn().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleGrantStatement x) {
        
    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        print("XPLAIN PLAN");
        incrementIndent();
        println();
        if (x.getStatementId() != null) {
            print("SET STATEMENT_ID = ");
            x.getStatementId().accept(this);
            println();
        }
        
        if (x.getInto() != null) {
            print("INTO ");
            x.getInto().accept(this);
            println();
        }
        
        print("FRO");
        println();
        x.getForStatement().accept(this);
        
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(OracleExplainStatement x) {
        
    }

    @Override
    public boolean visit(OracleAlterProcedureStatement x) {
        print("ALTER PROCEDURE ");
        x.getName().accept(this);
        if (x.isCompile()) {
            print(" COMPILE");
        }
        if (x.isReuseSettings()) {
            print(" REUSE SETTINGS");
        }
        return false;
    }

    @Override
    public void endVisit(OracleAlterProcedureStatement x) {
        
    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        print("DROP PARTITION ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableDropPartition x) {
        
    }

    @Override
    public boolean visit(OracleAlterTableStatement x) {
        print("ALTER TABLE ");
        x.getName().accept(this);
        incrementIndent();
        for (OracleAlterTableItem item : x.getItems()) {
            println();
            item.accept(this);
        }
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableStatement x) {
        
    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        print("TRUNCATE PARTITION ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableTruncatePartition x) {
        
    }

    @Override
    public boolean visit(TableSpaceItem x) {
        print("TABLESPACE ");
        x.getTablespace().accept(this);
        return false;
    }

    @Override
    public void endVisit(TableSpaceItem x) {
        
    }

    @Override
    public boolean visit(UpdateIndexesClause x) {
        print("UPDATE INDEXES");
        if (x.getItems().size() > 0) {
            print("(");
            printAndAccept(x.getItems(), ", ");
            print(")");
        }
        return false;
    }

    @Override
    public void endVisit(UpdateIndexesClause x) {
        
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        print("SPLIT PARTITION ");
        x.getName().accept(this);
        
        if (x.getAt().size() > 0) {
            incrementIndent();
            println();
            print("AT (");
            printAndAccept(x.getAt(), ", ");
            print(")");
            decrementIndent();
        }
        
        if (x.getInto().size() > 0) {
            println();
            incrementIndent();
            print("INTO (");
            printAndAccept(x.getInto(), ", ");
            print(")");
            decrementIndent();
        }
        
        if (x.getUpdateIndexes() != null) {
            println();
            incrementIndent();
            x.getUpdateIndexes().accept(this);
            decrementIndent();
        }
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition x) {
        
    }

    @Override
    public boolean visit(NestedTablePartitionSpec x) {
        print("PARTITION ");
        x.getPartition().accept(this);
        for (SQLObject item : x.getSegmentAttributeItems()) {
            print(" ");
            item.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(NestedTablePartitionSpec x) {
        
    }
}
