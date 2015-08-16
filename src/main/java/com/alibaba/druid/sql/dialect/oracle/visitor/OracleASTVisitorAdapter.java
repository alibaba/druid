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
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.MainModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumnClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelRulesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.QueryPartitionClause;
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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableAddConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleBlockStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCommitStatement;
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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUnique;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class OracleASTVisitorAdapter extends SQLASTVisitorAdapter implements OracleASTVisitor {

    public boolean visit(OracleSelect x) {
        return true;
    }

    public void endVisit(OracleSelect x) {
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
    public boolean visit(OraclePLSQLCommitStatement astNode) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalytic x) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalyticWindowing x) {

        return true;
    }

    @Override
    public boolean visit(OracleDateExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleDbLinkExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {

        return true;
    }

    @Override
    public boolean visit(OracleExtractExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleIntervalExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleOrderBy x) {

        return true;
    }

    @Override
    public boolean visit(OracleOuterExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectForUpdate x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectHierachicalQueryClause x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {

        return true;
    }

    @Override
    public boolean visit(OracleOrderByItem x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectPivot x) {

        return true;
    }

    @Override
    public boolean visit(Item x) {

        return true;
    }

    @Override
    public boolean visit(CheckOption x) {

        return true;
    }

    @Override
    public boolean visit(ReadOnly x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectSubqueryTableSource x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectUnPivot x) {

        return true;
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {

        return true;
    }

    @Override
    public boolean visit(SampleClause x) {

        return true;
    }

    @Override
    public void endVisit(SampleClause x) {

    }

    @Override
    public boolean visit(OracleSelectTableReference x) {

        return true;
    }

    @Override
    public void endVisit(OracleSelectTableReference x) {

    }

    @Override
    public boolean visit(PartitionExtensionClause x) {

        return true;
    }

    @Override
    public void endVisit(PartitionExtensionClause x) {

    }

    @Override
    public boolean visit(VersionsFlashbackQueryClause x) {

        return true;
    }

    @Override
    public void endVisit(VersionsFlashbackQueryClause x) {

    }

    @Override
    public boolean visit(AsOfFlashbackQueryClause x) {

        return true;
    }

    @Override
    public void endVisit(AsOfFlashbackQueryClause x) {

    }

    @Override
    public boolean visit(GroupingSetExpr x) {

        return true;
    }

    @Override
    public void endVisit(GroupingSetExpr x) {

    }

    @Override
    public boolean visit(OracleWithSubqueryEntry x) {

        return true;
    }

    @Override
    public void endVisit(OracleWithSubqueryEntry x) {

    }

    @Override
    public boolean visit(SearchClause x) {

        return true;
    }

    @Override
    public void endVisit(SearchClause x) {

    }

    @Override
    public boolean visit(CycleClause x) {

        return true;
    }

    @Override
    public void endVisit(CycleClause x) {

    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryFloatExpr x) {

    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryDoubleExpr x) {

    }

    @Override
    public boolean visit(OracleCursorExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleCursorExpr x) {

    }

    @Override
    public boolean visit(OracleIsSetExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleIsSetExpr x) {

    }

    @Override
    public boolean visit(ReturnRowsClause x) {
        return true;
    }

    @Override
    public void endVisit(ReturnRowsClause x) {

    }

    @Override
    public boolean visit(ModelClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause x) {

    }

    @Override
    public boolean visit(MainModelClause x) {
        return true;
    }

    @Override
    public void endVisit(MainModelClause x) {

    }

    @Override
    public boolean visit(ModelColumnClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelColumnClause x) {

    }

    @Override
    public boolean visit(QueryPartitionClause x) {
        return true;
    }

    @Override
    public void endVisit(QueryPartitionClause x) {

    }

    @Override
    public boolean visit(ModelColumn x) {
        return true;
    }

    @Override
    public void endVisit(ModelColumn x) {

    }

    @Override
    public boolean visit(ModelRulesClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelRulesClause x) {

    }

    @Override
    public boolean visit(CellAssignmentItem x) {
        return true;
    }

    @Override
    public void endVisit(CellAssignmentItem x) {

    }

    @Override
    public boolean visit(CellAssignment x) {
        return true;
    }

    @Override
    public void endVisit(CellAssignment x) {

    }

    @Override
    public boolean visit(OracleMergeStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleMergeStatement x) {
    }

    @Override
    public boolean visit(MergeUpdateClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeUpdateClause x) {

    }

    @Override
    public boolean visit(MergeInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeInsertClause x) {

    }

    @Override
    public boolean visit(OracleErrorLoggingClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleErrorLoggingClause x) {

    }

    @Override
    public boolean visit(OracleReturningClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleReturningClause x) {

    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleInsertStatement x) {

    }

    @Override
    public boolean visit(InsertIntoClause x) {
        return true;
    }

    @Override
    public void endVisit(InsertIntoClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {

    }

    @Override
    public boolean visit(ConditionalInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(ConditionalInsertClause x) {

    }

    @Override
    public boolean visit(ConditionalInsertClauseItem x) {
        return true;
    }

    @Override
    public void endVisit(ConditionalInsertClauseItem x) {

    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(OracleSelectQueryBlock x) {

    }

    @Override
    public boolean visit(OracleBlockStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleBlockStatement x) {

    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleLockTableStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterSessionStatement x) {

    }

    @Override
    public boolean visit(OracleExprStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExprStatement x) {

    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleDatetimeExpr x) {

    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleSysdateExpr x) {

    }

    @Override
    public boolean visit(OracleExceptionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExceptionStatement x) {

    }

    @Override
    public boolean visit(OracleExceptionStatement.Item x) {
        return true;
    }

    @Override
    public void endVisit(OracleExceptionStatement.Item x) {

    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleArgumentExpr x) {

    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleSetTransactionStatement x) {

    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExplainStatement x) {

    }

    @Override
    public boolean visit(OracleAlterProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterProcedureStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableDropPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableTruncatePartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.TableSpaceItem x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableModify x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableModify x) {

    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateIndexStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement x) {

    }

    @Override
    public boolean visit(OracleForStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleForStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement.Rebuild x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement.Rebuild x) {

    }

    @Override
    public boolean visit(Else x) {
        return true;
    }

    @Override
    public void endVisit(Else x) {

    }

    @Override
    public boolean visit(ElseIf x) {
        return true;
    }

    @Override
    public void endVisit(ElseIf x) {

    }

    @Override
    public boolean visit(OracleIfStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleIfStatement x) {

    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleRangeExpr x) {

    }

    @Override
    public boolean visit(OracleAlterTableAddConstaint x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableAddConstaint x) {

    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(OraclePrimaryKey x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateTableStatement x) {

    }

    @Override
    public boolean visit(OracleStorageClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleStorageClause x) {

    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleGotoStatement x) {

    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleLabelStatement x) {

    }

    @Override
    public boolean visit(OracleParameter x) {
        return true;
    }

    @Override
    public void endVisit(OracleParameter x) {

    }

    @Override
    public boolean visit(OracleCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCommitStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTriggerStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterSynonymStatement x) {

    }

    @Override
    public boolean visit(AsOfSnapshotClause x) {
        return true;
    }

    @Override
    public void endVisit(AsOfSnapshotClause x) {

    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterViewStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableMoveTablespace x) {

    }

    @Override
    public boolean visit(OracleSizeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleSizeExpr x) {

    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        return true;
    }

    @Override
    public void endVisit(OracleFileSpecification x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTablespaceAddDataFile x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(OracleCreateSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateSequenceStatement x) {

    }

    @Override
    public boolean visit(OracleRangeValuesClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleRangeValuesClause x) {

    }

    @Override
    public boolean visit(OraclePartitionByRangeClause x) {
        return true;
    }

    @Override
    public void endVisit(OraclePartitionByRangeClause x) {

    }

    @Override
    public boolean visit(OracleLoopStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleLoopStatement x) {

    }

    @Override
    public boolean visit(OracleExitStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExitStatement x) {

    }

    @Override
    public boolean visit(OracleFetchStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleFetchStatement x) {

    }

    @Override
    public boolean visit(OracleSavePointStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleSavePointStatement x) {

    }

    @Override
    public boolean visit(OracleCreateProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateDatabaseDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleDropDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDataTypeTimestamp x) {
        return true;
    }

    @Override
    public void endVisit(OracleDataTypeTimestamp x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        return true;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalYear x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        return true;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalDay x) {

    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleUsingIndexClause x) {

    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleLobStorageClause x) {

    }

    @Override
    public boolean visit(OracleUnique x) {
        return true;
    }

    @Override
    public void endVisit(OracleUnique x) {

    }

    @Override
    public boolean visit(OracleForeignKey x) {
        return true;
    }

    @Override
    public void endVisit(OracleForeignKey x) {

    }

    @Override
    public boolean visit(OracleCheck x) {
        return true;
    }

    @Override
    public void endVisit(OracleCheck x) {

    }

}
