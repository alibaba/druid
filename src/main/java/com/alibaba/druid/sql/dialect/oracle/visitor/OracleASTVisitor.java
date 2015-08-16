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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUnique;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface OracleASTVisitor extends SQLASTVisitor {

    void endVisit(OraclePLSQLCommitStatement astNode);

    void endVisit(OracleAnalytic x);

    void endVisit(OracleAnalyticWindowing x);

    void endVisit(OracleDateExpr x);

    void endVisit(OracleDbLinkExpr x);

    void endVisit(OracleDeleteStatement x);

    void endVisit(OracleExtractExpr x);

    void endVisit(OracleIntervalExpr x);

    void endVisit(OracleOrderBy x);

    void endVisit(OracleOuterExpr x);

    void endVisit(OracleSelectForUpdate x);

    void endVisit(OracleSelectHierachicalQueryClause x);

    void endVisit(OracleSelectJoin x);

    void endVisit(OracleOrderByItem x);

    void endVisit(OracleSelectPivot x);

    void endVisit(OracleSelectPivot.Item x);

    void endVisit(OracleSelectRestriction.CheckOption x);

    void endVisit(OracleSelectRestriction.ReadOnly x);

    void endVisit(OracleSelectSubqueryTableSource x);

    void endVisit(OracleSelectUnPivot x);

    void endVisit(OracleUpdateStatement x);

    boolean visit(OraclePLSQLCommitStatement astNode);

    boolean visit(OracleAnalytic x);

    boolean visit(OracleAnalyticWindowing x);

    boolean visit(OracleDateExpr x);

    boolean visit(OracleDbLinkExpr x);

    boolean visit(OracleDeleteStatement x);

    boolean visit(OracleExtractExpr x);

    boolean visit(OracleIntervalExpr x);

    boolean visit(OracleOrderBy x);

    boolean visit(OracleOuterExpr x);

    boolean visit(OracleSelectForUpdate x);

    boolean visit(OracleSelectHierachicalQueryClause x);

    boolean visit(OracleSelectJoin x);

    boolean visit(OracleOrderByItem x);

    boolean visit(OracleSelectPivot x);

    boolean visit(OracleSelectPivot.Item x);

    boolean visit(OracleSelectRestriction.CheckOption x);

    boolean visit(OracleSelectRestriction.ReadOnly x);

    boolean visit(OracleSelectSubqueryTableSource x);

    boolean visit(OracleSelectUnPivot x);

    boolean visit(OracleUpdateStatement x);

    boolean visit(SampleClause x);

    void endVisit(SampleClause x);

    boolean visit(OracleSelectTableReference x);

    void endVisit(OracleSelectTableReference x);

    boolean visit(PartitionExtensionClause x);

    void endVisit(PartitionExtensionClause x);

    boolean visit(VersionsFlashbackQueryClause x);

    void endVisit(VersionsFlashbackQueryClause x);

    boolean visit(AsOfFlashbackQueryClause x);

    void endVisit(AsOfFlashbackQueryClause x);

    boolean visit(GroupingSetExpr x);

    void endVisit(GroupingSetExpr x);

    boolean visit(OracleWithSubqueryEntry x);

    void endVisit(OracleWithSubqueryEntry x);

    boolean visit(SearchClause x);

    void endVisit(SearchClause x);

    boolean visit(CycleClause x);

    void endVisit(CycleClause x);

    boolean visit(OracleBinaryFloatExpr x);

    void endVisit(OracleBinaryFloatExpr x);

    boolean visit(OracleBinaryDoubleExpr x);

    void endVisit(OracleBinaryDoubleExpr x);

    boolean visit(OracleSelect x);

    void endVisit(OracleSelect x);

    boolean visit(OracleCursorExpr x);

    void endVisit(OracleCursorExpr x);

    boolean visit(OracleIsSetExpr x);

    void endVisit(OracleIsSetExpr x);

    boolean visit(ModelClause.ReturnRowsClause x);

    void endVisit(ModelClause.ReturnRowsClause x);

    boolean visit(ModelClause.MainModelClause x);

    void endVisit(ModelClause.MainModelClause x);

    boolean visit(ModelClause.ModelColumnClause x);

    void endVisit(ModelClause.ModelColumnClause x);

    boolean visit(ModelClause.QueryPartitionClause x);

    void endVisit(ModelClause.QueryPartitionClause x);

    boolean visit(ModelClause.ModelColumn x);

    void endVisit(ModelClause.ModelColumn x);

    boolean visit(ModelClause.ModelRulesClause x);

    void endVisit(ModelClause.ModelRulesClause x);

    boolean visit(ModelClause.CellAssignmentItem x);

    void endVisit(ModelClause.CellAssignmentItem x);

    boolean visit(ModelClause.CellAssignment x);

    void endVisit(ModelClause.CellAssignment x);

    boolean visit(ModelClause x);

    void endVisit(ModelClause x);

    boolean visit(OracleMergeStatement x);

    void endVisit(OracleMergeStatement x);

    boolean visit(OracleMergeStatement.MergeUpdateClause x);

    void endVisit(OracleMergeStatement.MergeUpdateClause x);

    boolean visit(OracleMergeStatement.MergeInsertClause x);

    void endVisit(OracleMergeStatement.MergeInsertClause x);

    boolean visit(OracleErrorLoggingClause x);

    void endVisit(OracleErrorLoggingClause x);

    boolean visit(OracleReturningClause x);

    void endVisit(OracleReturningClause x);

    boolean visit(OracleInsertStatement x);

    void endVisit(OracleInsertStatement x);

    boolean visit(InsertIntoClause x);

    void endVisit(InsertIntoClause x);

    boolean visit(OracleMultiInsertStatement x);

    void endVisit(OracleMultiInsertStatement x);

    boolean visit(ConditionalInsertClause x);

    void endVisit(ConditionalInsertClause x);

    boolean visit(ConditionalInsertClauseItem x);

    void endVisit(ConditionalInsertClauseItem x);

    boolean visit(OracleSelectQueryBlock x);

    void endVisit(OracleSelectQueryBlock x);

    boolean visit(OracleBlockStatement x);

    void endVisit(OracleBlockStatement x);

    boolean visit(OracleLockTableStatement x);

    void endVisit(OracleLockTableStatement x);

    boolean visit(OracleAlterSessionStatement x);

    void endVisit(OracleAlterSessionStatement x);

    boolean visit(OracleExprStatement x);

    void endVisit(OracleExprStatement x);

    boolean visit(OracleDatetimeExpr x);

    void endVisit(OracleDatetimeExpr x);

    boolean visit(OracleSysdateExpr x);

    void endVisit(OracleSysdateExpr x);

    boolean visit(OracleExceptionStatement x);

    void endVisit(OracleExceptionStatement x);

    boolean visit(OracleExceptionStatement.Item x);

    void endVisit(OracleExceptionStatement.Item x);

    boolean visit(OracleArgumentExpr x);

    void endVisit(OracleArgumentExpr x);

    boolean visit(OracleSetTransactionStatement x);

    void endVisit(OracleSetTransactionStatement x);

    boolean visit(OracleExplainStatement x);

    void endVisit(OracleExplainStatement x);

    boolean visit(OracleAlterProcedureStatement x);

    void endVisit(OracleAlterProcedureStatement x);

    boolean visit(OracleAlterTableDropPartition x);

    void endVisit(OracleAlterTableDropPartition x);

    boolean visit(OracleAlterTableTruncatePartition x);

    void endVisit(OracleAlterTableTruncatePartition x);

    boolean visit(OracleAlterTableStatement x);

    void endVisit(OracleAlterTableStatement x);

    boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x);

    void endVisit(OracleAlterTableSplitPartition.TableSpaceItem x);

    boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x);

    void endVisit(OracleAlterTableSplitPartition.UpdateIndexesClause x);

    boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x);

    void endVisit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x);

    boolean visit(OracleAlterTableSplitPartition x);

    void endVisit(OracleAlterTableSplitPartition x);

    boolean visit(OracleAlterTableModify x);

    void endVisit(OracleAlterTableModify x);

    boolean visit(OracleCreateIndexStatement x);

    void endVisit(OracleCreateIndexStatement x);

    boolean visit(OracleForStatement x);

    void endVisit(OracleForStatement x);

    boolean visit(Else x);

    void endVisit(Else x);

    boolean visit(ElseIf x);

    void endVisit(ElseIf x);

    boolean visit(OracleIfStatement x);

    void endVisit(OracleIfStatement x);

    boolean visit(OracleRangeExpr x);

    void endVisit(OracleRangeExpr x);

    boolean visit(OracleAlterIndexStatement x);

    void endVisit(OracleAlterIndexStatement x);

    boolean visit(OracleAlterTableAddConstaint x);

    void endVisit(OracleAlterTableAddConstaint x);

    boolean visit(OraclePrimaryKey x);

    void endVisit(OraclePrimaryKey x);

    boolean visit(OracleCreateTableStatement x);

    void endVisit(OracleCreateTableStatement x);

    boolean visit(OracleAlterIndexStatement.Rebuild x);

    void endVisit(OracleAlterIndexStatement.Rebuild x);

    boolean visit(OracleStorageClause x);

    void endVisit(OracleStorageClause x);

    boolean visit(OracleGotoStatement x);

    void endVisit(OracleGotoStatement x);

    boolean visit(OracleLabelStatement x);

    void endVisit(OracleLabelStatement x);

    boolean visit(OracleParameter x);

    void endVisit(OracleParameter x);

    boolean visit(OracleCommitStatement x);

    void endVisit(OracleCommitStatement x);

    boolean visit(OracleAlterTriggerStatement x);

    void endVisit(OracleAlterTriggerStatement x);

    boolean visit(OracleAlterSynonymStatement x);

    void endVisit(OracleAlterSynonymStatement x);

    boolean visit(OracleAlterViewStatement x);

    void endVisit(OracleAlterViewStatement x);

    boolean visit(AsOfSnapshotClause x);

    void endVisit(AsOfSnapshotClause x);

    boolean visit(OracleAlterTableMoveTablespace x);

    void endVisit(OracleAlterTableMoveTablespace x);

    boolean visit(OracleSizeExpr x);

    void endVisit(OracleSizeExpr x);

    boolean visit(OracleFileSpecification x);

    void endVisit(OracleFileSpecification x);

    boolean visit(OracleAlterTablespaceAddDataFile x);

    void endVisit(OracleAlterTablespaceAddDataFile x);

    boolean visit(OracleAlterTablespaceStatement x);

    void endVisit(OracleAlterTablespaceStatement x);

    boolean visit(OracleCreateSequenceStatement x);

    void endVisit(OracleCreateSequenceStatement x);

    boolean visit(OracleRangeValuesClause x);

    void endVisit(OracleRangeValuesClause x);

    boolean visit(OraclePartitionByRangeClause x);

    void endVisit(OraclePartitionByRangeClause x);

    boolean visit(OracleLoopStatement x);

    void endVisit(OracleLoopStatement x);

    boolean visit(OracleExitStatement x);

    void endVisit(OracleExitStatement x);

    boolean visit(OracleFetchStatement x);

    void endVisit(OracleFetchStatement x);

    boolean visit(OracleSavePointStatement x);

    void endVisit(OracleSavePointStatement x);

    boolean visit(OracleCreateProcedureStatement x);

    void endVisit(OracleCreateProcedureStatement x);

    boolean visit(OracleCreateDatabaseDbLinkStatement x);

    void endVisit(OracleCreateDatabaseDbLinkStatement x);

    boolean visit(OracleDropDbLinkStatement x);

    void endVisit(OracleDropDbLinkStatement x);

    boolean visit(OracleDataTypeTimestamp x);

    void endVisit(OracleDataTypeTimestamp x);

    boolean visit(OracleDataTypeIntervalYear x);

    void endVisit(OracleDataTypeIntervalYear x);

    boolean visit(OracleDataTypeIntervalDay x);

    void endVisit(OracleDataTypeIntervalDay x);

    boolean visit(OracleUsingIndexClause x);

    void endVisit(OracleUsingIndexClause x);

    boolean visit(OracleLobStorageClause x);

    void endVisit(OracleLobStorageClause x);

    boolean visit(OracleUnique x);

    void endVisit(OracleUnique x);

    boolean visit(OracleForeignKey x);

    void endVisit(OracleForeignKey x);

    boolean visit(OracleCheck x);

    void endVisit(OracleCheck x);
}
