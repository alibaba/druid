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

import com.alibaba.druid.sql.ast.statement.SQLShowColumnsStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface MySqlASTVisitor extends SQLASTVisitor {
    boolean visit(MySqlTableIndex x);

    void endVisit(MySqlTableIndex x);

    boolean visit(MySqlKey x);

    void endVisit(MySqlKey x);

    boolean visit(MySqlPrimaryKey x);

    void endVisit(MySqlPrimaryKey x);

    boolean visit(MySqlUnique x);

    void endVisit(MySqlUnique x);

    boolean visit(MysqlForeignKey x);

    void endVisit(MysqlForeignKey x);

    void endVisit(MySqlPrepareStatement x);

    boolean visit(MySqlPrepareStatement x);

    void endVisit(MySqlExecuteStatement x);

    boolean visit(MysqlDeallocatePrepareStatement x);

    void endVisit(MysqlDeallocatePrepareStatement x);

    boolean visit(MySqlExecuteStatement x);

    void endVisit(MySqlDeleteStatement x);

    boolean visit(MySqlDeleteStatement x);

    void endVisit(MySqlInsertStatement x);

    boolean visit(MySqlInsertStatement x);

    void endVisit(MySqlLoadDataInFileStatement x);

    boolean visit(MySqlLoadDataInFileStatement x);

    void endVisit(MySqlLoadXmlStatement x);

    boolean visit(MySqlLoadXmlStatement x);

    void endVisit(SQLShowColumnsStatement x);

    boolean visit(SQLShowColumnsStatement x);

    void endVisit(MySqlShowWarningsStatement x);

    boolean visit(MySqlShowWarningsStatement x);

    void endVisit(MySqlShowStatusStatement x);

    boolean visit(MySqlShowStatusStatement x);

    void endVisit(MySqlShowAuthorsStatement x);

    boolean visit(MySqlShowAuthorsStatement x);

    void endVisit(MysqlShowHtcStatement x);

    boolean visit(MysqlShowHtcStatement x);

    void endVisit(MysqlShowStcStatement x);

    boolean visit(MysqlShowStcStatement x);

    void endVisit(CobarShowStatus x);

    boolean visit(CobarShowStatus x);

    void endVisit(DrdsShowDDLJobs x);

    boolean visit(DrdsShowDDLJobs x);

    void endVisit(DrdsCancelDDLJob x);

    boolean visit(DrdsCancelDDLJob x);

    void endVisit(DrdsRecoverDDLJob x);

    boolean visit(DrdsRecoverDDLJob x);

    void endVisit(DrdsRollbackDDLJob x);

    boolean visit(DrdsRollbackDDLJob x);

    void endVisit(DrdsRemoveDDLJob x);

    boolean visit(DrdsRemoveDDLJob x);

    void endVisit(DrdsInspectDDLJobCache x);

    boolean visit(DrdsInspectDDLJobCache x);

    void endVisit(DrdsClearDDLJobCache x);

    boolean visit(DrdsClearDDLJobCache x);

    void endVisit(DrdsChangeDDLJob x);

    boolean visit(DrdsChangeDDLJob x);

    void endVisit(DrdsBaselineStatement x);

    boolean visit(DrdsBaselineStatement x);

    void endVisit(DrdsShowGlobalIndex x);

    boolean visit(DrdsShowGlobalIndex x);

    void endVisit(DrdsShowMetadataLock x);

    boolean visit(DrdsShowMetadataLock x);

    void endVisit(MySqlBinlogStatement x);

    boolean visit(MySqlBinlogStatement x);

    void endVisit(MySqlResetStatement x);

    boolean visit(MySqlResetStatement x);

    void endVisit(MySqlCreateUserStatement x);

    boolean visit(MySqlCreateUserStatement x);

    void endVisit(MySqlCreateUserStatement.UserSpecification x);

    boolean visit(MySqlCreateUserStatement.UserSpecification x);

    void endVisit(MySqlPartitionByKey x);

    boolean visit(MySqlPartitionByKey x);

    void endVisit(MySqlUpdatePlanCacheStatement x);

    boolean visit(MySqlUpdatePlanCacheStatement x);

    void endVisit(MySqlShowPlanCacheStatusStatement x);

    boolean visit(MySqlShowPlanCacheStatusStatement x);

    void endVisit(MySqlClearPlanCacheStatement x);

    boolean visit(MySqlClearPlanCacheStatement x);

    void endVisit(MySqlDisabledPlanCacheStatement x);

    boolean visit(MySqlDisabledPlanCacheStatement x);

    void endVisit(MySqlExplainPlanCacheStatement x);

    boolean visit(MySqlExplainPlanCacheStatement x);

    boolean visit(MySqlSelectQueryBlock x);

    void endVisit(MySqlSelectQueryBlock x);

    boolean visit(MySqlOutFileExpr x);

    void endVisit(MySqlOutFileExpr x);

    boolean visit(MySqlExplainStatement x);

    void endVisit(MySqlExplainStatement x);

    boolean visit(MySqlUpdateStatement x);

    void endVisit(MySqlUpdateStatement x);

    boolean visit(MySqlSetTransactionStatement x);

    void endVisit(MySqlSetTransactionStatement x);

    boolean visit(MySqlShowHMSMetaStatement x);

    void endVisit(MySqlShowHMSMetaStatement x);

    boolean visit(MySqlShowBinaryLogsStatement x);

    void endVisit(MySqlShowBinaryLogsStatement x);

    boolean visit(MySqlShowMasterLogsStatement x);

    void endVisit(MySqlShowMasterLogsStatement x);

    boolean visit(MySqlShowCharacterSetStatement x);

    void endVisit(MySqlShowCharacterSetStatement x);

    boolean visit(MySqlShowCollationStatement x);

    void endVisit(MySqlShowCollationStatement x);

    boolean visit(MySqlShowBinLogEventsStatement x);

    void endVisit(MySqlShowBinLogEventsStatement x);

    boolean visit(MySqlShowContributorsStatement x);

    void endVisit(MySqlShowContributorsStatement x);

    boolean visit(MySqlShowCreateDatabaseStatement x);

    void endVisit(MySqlShowCreateDatabaseStatement x);

    boolean visit(MySqlShowCreateEventStatement x);

    void endVisit(MySqlShowCreateEventStatement x);

    boolean visit(MySqlShowCreateFunctionStatement x);

    void endVisit(MySqlShowCreateFunctionStatement x);

    boolean visit(MySqlShowCreateProcedureStatement x);

    void endVisit(MySqlShowCreateProcedureStatement x);

    boolean visit(SQLShowCreateTableStatement x);

    void endVisit(SQLShowCreateTableStatement x);

    boolean visit(MySqlShowCreateTriggerStatement x);

    void endVisit(MySqlShowCreateTriggerStatement x);

    boolean visit(MySqlShowEngineStatement x);

    void endVisit(MySqlShowEngineStatement x);

    boolean visit(MySqlShowEnginesStatement x);

    void endVisit(MySqlShowEnginesStatement x);

    boolean visit(MySqlShowErrorsStatement x);

    void endVisit(MySqlShowErrorsStatement x);

    boolean visit(MySqlShowEventsStatement x);

    void endVisit(MySqlShowEventsStatement x);

    boolean visit(MySqlShowFunctionCodeStatement x);

    void endVisit(MySqlShowFunctionCodeStatement x);

    boolean visit(MySqlShowFunctionStatusStatement x);

    void endVisit(MySqlShowFunctionStatusStatement x);

    boolean visit(MySqlShowGrantsStatement x);

    void endVisit(MySqlShowGrantsStatement x);

    boolean visit(MySqlUserName x);

    void endVisit(MySqlUserName x);

    boolean visit(MySqlAlterDatabaseSetOption x);

    void endVisit(MySqlAlterDatabaseSetOption x);

    boolean visit(MySqlAlterDatabaseKillJob x);

    void endVisit(MySqlAlterDatabaseKillJob x);

    boolean visit(MySqlShowMasterStatusStatement x);

    void endVisit(MySqlShowMasterStatusStatement x);

    boolean visit(MySqlShowOpenTablesStatement x);

    void endVisit(MySqlShowOpenTablesStatement x);

    boolean visit(MySqlShowPluginsStatement x);

    void endVisit(MySqlShowPluginsStatement x);

    boolean visit(MySqlShowPartitionsStatement x);

    void endVisit(MySqlShowPartitionsStatement x);

    boolean visit(MySqlShowPrivilegesStatement x);

    void endVisit(MySqlShowPrivilegesStatement x);

    boolean visit(MySqlShowProcedureCodeStatement x);

    void endVisit(MySqlShowProcedureCodeStatement x);

    boolean visit(MySqlShowProcedureStatusStatement x);

    void endVisit(MySqlShowProcedureStatusStatement x);

    boolean visit(MySqlShowProcessListStatement x);

    void endVisit(MySqlShowProcessListStatement x);

    boolean visit(MySqlShowProfileStatement x);

    void endVisit(MySqlShowProfileStatement x);

    boolean visit(MySqlShowProfilesStatement x);

    void endVisit(MySqlShowProfilesStatement x);

    boolean visit(MySqlShowRelayLogEventsStatement x);

    void endVisit(MySqlShowRelayLogEventsStatement x);

    boolean visit(MySqlShowSlaveHostsStatement x);

    void endVisit(MySqlShowSlaveHostsStatement x);

    boolean visit(MySqlShowSequencesStatement x);

    void endVisit(MySqlShowSequencesStatement x);

    boolean visit(MySqlShowSlaveStatusStatement x);

    void endVisit(MySqlShowSlaveStatusStatement x);

    boolean visit(MySqlShowSlowStatement x);

    void endVisit(MySqlShowSlowStatement x);

    boolean visit(MySqlShowTableStatusStatement x);

    void endVisit(MySqlShowTableStatusStatement x);

    boolean visit(MySqlShowTriggersStatement x);

    void endVisit(MySqlShowTriggersStatement x);

    boolean visit(MySqlShowVariantsStatement x);

    void endVisit(MySqlShowVariantsStatement x);

    boolean visit(MySqlShowTraceStatement x);

    void endVisit(MySqlShowTraceStatement x);

    boolean visit(MySqlShowBroadcastsStatement x);

    void endVisit(MySqlShowBroadcastsStatement x);

    boolean visit(MySqlShowRuleStatement x);

    void endVisit(MySqlShowRuleStatement x);

    boolean visit(MySqlShowRuleStatusStatement x);

    void endVisit(MySqlShowRuleStatusStatement x);

    boolean visit(MySqlShowDsStatement x);

    void endVisit(MySqlShowDsStatement x);

    boolean visit(MySqlShowDdlStatusStatement x);

    void endVisit(MySqlShowDdlStatusStatement x);

    boolean visit(MySqlShowTopologyStatement x);

    void endVisit(MySqlShowTopologyStatement x);

    boolean visit(MySqlRenameTableStatement.Item x);

    void endVisit(MySqlRenameTableStatement.Item x);

    boolean visit(MySqlRenameTableStatement x);

    void endVisit(MySqlRenameTableStatement x);

    boolean visit(MysqlShowDbLockStatement x);

    void endVisit(MysqlShowDbLockStatement x);

    boolean visit(MySqlShowDatabaseStatusStatement x);

    void endVisit(MySqlShowDatabaseStatusStatement x);

    boolean visit(MySqlUseIndexHint x);

    void endVisit(MySqlUseIndexHint x);

    boolean visit(MySqlIgnoreIndexHint x);

    void endVisit(MySqlIgnoreIndexHint x);

    boolean visit(MySqlLockTableStatement x);

    void endVisit(MySqlLockTableStatement x);

    boolean visit(MySqlLockTableStatement.Item x);

    void endVisit(MySqlLockTableStatement.Item x);

    boolean visit(MySqlUnlockTablesStatement x);

    void endVisit(MySqlUnlockTablesStatement x);

    boolean visit(MySqlForceIndexHint x);

    void endVisit(MySqlForceIndexHint x);

    boolean visit(MySqlAlterTableChangeColumn x);

    void endVisit(MySqlAlterTableChangeColumn x);

    boolean visit(MySqlAlterTableOption x);

    void endVisit(MySqlAlterTableOption x);

    boolean visit(MySqlCreateTableStatement x);

    void endVisit(MySqlCreateTableStatement x);

    boolean visit(MySqlHelpStatement x);

    void endVisit(MySqlHelpStatement x);

    boolean visit(MySqlCharExpr x);

    void endVisit(MySqlCharExpr x);

    boolean visit(MySqlAlterTableModifyColumn x);

    void endVisit(MySqlAlterTableModifyColumn x);

    boolean visit(MySqlAlterTableDiscardTablespace x);

    void endVisit(MySqlAlterTableDiscardTablespace x);

    boolean visit(MySqlAlterTableImportTablespace x);

    void endVisit(MySqlAlterTableImportTablespace x);

    boolean visit(MySqlCreateTableStatement.TableSpaceOption x);

    void endVisit(MySqlCreateTableStatement.TableSpaceOption x);

    boolean visit(MySqlAnalyzeStatement x);

    void endVisit(MySqlAnalyzeStatement x);

    boolean visit(MySqlCreateExternalCatalogStatement x);

    void endVisit(MySqlCreateExternalCatalogStatement x);

    boolean visit(MySqlAlterUserStatement x);

    void endVisit(MySqlAlterUserStatement x);

    boolean visit(MySqlOptimizeStatement x);

    void endVisit(MySqlOptimizeStatement x);

    boolean visit(MySqlHintStatement x);

    void endVisit(MySqlHintStatement x);

    boolean visit(MySqlOrderingExpr x);

    void endVisit(MySqlOrderingExpr x);

    boolean visit(MySqlCaseStatement x);

    void endVisit(MySqlCaseStatement x);

    boolean visit(MySqlDeclareStatement x);

    void endVisit(MySqlDeclareStatement x);

    boolean visit(MySqlSelectIntoStatement x);

    void endVisit(MySqlSelectIntoStatement x);

    boolean visit(MySqlWhenStatement x);

    void endVisit(MySqlWhenStatement x);

    boolean visit(MySqlLeaveStatement x);

    void endVisit(MySqlLeaveStatement x);

    boolean visit(MySqlIterateStatement x);

    void endVisit(MySqlIterateStatement x);

    boolean visit(MySqlRepeatStatement x);

    void endVisit(MySqlRepeatStatement x);

    boolean visit(MySqlCursorDeclareStatement x);

    void endVisit(MySqlCursorDeclareStatement x);

    boolean visit(MySqlUpdateTableSource x);

    void endVisit(MySqlUpdateTableSource x);

    boolean visit(MySqlAlterTableAlterColumn x);

    void endVisit(MySqlAlterTableAlterColumn x);

    boolean visit(MySqlAlterTableForce x);

    void endVisit(MySqlAlterTableForce x);

    boolean visit(MySqlAlterTableLock x);

    void endVisit(MySqlAlterTableLock x);

    boolean visit(MySqlAlterTableOrderBy x);

    void endVisit(MySqlAlterTableOrderBy x);

    boolean visit(MySqlAlterTableValidation x);

    void endVisit(MySqlAlterTableValidation x);

    boolean visit(MySqlSubPartitionByKey x);

    void endVisit(MySqlSubPartitionByKey x);

    boolean visit(MySqlSubPartitionByList x);

    void endVisit(MySqlSubPartitionByList x);

    boolean visit(MySqlDeclareHandlerStatement x);

    void endVisit(MySqlDeclareHandlerStatement x);

    boolean visit(MySqlDeclareConditionStatement x);

    void endVisit(MySqlDeclareConditionStatement x);

    boolean visit(MySqlFlushStatement x);

    void endVisit(MySqlFlushStatement x);

    boolean visit(MySqlEventSchedule x);
    void endVisit(MySqlEventSchedule x);

    boolean visit(MySqlCreateEventStatement x);
    void endVisit(MySqlCreateEventStatement x);

    boolean visit(MySqlCreateAddLogFileGroupStatement x);
    void endVisit(MySqlCreateAddLogFileGroupStatement x);

    boolean visit(MySqlCreateServerStatement x);
    void endVisit(MySqlCreateServerStatement x);

    boolean visit(MySqlCreateTableSpaceStatement x);
    void endVisit(MySqlCreateTableSpaceStatement x);

    boolean visit(MySqlAlterEventStatement x);
    void endVisit(MySqlAlterEventStatement x);

    boolean visit(MySqlAlterLogFileGroupStatement x);
    void endVisit(MySqlAlterLogFileGroupStatement x);

    boolean visit(MySqlAlterServerStatement x);
    void endVisit(MySqlAlterServerStatement x);

    boolean visit(MySqlAlterTablespaceStatement x);
    void endVisit(MySqlAlterTablespaceStatement x);

    boolean visit(MySqlChecksumTableStatement x);
    void endVisit(MySqlChecksumTableStatement x);

    boolean visit(MySqlShowDatasourcesStatement x);
    void endVisit(MySqlShowDatasourcesStatement x);

    boolean visit(MySqlShowNodeStatement x);
    void endVisit(MySqlShowNodeStatement x);

    boolean visit(MySqlShowHelpStatement x);
    void endVisit(MySqlShowHelpStatement x);

    boolean visit(MySqlFlashbackStatement x);
    void endVisit(MySqlFlashbackStatement x);

    boolean visit(MySqlShowConfigStatement x);
    void endVisit(MySqlShowConfigStatement x);

    boolean visit(MySqlShowPlanCacheStatement x);
    void endVisit(MySqlShowPlanCacheStatement x);

    boolean visit(MySqlShowPhysicalProcesslistStatement x);
    void endVisit(MySqlShowPhysicalProcesslistStatement x);

    boolean visit(MySqlRenameSequenceStatement x);
    void endVisit(MySqlRenameSequenceStatement x);

    boolean visit(MySqlCheckTableStatement x);
    void endVisit(MySqlCheckTableStatement x);

    boolean visit(MysqlCreateFullTextCharFilterStatement x);
    void endVisit(MysqlCreateFullTextCharFilterStatement x);

    boolean visit(MysqlShowFullTextStatement x);
    void endVisit(MysqlShowFullTextStatement x);

    boolean visit(MysqlShowCreateFullTextStatement x);
    void endVisit(MysqlShowCreateFullTextStatement x);

    boolean visit(MysqlAlterFullTextStatement x);
    void endVisit(MysqlAlterFullTextStatement x);

    boolean visit(MysqlDropFullTextStatement x);
    void endVisit(MysqlDropFullTextStatement x);

    boolean visit(MysqlCreateFullTextTokenizerStatement x);
    void endVisit(MysqlCreateFullTextTokenizerStatement x);

    boolean visit(MysqlCreateFullTextTokenFilterStatement x);
    void endVisit(MysqlCreateFullTextTokenFilterStatement x);

    boolean visit(MysqlCreateFullTextAnalyzerStatement x);
    void endVisit(MysqlCreateFullTextAnalyzerStatement x);

    boolean visit(MysqlCreateFullTextDictionaryStatement x);
    void endVisit(MysqlCreateFullTextDictionaryStatement x);

    boolean visit(MySqlAlterTableAlterFullTextIndex x);
    void endVisit(MySqlAlterTableAlterFullTextIndex x);

    boolean visit(MySqlExecuteForAdsStatement x);
    void endVisit(MySqlExecuteForAdsStatement x);

    boolean visit(MySqlManageInstanceGroupStatement x);
    void endVisit(MySqlManageInstanceGroupStatement x);

    boolean visit(MySqlRaftMemberChangeStatement x);
    void endVisit(MySqlRaftMemberChangeStatement x);

    boolean visit(MySqlRaftLeaderTransferStatement x);
    void endVisit(MySqlRaftLeaderTransferStatement x);

    boolean visit(MySqlMigrateStatement x);
    void endVisit(MySqlMigrateStatement x);

    boolean visit(MySqlShowClusterNameStatement x);
    void endVisit(MySqlShowClusterNameStatement x);

    boolean visit(MySqlShowJobStatusStatement x);
    void endVisit(MySqlShowJobStatusStatement x);

    boolean visit(MySqlShowMigrateTaskStatusStatement x);
    void endVisit(MySqlShowMigrateTaskStatusStatement x);

    boolean visit(MySqlSubPartitionByValue x);
    void endVisit(MySqlSubPartitionByValue x);

    boolean visit(MySqlExtPartition x);

    void endVisit(MySqlExtPartition x);

    boolean visit(MySqlExtPartition.Item x);

    void endVisit(MySqlExtPartition.Item x);


} //
