/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeTimestamp;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfSnapshotClause;
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
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleParameter;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OraclePartitionByRangeClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleRangeValuesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
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
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleRangeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSizeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterIndexStatement.Rebuild;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableAddConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.NestedTablePartitionSpec;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.TableSpaceItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.UpdateIndexesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleBlockStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateDatabaseDbLinkStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateSequenceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDropDbLinkStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExprStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleFetchStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleFileSpecification;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement.Else;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement.ElseIf;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLabelStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLoopStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeUpdateClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSavePointStatement;
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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUnique;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class OracleOutputVisitor extends SQLASTOutputVisitor implements OracleASTVisitor {

    private final boolean printPostSemi;

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

    public void postVisit(SQLObject x) {
        if (!(x instanceof SQLStatement)) {
            return;
        }

        if ((!isPrintPostSemi()) //
            && (!(x.getParent() instanceof OracleBlockStatement))) {
            return;
        }

        if (x instanceof OraclePLSQLCommitStatement) {
            return;
        }
        if (x.getParent() instanceof OracleCreateProcedureStatement) {
            return;
        }

        if (isPrettyFormat()) {
            if (x.getParent() != null) {
                print(";");
            } else {
                println(";");
            }
        }
    }

    private void printHints(List<SQLHint> hints) {
        if (hints.size() > 0) {
            print("/*+ ");
            printAndAccept(hints, ", ");
            print(" */");
        }
    }

    public boolean visit(SQLAllColumnExpr x) {
        print("*");
        return false;
    }

    public boolean visit(OracleAnalytic x) {
        print("OVER (");
        
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

        print(")");
        
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
        if (x.getTableName() != null) {
            print("DELETE ");
            
            if (x.getHints().size() > 0) {
                printAndAccept(x.getHints(), ", ");
                print(' ');
            }

            print("FROM ");
            if (x.isOnly()) {
                print("ONLY (");
                x.getTableName().accept(this);
                print(")");
            } else {
                x.getTableName().accept(this);
            }

            printAlias(x.getAlias());
        }

        if (x.getWhere() != null) {
            println();
            incrementIndent();
            print("WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
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
        println();
        return false;
    }

    public boolean visit(SQLSelect x) {
        if (x instanceof OracleSelect) {
            return visit((OracleSelect) x);
        }

        return super.visit(x);
    }

    public boolean visit(OracleSelect x) {
        if (x.getWithSubQuery() != null) {
            x.getWithSubQuery().accept(this);
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

        if (x.isNoCycle()) {
            print("NOCYCLE ");
        }

        if (x.isPrior()) {
            print("PRIOR ");
        }

        x.getConnectBy().accept(this);

        return false;
    }

    public boolean visit(OracleSelectJoin x) {
        x.getLeft().accept(this);

        if (x.getJoinType() == JoinType.COMMA) {
            print(", ");
            x.getRight().accept(this);
        } else {
            boolean isRoot = x.getParent() instanceof SQLSelectQueryBlock;
            if (isRoot) {
                incrementIndent();
            }

            println();
            print(JoinType.toString(x.getJoinType()));
            print(" ");

            x.getRight().accept(this);

            if (isRoot) {
                decrementIndent();
            }

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

    public boolean visit(OracleSelectQueryBlock x) {
        print("SELECT ");

        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print("UNIQUE ");
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            print("INTO ");
            x.getInto().accept(this);
        }

        println();
        print("FROM ");
        if (x.getFrom() == null) {
            print("DUAL");
        } else {
            x.getFrom().setParent(x);
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        if (x.getHierachicalQueryClause() != null) {
            println();
            x.getHierachicalQueryClause().accept(this);
        }

        if (x.getGroupBy() != null) {
            println();
            x.getGroupBy().accept(this);
        }

        if (x.getModelClause() != null) {
            println();
            x.getModelClause().accept(this);
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
        println();
        x.getSelect().accept(this);
        decrementIndent();
        println();
        print(")");

        if (x.getPivot() != null) {
            println();
            x.getPivot().accept(this);
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
            print(" ");
            x.getSampleClause().accept(this);
        }

        if (x.getPivot() != null) {
            println();
            x.getPivot().accept(this);
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

    public boolean visit(OracleUpdateStatement x) {
        print("UPDATE ");
        
        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

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
            incrementIndent();
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.getReturning().size() > 0) {
            println();
            print("RETURNING ");
            printAndAccept(x.getReturning(), ", ");
            print(" INTO ");
            printAndAccept(x.getReturningInto(), ", ");
        }

        return false;
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
    public void endVisit(OracleIntervalExpr x) {

    }

    @Override
    public void endVisit(SQLMethodInvokeExpr x) {

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
    public boolean visit(OracleWithSubqueryEntry x) {
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
    public void endVisit(OracleWithSubqueryEntry x) {

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
    public boolean visit(OracleCursorExpr x) {
        print("CURSOR(");
        incrementIndent();
        println();
        x.getQuery().accept(this);
        decrementIndent();
        println();
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

        print(" ON (");
        x.getOn().accept(this);
        print(") ");

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
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.getDeleteWhere() != null) {
            incrementIndent();
            println();
            print("DELETE WHERE ");
            x.getDeleteWhere().setParent(x);
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
            x.getWhere().setParent(x);
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
        //visit((SQLInsertStatement) x);
        
        print("INSERT ");
        
        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

        print("INTO ");
        
        x.getTableSource().accept(this);

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
            print("VALUES");
            println();
            x.getValues().accept(this);
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().setParent(x);
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
    public void endVisit(OracleInsertStatement x) {
        endVisit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(InsertIntoClause x) {
        print("INTO ");

        x.getTableSource().accept(this);

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
        if (x.getParameters().size() != 0) {
            print("DECLARE");
            incrementIndent();
            println();

            for (int i = 0, size = x.getParameters().size(); i < size; ++i) {
                if (i != 0) {
                    println();
                }
                OracleParameter param = x.getParameters().get(i);
                param.accept(this);
                print(";");
            }

            decrementIndent();
            println();
        }
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
        } else if (x.getWait() != null) {
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
    public boolean visit(OracleExprStatement x) {
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleExprStatement x) {

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

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            println();
            SQLStatement stmt = x.getStatements().get(i);
            stmt.setParent(x);
            stmt.accept(this);
        }
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
        if (x.isReadOnly()) {
            print("SET TRANSACTION READ ONLY NAME ");
        } else {
            print("SET TRANSACTION NAME ");
        }
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleSetTransactionStatement x) {

    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        print("EXPLAIN PLAN");
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
        x.getStatement().accept(this);

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
        for (SQLAlterTableItem item : x.getItems()) {
            println();
            item.accept(this);
        }
        if (x.isUpdateGlobalIndexes()) {
            println();
            print("UPDATE GLOABL INDEXES");
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

    @Override
    public boolean visit(OracleAlterTableModify x) {
        print("MODIFY (");
        incrementIndent();
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            println();
            SQLColumnDefinition column = x.getColumns().get(i);
            column.accept(this);
            if (i != size - 1) {
                print(", ");
            }
        }
        decrementIndent();
        println();
        print(")");

        return false;
    }

    @Override
    public void endVisit(OracleAlterTableModify x) {

    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        print("CREATE ");
        if (x.getType() != null) {
            print(x.getType());
            print(" ");
        }

        print("INDEX ");

        x.getName().accept(this);
        print(" ON ");
        x.getTable().accept(this);
        print("(");
        printAndAccept(x.getItems(), ", ");
        print(")");

        if (x.isIndexOnlyTopLevel()) {
            println();
            print("INDEX ONLY TOPLEVEL");
        }

        if (x.getPtcfree() != null) {
            println();
            print("PCTFREE ");
            x.getPtcfree().accept(this);
        }

        if (x.getInitrans() != null) {
            println();
            print("INITRANS ");
            x.getInitrans().accept(this);
        }

        if (x.getMaxtrans() != null) {
            println();
            print("MAXTRANS ");
            x.getMaxtrans().accept(this);
        }

        if (x.isComputeStatistics()) {
            println();
            print("COMPUTE STATISTICS");
        }

        if (x.getTablespace() != null) {
            println();
            print("TABLESPACE ");
            x.getTablespace().accept(this);
        }

        if (x.isOnline()) {
            print(" ONLINE");
        }

        if (x.isNoParallel()) {
            print(" NOPARALLEL");
        } else if (x.getParallel() != null) {
            print(" PARALLEL ");
            x.getParallel().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleCreateIndexStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement x) {
        print("ALTER INDEX ");
        x.getName().accept(this);

        if (x.getRenameTo() != null) {
            print(" RENAME TO ");
            x.getRenameTo().accept(this);
        }

        if (x.getMonitoringUsage() != null) {
            print(" MONITORING USAGE");
        }

        if (x.getRebuild() != null) {
            print(" ");
            x.getRebuild().accept(this);
        }

        if (x.getParallel() != null) {
            print(" PARALLEL");
            x.getParallel().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement x) {

    }

    @Override
    public boolean visit(Rebuild x) {
        print("REBUILD");

        if (x.getOption() != null) {
            print(" ");
            x.getOption().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(Rebuild x) {

    }

    @Override
    public boolean visit(OracleForStatement x) {
        print("FOR ");
        x.getIndex().accept(this);
        print(" IN ");
        x.getRange().accept(this);
        println();
        print("LOOP");
        incrementIndent();
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.setParent(x);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }

        decrementIndent();
        println();
        print("END LOOP");
        return false;
    }

    @Override
    public void endVisit(OracleForStatement x) {

    }

    @Override
    public boolean visit(Else x) {
        print("ELSE");
        incrementIndent();
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement item = x.getStatements().get(i);
            item.setParent(x);
            item.accept(this);
        }
        print(";");

        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(ElseIf x) {
        print("ELSE IF ");
        x.getCondition().accept(this);
        print(" THEN");
        incrementIndent();
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement item = x.getStatements().get(i);
            item.setParent(x);
            item.accept(this);
        }
        print(";");

        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(ElseIf x) {

    }

    @Override
    public void endVisit(Else x) {

    }

    @Override
    public boolean visit(OracleIfStatement x) {
        print("IF ");
        x.getCondition().accept(this);
        print(" THEN");
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.setParent(x);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        print(";");
        decrementIndent();

        for (ElseIf elseIf : x.getElseIfList()) {
            println();
            elseIf.accept(this);
        }

        if (x.getElseItem() != null) {
            println();
            x.getElseItem().accept(this);
        }
        println();
        print("END IF");
        return false;
    }

    @Override
    public void endVisit(OracleIfStatement x) {

    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        x.getLowBound().accept(this);
        print("..");
        x.getUpBound().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleRangeExpr x) {

    }

    protected void visitColumnDefault(SQLColumnDefinition x) {
        if (x.getParent() instanceof OracleBlockStatement) {
            print(" := ");
        } else {
            print(" DEFAULT ");
        }
        x.getDefaultExpr().accept(this);
    }

    @Override
    public boolean visit(OracleAlterTableAddConstaint x) {
        print("ADD ");
        x.getConstraint().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableAddConstaint x) {

    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(" ");
        }
        print("PRIMARY KEY (");
        printAndAccept(x.getColumns(), ", ");
        print(")");

        printConstraintState(x);

        return false;
    }

    protected void printConstraintState(OracleConstraint x) {
        if (x.getUsing() != null) {
            println();
            x.getUsing().accept(this);
        }

        if (x.getExceptionsInto() != null) {
            println();
            print("EXCEPTIONS INTO ");
            x.getExceptionsInto().accept(this);
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print(" ENABLE");
            } else {
                print(" DIABLE");
            }
        }

        if (x.getInitially() != null) {
            print(" INITIALLY ");
            print(x.getInitially().name());
        }

        if (x.getDeferrable() != null) {
            if (x.getDeferrable().booleanValue()) {
                print(" DEFERRABLE");
            } else {
                print(" NOT DEFERRABLE");
            }
        }
    }

    @Override
    public void endVisit(OraclePrimaryKey x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        this.visit((SQLCreateTableStatement) x);

        if (x.isOrganizationIndex()) {
            println();
            print("ORGANIZATION INDEX");
        }

        if (x.getPtcfree() != null) {
            println();
            print("PCTFREE ");
            x.getPtcfree().accept(this);
        }

        if (x.getInitrans() != null) {
            println();
            print("INITRANS ");
            x.getInitrans().accept(this);
        }

        if (x.getMaxtrans() != null) {
            println();
            print("MAXTRANS ");
            x.getMaxtrans().accept(this);
        }

        if (x.isInMemoryMetadata()) {
            println();
            print("IN_MEMORY_METADATA");
        }

        if (x.isCursorSpecificSegment()) {
            println();
            print("CURSOR_SPECIFIC_SEGMENT");
        }

        if (x.getParallel() == Boolean.TRUE) {
            println();
            print("PARALLEL");
        } else if (x.getParallel() == Boolean.FALSE) {
            println();
            print("NOPARALLEL");
        }

        if (x.getCache() == Boolean.TRUE) {
            println();
            print("CACHE");
        } else if (x.getCache() == Boolean.FALSE) {
            println();
            print("NOCACHE");
        }

        if (x.getCompress() == Boolean.TRUE) {
            println();
            print("COMPRESS");
        } else if (x.getCompress() == Boolean.FALSE) {
            println();
            print("NOCOMPRESS");
        }

        if (x.getLogging() == Boolean.TRUE) {
            println();
            print("LOGGING");
        } else if (x.getLogging() == Boolean.FALSE) {
            println();
            print("NOLOGGING");
        }

        if (x.getTablespace() != null) {
            println();
            print("TABLESPACE ");
            x.getTablespace().accept(this);
        }

        if (x.getStorage() != null) {
            println();
            x.getStorage().accept(this);
        }

        if (x.getLobStorage() != null) {
            println();
            x.getLobStorage().accept(this);
        }

        if (x.isOnCommit()) {
            println();
            print("ON COMMIT");
        }

        if (x.isPreserveRows()) {
            println();
            print("PRESERVE ROWS");
        }

        if (x.getPartitioning() != null) {
            println();
            x.getPartitioning().accept(this);
        }

        if (x.getSelect() != null) {
            println();
            print("AS");
            println();
            x.getSelect().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleCreateTableStatement x) {

    }

    @Override
    public boolean visit(OracleStorageClause x) {
        print("STORAGE (");

        boolean first = true;
        if (x.getInitial() != null) {
            if (!first) {
                print(' ');
            }
            print("INITIAL ");
            x.getInitial().accept(this);
            first = false;
        }

        if (x.getMaxSize() != null) {
            if (!first) {
                print(' ');
            }
            print("MAXSIZE ");
            x.getMaxSize().accept(this);
            first = false;
        }

        if (x.getFreeLists() != null) {
            if (!first) {
                print(' ');
            }
            print("FREELISTS ");
            x.getFreeLists().accept(this);
            first = false;
        }

        if (x.getFreeListGroups() != null) {
            if (!first) {
                print(' ');
            }

            print("FREELIST GROUPS ");
            x.getFreeListGroups().accept(this);
            first = false;
        }

        if (x.getBufferPool() != null) {
            if (!first) {
                print(' ');
            }
            print("BUFFER_POOL ");
            x.getBufferPool().accept(this);
            first = false;
        }

        if (x.getObjno() != null) {
            if (!first) {
                print(' ');
            }
            print("OBJNO ");
            x.getObjno().accept(this);
            first = false;
        }

        print(")");
        return false;
    }

    @Override
    public void endVisit(OracleStorageClause x) {

    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        print("GOTO ");
        x.getLabel().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleGotoStatement x) {

    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        print("<<");
        x.getLabel().accept(this);
        print(">>");
        return false;
    }

    @Override
    public void endVisit(OracleLabelStatement x) {

    }

    @Override
    public boolean visit(OracleParameter x) {
        if (x.getDataType().getName().equalsIgnoreCase("CURSOR")) {
            print("CURSOR ");
            x.getName().accept(this);
            print(" IS");
            incrementIndent();
            println();
            SQLSelect select = ((SQLQueryExpr) x.getDefaultValue()).getSubQuery();
            select.accept(this);
            decrementIndent();

        } else {
            x.getName().accept(this);
            print(" ");

            x.getDataType().accept(this);

            if (x.getDefaultValue() != null) {
                print(" := ");
                x.getDefaultValue().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(OracleParameter x) {

    }

    @Override
    public boolean visit(OracleCommitStatement x) {
        print("COMMIT");

        if (x.isWrite()) {
            print(" WRITE");
            if (x.getWait() != null) {
                if (x.getWait().booleanValue()) {
                    print(" WAIT");
                } else {
                    print(" NOWAIT");
                }
            }

            if (x.getImmediate() != null) {
                if (x.getImmediate().booleanValue()) {
                    print(" IMMEDIATE");
                } else {
                    print(" BATCH");
                }
            }
        }

        return false;
    }

    @Override
    public void endVisit(OracleCommitStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        print("ALTER TRIGGER ");
        x.getName().accept(this);

        if (x.isCompile()) {
            print(" COMPILE");
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print("ENABLE");
            } else {
                print("DISABLE");
            }
        }
        return false;
    }

    @Override
    public void endVisit(OracleAlterTriggerStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        print("ALTER SYNONYM ");
        x.getName().accept(this);

        if (x.isCompile()) {
            print(" COMPILE");
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print("ENABLE");
            } else {
                print("DISABLE");
            }
        }
        return false;
    }

    @Override
    public void endVisit(OracleAlterSynonymStatement x) {

    }

    @Override
    public boolean visit(AsOfSnapshotClause x) {
        print("AS OF SNAPSHOT(");
        x.getExpr().accept(this);
        print(")");
        return false;
    }

    @Override
    public void endVisit(AsOfSnapshotClause x) {

    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        print("ALTER VIEW ");
        x.getName().accept(this);

        if (x.isCompile()) {
            print(" COMPILE");
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print("ENABLE");
            } else {
                print("DISABLE");
            }
        }
        return false;
    }

    @Override
    public void endVisit(OracleAlterViewStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        print(" MOVE TABLESPACE ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableMoveTablespace x) {

    }

    @Override
    public boolean visit(OracleSizeExpr x) {
        x.getValue().accept(this);
        print(x.getUnit().name());
        return false;
    }

    @Override
    public void endVisit(OracleSizeExpr x) {

    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        printAndAccept(x.getFileNames(), ", ");

        if (x.getSize() != null) {
            print(" SIZE ");
            x.getSize().accept(this);
        }

        if (x.isAutoExtendOff()) {
            print(" AUTOEXTEND OFF");
        } else if (x.getAutoExtendOn() != null) {
            print(" AUTOEXTEND ON ");
            x.getAutoExtendOn().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleFileSpecification x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        print("ADD DATAFILE");
        incrementIndent();
        for (OracleFileSpecification file : x.getFiles()) {
            println();
            file.accept(this);
        }
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(OracleAlterTablespaceAddDataFile x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        print("ALTER TABLESPACE ");
        x.getName().accept(this);
        println();
        x.getItem().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        print("TRUNCATE TABLE ");
        printAndAccept(x.getTableSources(), ", ");

        if (x.isPurgeSnapshotLog()) {
            print(" PURGE SNAPSHOT LOG");
        }
        return false;
    }

    @Override
    public boolean visit(OracleCreateSequenceStatement x) {
        print("CREATE SEQUENCE ");
        x.getName().accept(this);

        if (x.getStartWith() != null) {
            print(" START WITH ");
            x.getStartWith().accept(this);
        }

        if (x.getIncrementBy() != null) {
            print(" INCREMENT BY ");
            x.getIncrementBy().accept(this);
        }

        if (x.getMaxValue() != null) {
            print(" MAXVALUE ");
            x.getMaxValue().accept(this);
        }

        if (x.isNoMaxValue()) {
            print(" NOMAXVALUE");
        }

        if (x.getMinValue() != null) {
            print(" MINVALUE ");
            x.getMinValue().accept(this);
        }

        if (x.isNoMinValue()) {
            print(" NOMINVALUE");
        }

        if (x.getCycle() != null) {
            if (x.getCycle().booleanValue()) {
                print(" CYCLE");
            } else {
                print(" NOCYCLE");
            }
        }

        if (x.getCache() != null) {
            if (x.getCache().booleanValue()) {
                print(" CACHE");
            } else {
                print(" NOCACHE");
            }
        }

        return false;
    }

    @Override
    public void endVisit(OracleCreateSequenceStatement x) {

    }

    @Override
    public boolean visit(OracleRangeValuesClause x) {
        print("PARTITION ");
        x.getName().accept(this);
        print(" VALUES LESS THAN (");
        printAndAccept(x.getValues(), ", ");
        print(")");
        return false;
    }

    @Override
    public void endVisit(OracleRangeValuesClause x) {

    }

    @Override
    public boolean visit(OraclePartitionByRangeClause x) {
        print("PARTITION BY RANGE (");
        printAndAccept(x.getColumns(), ", ");
        print(")");

        if (x.getInterval() != null) {
            print(" INTERVAL ");
            x.getInterval().accept(this);
        }

        if (x.getStoreIn().size() > 0) {
            print(" STORE IN (");
            printAndAccept(x.getStoreIn(), ", ");
            print(")");
        }

        println();
        print("(");
        incrementIndent();
        for (int i = 0, size = x.getRanges().size(); i < size; ++i) {
            if (i != 0) {
                print(",");
            }
            println();
            x.getRanges().get(i).accept(this);
        }
        decrementIndent();
        println();
        print(")");
        return false;
    }

    @Override
    public void endVisit(OraclePartitionByRangeClause x) {

    }

    @Override
    public boolean visit(OracleLoopStatement x) {
        print("LOOP");
        incrementIndent();
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.setParent(x);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }

        decrementIndent();
        println();
        print("END LOOP");
        return false;
    }

    @Override
    public void endVisit(OracleLoopStatement x) {

    }

    @Override
    public boolean visit(OracleExitStatement x) {
        print("EXIT");
        if (x.getWhen() != null) {
            print(" WHEN ");
            x.getWhen().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleExitStatement x) {

    }

    @Override
    public boolean visit(OracleFetchStatement x) {
        print("FETCH ");
        x.getCursorName().accept(this);
        print(" INTO ");
        printAndAccept(x.getInto(), ", ");
        return false;
    }

    @Override
    public void endVisit(OracleFetchStatement x) {

    }

    @Override
    public void endVisit(SQLRollbackStatement x) {

    }

    @Override
    public boolean visit(OracleSavePointStatement x) {
        print("ROLLBACK");
        if (x.getTo() != null) {
            print(" TO ");
            x.getTo().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleSavePointStatement x) {

    }

    @Override
    public boolean visit(OracleCreateProcedureStatement x) {
        if (x.isOrReplace()) {
            print("CREATE OR REPLACE PROCEDURE ");
        } else {
            print("CREATE PROCEDURE ");
        }
        x.getName().accept(this);

        int paramSize = x.getParameters().size();

        if (paramSize > 0) {
            print(" (");
            incrementIndent();
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print(", ");
                    println();
                }
                OracleParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            decrementIndent();
            println();
            print(")");
        }

        println();
        x.getBlock().setParent(x);
        x.getBlock().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        print("CREATE ");
        if (x.isShared()) {
            print("SHARE ");
        }

        if (x.isPublic()) {
            print("PUBLIC ");
        }

        print("DATABASE LINK ");

        x.getName().accept(this);

        if (x.getUser() != null) {
            print(" CONNECT TO ");
            x.getUser().accept(this);

            if (x.getPassword() != null) {
                print(" IDENTIFIED BY ");
                print(x.getPassword());
            }
        }

        if (x.getAuthenticatedUser() != null) {
            print(" AUTHENTICATED BY ");
            x.getAuthenticatedUser().accept(this);
            if (x.getAuthenticatedPassword() != null) {
                print(" IDENTIFIED BY ");
                print(x.getAuthenticatedPassword());
            }
        }

        if (x.getUsing() != null) {
            print(" USING ");
            x.getUsing().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OracleCreateDatabaseDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        print("DROP ");
        if (x.isPublic()) {
            print("PUBLIC ");
        }
        print("DATABASE LINK ");
        x.getName().accept(this);

        return false;
    }

    @Override
    public void endVisit(OracleDropDbLinkStatement x) {

    }

    public boolean visit(SQLCharacterDataType x) {
        print(x.getName());
        if (x.getArguments().size() > 0) {
            print("(");
            x.getArguments().get(0).accept(this);
            if (x.getCharType() != null) {
                print(' ');
                print(x.getCharType());
            }
            print(")");
        }
        return false;
    }

    @Override
    public boolean visit(OracleDataTypeTimestamp x) {
        print(x.getName());
        if (x.getArguments().size() > 0) {
            print("(");
            x.getArguments().get(0).accept(this);
            print(")");
        }

        if (x.isWithTimeZone()) {
            print(" WITH TIME ZONE");
        } else if (x.isWithLocalTimeZone()) {
            print(" WITH LOCAL TIME ZONE");
        }

        return false;
    }

    @Override
    public void endVisit(OracleDataTypeTimestamp x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        print(x.getName());
        if (x.getArguments().size() > 0) {
            print("(");
            x.getArguments().get(0).accept(this);
            print(")");
        }

        print(" TO MONTH");

        return false;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalYear x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        print(x.getName());
        if (x.getArguments().size() > 0) {
            print("(");
            x.getArguments().get(0).accept(this);
            print(")");
        }

        print(" TO SECOND");

        if (x.getFractionalSeconds().size() > 0) {
            print("(");
            x.getFractionalSeconds().get(0).accept(this);
            print(")");
        }

        return false;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalDay x) {

    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        print("USING INDEX");
        if (x.getIndex() != null) {
            print(' ');
            x.getIndex().accept(this);
        } else {
            if (x.getPtcfree() != null) {
                print(" PCTFREE ");
                x.getPtcfree().accept(this);
            }

            if (x.getInitrans() != null) {
                print(" INITRANS ");
                x.getInitrans().accept(this);
            }

            if (x.getMaxtrans() != null) {
                print(" MAXTRANS ");
                x.getMaxtrans().accept(this);
            }

            if (x.isComputeStatistics()) {
                print(" COMPUTE STATISTICS");
            }

            if (x.getTablespace() != null) {
                print(" TABLESPACE ");
                x.getTablespace().accept(this);
            }

            if (x.getEnable() != null) {
                if (x.getEnable().booleanValue()) {
                    print(" ENABLE");
                } else {
                    print(" DISABLE");
                }
            }

            if (x.getStorage() != null) {
                println();
                x.getStorage().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(OracleUsingIndexClause x) {

    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        print("LOB (");
        printAndAccept(x.getItems(), ",");
        print(") STORE AS ");

        if (x.isSecureFile()) {
            print("SECUREFILE ");
        }

        if (x.isBasicFile()) {
            print("BASICFILE ");
        }

        boolean first = true;
        print('(');
        if (x.getTableSpace() != null) {
            if (!first) {
                print(' ');
            }
            print("TABLESPACE ");
            x.getTableSpace().accept(this);
            first = false;
        }

        if (x.getEnable() != null) {
            if (!first) {
                print(' ');
            }
            if (x.getEnable().booleanValue()) {
                print("ENABLE STORAGE IN ROW");
            } else {
                print("DISABLE STORAGE IN ROW");
            }
        }

        if (x.getChunk() != null) {
            if (!first) {
                print(' ');
            }
            print("CHUNK ");
            x.getChunk().accept(this);
        }

        if (x.getCache() != null) {
            if (!first) {
                print(' ');
            }
            if (x.getCache().booleanValue()) {
                print("CACHE");
            } else {
                print("NOCACHE");
            }

            if (x.getLogging() != null) {
                if (x.getLogging().booleanValue()) {
                    print(" LOGGING");
                } else {
                    print(" NOLOGGING");
                }
            }
        }

        if (x.getCompress() != null) {
            if (!first) {
                print(' ');
            }
            if (x.getCompress().booleanValue()) {
                print("COMPRESS");
            } else {
                print("NOCOMPRESS");
            }
        }

        if (x.getKeepDuplicate() != null) {
            if (!first) {
                print(' ');
            }
            if (x.getKeepDuplicate().booleanValue()) {
                print("KEEP_DUPLICATES");
            } else {
                print("DEDUPLICATE");
            }
        }

        print(')');
        return false;
    }

    @Override
    public void endVisit(OracleLobStorageClause x) {

    }

    @Override
    public boolean visit(OracleUnique x) {
        visit((SQLUnique) x);

        printConstraintState(x);
        return false;
    }

    @Override
    public void endVisit(OracleUnique x) {

    }

    @Override
    public boolean visit(OracleForeignKey x) {
        visit((SQLForeignKeyImpl) x);

        printConstraintState(x);
        return false;
    }

    @Override
    public void endVisit(OracleForeignKey x) {

    }

    @Override
    public boolean visit(OracleCheck x) {
        visit((SQLCheck) x);

        printConstraintState(x);
        return false;
    }

    @Override
    public void endVisit(OracleCheck x) {

    }

    @Override
    protected void printCascade() {
        print(" CASCADE CONSTRAINTS");
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        if ("trim".equalsIgnoreCase(x.getMethodName())) {
            SQLExpr trim_character = (SQLExpr) x.getAttribute("trim_character");
            if (trim_character != null) {
                print(x.getMethodName());
                print("(");
                String trim_option = (String) x.getAttribute("trim_option");
                if (trim_option != null && trim_option.length() != 0) {
                    print(trim_option);
                    print(' ');
                }
                trim_character.accept(this);
                if (x.getParameters().size() > 0) {
                    print(" FROM ");
                    x.getParameters().get(0).accept(this);
                }
                print(")");
                return false;
            }
        }

        return super.visit(x);
    }
    
    public boolean visit(SQLCharExpr x) {
        if (x.getText() != null && x.getText().length() == 0) {
            print("NULL");
        } else {
            super.visit(x);
        }

        return false;
    }
}
