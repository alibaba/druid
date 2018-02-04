/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.*;
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface OracleASTVisitor extends SQLASTVisitor {



    void endVisit(OracleAnalytic x);

    void endVisit(OracleAnalyticWindowing x);

    void endVisit(SQLDateExpr x);

    void endVisit(OracleDbLinkExpr x);

    void endVisit(OracleDeleteStatement x);

    void endVisit(OracleIntervalExpr x);

    void endVisit(OracleOuterExpr x);

    void endVisit(OracleSelectJoin x);

    void endVisit(OracleSelectPivot x);

    void endVisit(OracleSelectPivot.Item x);

    void endVisit(OracleSelectRestriction.CheckOption x);

    void endVisit(OracleSelectRestriction.ReadOnly x);

    void endVisit(OracleSelectSubqueryTableSource x);

    void endVisit(OracleSelectUnPivot x);

    void endVisit(OracleUpdateStatement x);

    boolean visit(OracleAnalytic x);

    boolean visit(OracleAnalyticWindowing x);

    boolean visit(SQLDateExpr x);

    boolean visit(OracleDbLinkExpr x);

    boolean visit(OracleDeleteStatement x);

    boolean visit(OracleIntervalExpr x);

    boolean visit(OracleOuterExpr x);

    boolean visit(OracleSelectJoin x);

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

    boolean visit(OracleLockTableStatement x);

    void endVisit(OracleLockTableStatement x);

    boolean visit(OracleAlterSessionStatement x);

    void endVisit(OracleAlterSessionStatement x);

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

    boolean visit(OracleAlterTableDropPartition x);

    void endVisit(OracleAlterTableDropPartition x);

    boolean visit(OracleAlterTableTruncatePartition x);

    void endVisit(OracleAlterTableTruncatePartition x);

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

    boolean visit(OracleRangeExpr x);

    void endVisit(OracleRangeExpr x);

    boolean visit(OracleAlterIndexStatement x);

    void endVisit(OracleAlterIndexStatement x);

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

    boolean visit(OracleAlterTriggerStatement x);

    void endVisit(OracleAlterTriggerStatement x);

    boolean visit(OracleAlterSynonymStatement x);

    void endVisit(OracleAlterSynonymStatement x);

    boolean visit(OracleAlterViewStatement x);

    void endVisit(OracleAlterViewStatement x);

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

    boolean visit(OracleExitStatement x);

    void endVisit(OracleExitStatement x);

    boolean visit(OracleContinueStatement x);

    void endVisit(OracleContinueStatement x);

    boolean visit(OracleRaiseStatement x);

    void endVisit(OracleRaiseStatement x);

    boolean visit(OracleCreateDatabaseDbLinkStatement x);

    void endVisit(OracleCreateDatabaseDbLinkStatement x);

    boolean visit(OracleDropDbLinkStatement x);

    void endVisit(OracleDropDbLinkStatement x);

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

    boolean visit(OracleSupplementalIdKey x);

    void endVisit(OracleSupplementalIdKey x);

    boolean visit(OracleSupplementalLogGrp x);

    void endVisit(OracleSupplementalLogGrp x);

    boolean visit(OracleCreateTableStatement.Organization x);

    void endVisit(OracleCreateTableStatement.Organization x);

    boolean visit(OracleCreateTableStatement.OIDIndex x);

    void endVisit(OracleCreateTableStatement.OIDIndex x);

    boolean visit(OracleCreatePackageStatement x);

    void endVisit(OracleCreatePackageStatement x);

    boolean visit(OracleExecuteImmediateStatement x);

    void endVisit(OracleExecuteImmediateStatement x);

    boolean visit(OracleTreatExpr x);

    void endVisit(OracleTreatExpr x);

    boolean visit(OracleCreateSynonymStatement x);

    void endVisit(OracleCreateSynonymStatement x);

    boolean visit(OracleCreateTypeStatement x);

    void endVisit(OracleCreateTypeStatement x);

    boolean visit(OraclePipeRowStatement x);

    void endVisit(OraclePipeRowStatement x);

    boolean visit(OracleIsOfTypeExpr x);

    void endVisit(OracleIsOfTypeExpr x);

    boolean visit(OracleRunStatement x);

    void endVisit(OracleRunStatement x);
}
