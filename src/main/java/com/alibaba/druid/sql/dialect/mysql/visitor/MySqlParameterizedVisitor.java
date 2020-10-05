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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.mysql.ast.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import com.alibaba.druid.sql.visitor.SQLASTParameterizedVisitor;

import java.util.List;

public class MySqlParameterizedVisitor extends SQLASTParameterizedVisitor implements MySqlASTVisitor {

    public MySqlParameterizedVisitor() {
        super(DbType.mysql);
    }

    public MySqlParameterizedVisitor(List<Object> outParameters) {
        super(DbType.mysql, outParameters);
    }

    @Override
    public boolean visit(MySqlTableIndex x) {
        return true;
    }

    @Override
    public void endVisit(MySqlTableIndex x) {

    }

    @Override
    public boolean visit(MySqlKey x) {
        return true;
    }

    @Override
    public void endVisit(MySqlKey x) {

    }

    @Override
    public boolean visit(MySqlPrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(MySqlPrimaryKey x) {

    }

    @Override
    public boolean visit(MySqlUnique x) {
        return true;
    }

    @Override
    public void endVisit(MySqlUnique x) {

    }

    @Override
    public boolean visit(MysqlForeignKey x) {
        return true;
    }

    @Override
    public void endVisit(MysqlForeignKey x) {

    }

    @Override
    public void endVisit(MySqlPrepareStatement x) {

    }

    @Override
    public boolean visit(MySqlPrepareStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlExecuteStatement x) {

    }

    @Override
    public boolean visit(MysqlDeallocatePrepareStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlDeallocatePrepareStatement x) {

    }

    @Override
    public boolean visit(MySqlExecuteStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlDeleteStatement x) {

    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlInsertStatement x) {

    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlLoadDataInFileStatement x) {

    }

    @Override
    public boolean visit(MySqlLoadDataInFileStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlLoadXmlStatement x) {

    }

    @Override
    public boolean visit(MySqlLoadXmlStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowWarningsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowWarningsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowAuthorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowAuthorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlShowHtcStatement x) {

    }

    @Override
    public boolean visit(MysqlShowHtcStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlShowStcStatement x) {

    }

    @Override
    public boolean visit(MysqlShowStcStatement x) {
        return true;
    }

    @Override
    public void endVisit(CobarShowStatus x) {

    }

    @Override
    public boolean visit(CobarShowStatus x) {
        return true;
    }

    @Override
    public void endVisit(DrdsShowDDLJobs x) {

    }

    @Override
    public boolean visit(DrdsShowDDLJobs x) {
        return true;
    }

    @Override
    public void endVisit(DrdsCancelDDLJob x) {

    }

    @Override
    public boolean visit(DrdsCancelDDLJob x) {
        return true;
    }

    @Override
    public void endVisit(DrdsRecoverDDLJob x) {

    }

    @Override
    public boolean visit(DrdsRecoverDDLJob x) {
        return true;
    }

    @Override
    public void endVisit(DrdsRollbackDDLJob x) {

    }

    @Override
    public boolean visit(DrdsRollbackDDLJob x) {
        return true;
    }

    @Override
    public void endVisit(DrdsRemoveDDLJob x) {

    }

    @Override
    public boolean visit(DrdsRemoveDDLJob x) {
        return true;
    }

    @Override
    public void endVisit(DrdsInspectDDLJobCache x) {

    }

    @Override
    public boolean visit(DrdsInspectDDLJobCache x) {
        return true;
    }

    @Override
    public void endVisit(DrdsClearDDLJobCache x) {

    }

    @Override
    public boolean visit(DrdsClearDDLJobCache x) {
        return true;
    }

    @Override
    public void endVisit(DrdsChangeDDLJob x) {

    }

    @Override
    public boolean visit(DrdsChangeDDLJob x) {
        return true;
    }

    @Override
    public void endVisit(DrdsBaselineStatement x) {

    }

    @Override
    public boolean visit(DrdsBaselineStatement x) {
        return true;
    }

    @Override
    public void endVisit(DrdsShowGlobalIndex x) {

    }

    @Override
    public boolean visit(DrdsShowGlobalIndex x) {
        return true;
    }

    @Override
    public void endVisit(DrdsShowMetadataLock x) {

    }

    @Override
    public boolean visit(DrdsShowMetadataLock x) {
        return true;
    }

    @Override
    public void endVisit(MySqlBinlogStatement x) {

    }

    @Override
    public boolean visit(MySqlBinlogStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlResetStatement x) {

    }

    @Override
    public boolean visit(MySqlResetStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateUserStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(UserSpecification x) {

    }

    @Override
    public boolean visit(UserSpecification x) {
        return true;
    }

    @Override
    public void endVisit(MySqlPartitionByKey x) {

    }

    @Override
    public boolean visit(MySqlPartitionByKey x) {
        return true;
    }

    @Override
    public void endVisit(MySqlUpdatePlanCacheStatement x) {

    }

    @Override
    public boolean visit(MySqlUpdatePlanCacheStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowPlanCacheStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPlanCacheStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlClearPlanCacheStatement x) {

    }

    @Override
    public boolean visit(MySqlClearPlanCacheStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlDisabledPlanCacheStatement x) {

    }

    @Override
    public boolean visit(MySqlDisabledPlanCacheStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlExplainPlanCacheStatement x) {

    }

    @Override
    public boolean visit(MySqlExplainPlanCacheStatement x) {
        return true;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(MySqlSelectQueryBlock x) {

    }

    @Override
    public boolean visit(MySqlOutFileExpr x) {
        return true;
    }

    @Override
    public void endVisit(MySqlOutFileExpr x) {

    }

    @Override
    public boolean visit(MySqlExplainStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlExplainStatement x) {

    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlUpdateStatement x) {

    }

    @Override
    public boolean visit(MySqlSetTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlSetTransactionStatement x) {

    }

    @Override
    public boolean visit(MySqlShowHMSMetaStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowHMSMetaStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinaryLogsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowBinaryLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowMasterLogsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowMasterLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCharacterSetStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowCharacterSetStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCollationStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowCollationStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinLogEventsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowBinLogEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowContributorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowContributorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowCreateEventStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEngineStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowEngineStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEnginesStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowEnginesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowErrorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowErrorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEventsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionCodeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowFunctionCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowFunctionStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowGrantsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowGrantsStatement x) {

    }

    @Override
    public boolean visit(MySqlUserName x) {
        return true;
    }

    @Override
    public void endVisit(MySqlUserName x) {

    }

    @Override
    public boolean visit(MySqlAlterDatabaseSetOption x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterDatabaseSetOption x) {

    }

    @Override
    public boolean visit(MySqlAlterDatabaseKillJob x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterDatabaseKillJob x) {

    }

    @Override
    public boolean visit(MySqlShowMasterStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowMasterStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowOpenTablesStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowOpenTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPluginsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowPluginsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPartitionsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowPartitionsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPrivilegesStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowPrivilegesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureCodeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowProcedureCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowProcedureStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcessListStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowProcessListStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProfileStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowProfileStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProfilesStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowProfilesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowRelayLogEventsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowRelayLogEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlaveHostsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowSlaveHostsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSequencesStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowSequencesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlaveStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowSlaveStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlowStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowSlowStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTableStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowTableStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTriggersStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowTriggersStatement x) {

    }

    @Override
    public boolean visit(MySqlShowVariantsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowVariantsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTraceStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowTraceStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBroadcastsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowBroadcastsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowRuleStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowRuleStatement x) {

    }

    @Override
    public boolean visit(MySqlShowRuleStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowRuleStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowDsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowDsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowDdlStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowDdlStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTopologyStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowTopologyStatement x) {

    }

    @Override
    public boolean visit(MySqlRenameTableStatement.Item x) {
        return true;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement.Item x) {

    }

    @Override
    public boolean visit(MySqlRenameTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement x) {

    }

    @Override
    public boolean visit(MysqlShowDbLockStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlShowDbLockStatement x) {

    }

    @Override
    public boolean visit(MySqlShowDatabaseStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowDatabaseStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlUseIndexHint x) {
        return true;
    }

    @Override
    public void endVisit(MySqlUseIndexHint x) {

    }

    @Override
    public boolean visit(MySqlIgnoreIndexHint x) {
        return true;
    }

    @Override
    public void endVisit(MySqlIgnoreIndexHint x) {

    }

    @Override
    public boolean visit(MySqlLockTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlLockTableStatement x) {

    }

    @Override
    public boolean visit(MySqlLockTableStatement.Item x) {
        return true;
    }

    @Override
    public void endVisit(MySqlLockTableStatement.Item x) {

    }

    @Override
    public boolean visit(MySqlUnlockTablesStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlUnlockTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlForceIndexHint x) {
        return true;
    }

    @Override
    public void endVisit(MySqlForceIndexHint x) {

    }

    @Override
    public boolean visit(MySqlAlterTableChangeColumn x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableChangeColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableOption x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableOption x) {

    }

    @Override
    public boolean visit(MySqlCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateTableStatement x) {

    }

    @Override
    public boolean visit(MySqlHelpStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlHelpStatement x) {

    }

    @Override
    public boolean visit(MySqlCharExpr x) {
        parameterizeAndExportPara(x);
        return false;
    }

    @Override
    public void endVisit(MySqlCharExpr x) {

    }

    @Override
    public boolean visit(MySqlAlterTableModifyColumn x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableModifyColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableDiscardTablespace x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableDiscardTablespace x) {

    }

    @Override
    public boolean visit(MySqlAlterTableImportTablespace x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableImportTablespace x) {

    }

    @Override
    public boolean visit(TableSpaceOption x) {
        return true;
    }

    @Override
    public void endVisit(TableSpaceOption x) {

    }

    @Override
    public boolean visit(MySqlAnalyzeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAnalyzeStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateExternalCatalogStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateExternalCatalogStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterUserStatement x) {

    }

    @Override
    public boolean visit(MySqlOptimizeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlOptimizeStatement x) {

    }

    @Override
    public boolean visit(MySqlHintStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlHintStatement x) {

    }

    @Override
    public boolean visit(MySqlOrderingExpr x) {
        return true;
    }

    @Override
    public void endVisit(MySqlOrderingExpr x) {

    }

    @Override
    public boolean visit(MySqlCaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCaseStatement x) {

    }

    @Override
    public boolean visit(MySqlDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlDeclareStatement x) {

    }

    @Override
    public boolean visit(MySqlSelectIntoStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlSelectIntoStatement x) {

    }

    @Override
    public boolean visit(MySqlWhenStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlWhenStatement x) {

    }

    @Override
    public boolean visit(MySqlLeaveStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlLeaveStatement x) {

    }

    @Override
    public boolean visit(MySqlIterateStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlIterateStatement x) {

    }

    @Override
    public boolean visit(MySqlRepeatStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlRepeatStatement x) {

    }

    @Override
    public boolean visit(MySqlCursorDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCursorDeclareStatement x) {

    }

    @Override
    public boolean visit(MySqlUpdateTableSource x) {
        return true;
    }

    @Override
    public void endVisit(MySqlUpdateTableSource x) {

    }

    @Override
    public boolean visit(MySqlAlterTableAlterColumn x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableForce x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableForce x) {

    }

    @Override
    public boolean visit(MySqlAlterTableLock x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableLock x) {

    }

    @Override
    public boolean visit(MySqlAlterTableOrderBy x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableOrderBy x) {

    }

    @Override
    public boolean visit(MySqlAlterTableValidation x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableValidation x) {

    }

    @Override
    public boolean visit(MySqlSubPartitionByKey x) {
        return true;
    }

    @Override
    public void endVisit(MySqlSubPartitionByKey x) {

    }

    @Override
    public boolean visit(MySqlSubPartitionByList x) {
        return true;
    }

    @Override
    public void endVisit(MySqlSubPartitionByList x) {

    }

    @Override
    public boolean visit(MySqlDeclareHandlerStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlDeclareHandlerStatement x) {

    }

    @Override
    public boolean visit(MySqlDeclareConditionStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlDeclareConditionStatement x) {

    }

    @Override
    public boolean visit(MySqlFlushStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlFlushStatement x) {

    }

    @Override
    public boolean visit(MySqlEventSchedule x) {
        return true;
    }

    @Override
    public void endVisit(MySqlEventSchedule x) {

    }

    @Override
    public boolean visit(MySqlCreateEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateEventStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateAddLogFileGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateAddLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateServerStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateServerStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateTableSpaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateTableSpaceStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterEventStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterLogFileGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterServerStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterServerStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterTablespaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(MySqlChecksumTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlChecksumTableStatement x) {

    }

    @Override
    public boolean visit(MySqlShowDatasourcesStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowDatasourcesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowNodeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowNodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowHelpStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowHelpStatement x) {

    }

    @Override
    public boolean visit(MySqlFlashbackStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlFlashbackStatement x) {

    }

    @Override
    public boolean visit(MySqlShowConfigStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowConfigStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPlanCacheStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowPlanCacheStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPhysicalProcesslistStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowPhysicalProcesslistStatement x) {

    }

    @Override
    public boolean visit(MySqlRenameSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlRenameSequenceStatement x) {

    }

    @Override
    public boolean visit(MySqlCheckTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCheckTableStatement x) {

    }

    @Override
    public boolean visit(MysqlCreateFullTextCharFilterStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlCreateFullTextCharFilterStatement x) {

    }

    @Override
    public boolean visit(MysqlShowFullTextStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlShowFullTextStatement x) {

    }

    @Override
    public boolean visit(MysqlShowCreateFullTextStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlShowCreateFullTextStatement x) {

    }

    @Override
    public boolean visit(MysqlAlterFullTextStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlAlterFullTextStatement x) {

    }

    @Override
    public boolean visit(MysqlDropFullTextStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlDropFullTextStatement x) {

    }

    @Override
    public boolean visit(MysqlCreateFullTextTokenizerStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlCreateFullTextTokenizerStatement x) {

    }

    @Override
    public boolean visit(MysqlCreateFullTextTokenFilterStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlCreateFullTextTokenFilterStatement x) {

    }

    @Override
    public boolean visit(MysqlCreateFullTextAnalyzerStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlCreateFullTextAnalyzerStatement x) {

    }

    @Override
    public boolean visit(MysqlCreateFullTextDictionaryStatement x) {
        return true;
    }

    @Override
    public void endVisit(MysqlCreateFullTextDictionaryStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterTableAlterFullTextIndex x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterTableAlterFullTextIndex x) {

    }

    @Override
    public boolean visit(MySqlExecuteForAdsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlExecuteForAdsStatement x) {

    }

    @Override
    public boolean visit(MySqlManageInstanceGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlManageInstanceGroupStatement x) {

    }

    @Override
    public boolean visit(MySqlRaftMemberChangeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlRaftMemberChangeStatement x) {

    }

    @Override
    public boolean visit(MySqlRaftLeaderTransferStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlRaftLeaderTransferStatement x) {

    }

    @Override
    public boolean visit(MySqlMigrateStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlMigrateStatement x) {

    }

    @Override
    public boolean visit(MySqlShowClusterNameStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowClusterNameStatement x) {

    }

    @Override
    public boolean visit(MySqlShowJobStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowJobStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowMigrateTaskStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowMigrateTaskStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlSubPartitionByValue x) {
        return true;
    }

    @Override
    public void endVisit(MySqlSubPartitionByValue x) {

    }

    @Override
    public boolean visit(MySqlExtPartition x) {
        return true;
    }

    @Override
    public void endVisit(MySqlExtPartition x) {

    }

    @Override
    public boolean visit(MySqlExtPartition.Item x) {
        return true;
    }

    @Override
    public void endVisit(MySqlExtPartition.Item x) {

    }

}
