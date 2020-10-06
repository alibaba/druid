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
package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.expr.SQLSizeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.*;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

/**
 * Created by wenshao on 16/07/2017.
 */
public class OracleToMySqlOutputVisitor extends MySqlOutputVisitor implements OracleASTVisitor {
    public OracleToMySqlOutputVisitor(Appendable appender) {
        super(appender);
    }

    public OracleToMySqlOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(OracleAnalytic x) {
        return false;
    }

    @Override
    public boolean visit(OracleAnalyticWindowing x) {
        return false;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleIntervalExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleOuterExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectPivot x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectPivot.Item x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectRestriction.CheckOption x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectRestriction.ReadOnly x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectSubqueryTableSource x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectUnPivot x) {
        return false;
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {
        return false;
    }

    @Override
    public boolean visit(SampleClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectTableReference x) {
        return false;
    }

    @Override
    public boolean visit(PartitionExtensionClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleWithSubqueryEntry x) {
        return false;
    }

    @Override
    public boolean visit(SearchClause x) {
        return false;
    }

    @Override
    public boolean visit(CycleClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleCursorExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleIsSetExpr x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.ReturnRowsClause x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.MainModelClause x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.ModelColumnClause x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.QueryPartitionClause x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.ModelColumn x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.ModelRulesClause x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.CellAssignmentItem x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause.CellAssignment x) {
        return false;
    }

    @Override
    public boolean visit(ModelClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleReturningClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement.InsertIntoClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement.ConditionalInsertClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement.ConditionalInsertClauseItem x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        return false;
    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleExceptionStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleExceptionStatement.Item x) {
        return false;
    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableModify x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleForStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        return false;
    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleStorageClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        return false;
    }

    @Override
    public boolean visit(SQLSizeExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleExitStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleContinueStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleRaiseStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        return false;
    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        return false;
    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleUnique x) {
        return false;
    }

    @Override
    public boolean visit(OracleForeignKey x) {
        return false;
    }

    @Override
    public boolean visit(OracleCheck x) {
        return false;
    }

    @Override
    public boolean visit(OracleSupplementalIdKey x) {
        return false;
    }

    @Override
    public boolean visit(OracleSupplementalLogGrp x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateTableStatement.Organization x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateTableStatement.OIDIndex x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreatePackageStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleExecuteImmediateStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleTreatExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateSynonymStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateTypeStatement x) {
        return false;
    }

    @Override
    public boolean visit(OraclePipeRowStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleIsOfTypeExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleRunStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleXmlColumnProperties x) {
        return false;
    }

    @Override
    public boolean visit(OracleXmlColumnProperties.OracleXMLTypeStorage x) {
        return false;
    }
}
