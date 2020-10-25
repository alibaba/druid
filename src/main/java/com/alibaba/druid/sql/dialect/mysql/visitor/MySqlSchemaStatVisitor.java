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
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Mode;

public class MySqlSchemaStatVisitor extends SchemaStatVisitor implements MySqlASTVisitor {

    public MySqlSchemaStatVisitor() {
        super (DbType.mysql);
    }

    public MySqlSchemaStatVisitor(SchemaRepository repository) {
        super (repository);
    }

    public boolean visit(SQLSelectStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        return true;
    }

    @Override
    public DbType getDbType() {
        return DbType.mysql;
    }

    // DUAL
    public boolean visit(MySqlDeleteStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            from.accept(this);
        }

        SQLTableSource using = x.getUsing();
        if (using != null) {
            using.accept(this);
        }

        SQLTableSource tableSource = x.getTableSource();
        tableSource.accept(this);

        if (tableSource instanceof SQLExprTableSource) {
            TableStat stat = this.getTableStat((SQLExprTableSource) tableSource);
            stat.incrementDeleteCount();
        }

        accept(x.getWhere());

        accept(x.getOrderBy());
        accept(x.getLimit());

        return false;
    }

    @Override
    public void endVisit(MySqlInsertStatement x) {
        setModeOrigin(x);
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        setMode(x, Mode.Insert);

        TableStat stat = getTableStat(x.getTableSource());

        if (stat != null) {
            stat.incrementInsertCount();
        }

        accept(x.getColumns());
        accept(x.getValuesList());
        accept(x.getQuery());
        accept(x.getDuplicateKeyUpdate());

        return false;
    }

    @Override
    public boolean visit(MySqlTableIndex x) {
        return false;
    }

    @Override
    public boolean visit(MySqlKey x) {
        for (SQLObject item : x.getColumns()) {
            item.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlPrimaryKey x) {
        for (SQLSelectOrderByItem item : x.getColumns()) {
            SQLExpr expr = item.getExpr();
            expr.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLShowColumnsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowDatabaseStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCreateExternalCatalogStatement x) {
        return true;
    }

    @Override
    public boolean visit(MySqlCreateUserStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlPartitionByKey x) {
        accept(x.getColumns());
        return false;
    }

    @Override public boolean visit(MySqlUpdatePlanCacheStatement x) {
        return false;
    }

    @Override public boolean visit(MySqlShowPlanCacheStatusStatement x) {
        return false;
    }

    @Override public boolean visit(MySqlClearPlanCacheStatement x) {
        return false;
    }

    @Override public boolean visit(MySqlDisabledPlanCacheStatement x) {
        return false;
    }

    @Override public boolean visit(MySqlAlterDatabaseSetOption x) {
        return false;
    }

    @Override public boolean visit(MySqlAlterDatabaseKillJob x) {
        return false;
    }


    @Override public boolean visit(MySqlExplainPlanCacheStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlOutFileExpr x) {
        return false;
    }

    @Override
    public boolean visit(MySqlExplainStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        SQLName tableName = x.getTableName();
        if (tableName != null) {
            getTableStat(tableName);

            SQLName columnName = x.getColumnName();
            if (columnName != null) {
                addColumn(tableName, columnName.getSimpleName());
            }
        }

        if (x.getStatement() != null) {
            accept(x.getStatement());
        }

        return false;
    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        visit((SQLUpdateStatement) x);
        for (SQLExpr item : x.getReturning()) {
            item.accept(this);
        }
        
        return false;
    }

    @Override
    public boolean visit(MySqlSetTransactionStatement x) {
        return false;
    }

    @Override public boolean visit(MySqlShowHMSMetaStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowAuthorsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowBinaryLogsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowMasterLogsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowCollationStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowBinLogEventsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowCharacterSetStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowContributorsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateDatabaseStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateEventStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateFunctionStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateProcedureStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLShowCreateTableStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateTriggerStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowEngineStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowEnginesStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowErrorsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowEventsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowFunctionCodeStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowFunctionStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowGrantsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlUserName x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowMasterStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowOpenTablesStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowPluginsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowPartitionsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowPrivilegesStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowProcedureCodeStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowProcedureStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowProcessListStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowProfileStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowProfilesStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowRelayLogEventsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowRuleStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowRuleStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowSlaveHostsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowSequencesStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowSlowStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowSlaveStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowTableStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MysqlShowDbLockStatement x) {
        return false;
    }

    @Override
    public boolean visit(MysqlShowHtcStatement x) {
        return false;
    }

    @Override
    public boolean visit(MysqlShowStcStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowTriggersStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowTraceStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowBroadcastsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowDdlStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowDsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowTopologyStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowVariantsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlRenameTableStatement.Item x) {
        return false;
    }

    @Override
    public boolean visit(MySqlRenameTableStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlUseIndexHint x) {
        return false;
    }

    @Override
    public boolean visit(MySqlIgnoreIndexHint x) {
        return false;
    }

    @Override
    public boolean visit(MySqlLockTableStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlLockTableStatement.Item x) {
        return false;
    }

    @Override
    public boolean visit(MySqlUnlockTablesStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlForceIndexHint x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableChangeColumn x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();

        SQLName table = stmt.getName();

        SQLName column = x.getColumnName();
        String columnName = column.toString();
        addColumn(table, columnName);
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableModifyColumn x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();

        SQLName table = stmt.getName();

        SQLName column = x.getNewColumnDefinition().getName();
        String columnName = column.toString();
        addColumn(table, columnName);

        return false;
    }

    @Override
    public boolean visit(SQLAlterCharacter x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableOption x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCreateTableStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        boolean val = super.visit((SQLCreateTableStatement) x);

        SQLExpr union = x.getOption("union");
        if (union instanceof SQLListExpr) {
            for (SQLExpr item : ((SQLListExpr) union).getItems()) {
                if (item instanceof SQLName) {
                    getTableStatWithUnwrap(item);
                }
            }
        }

        return val;
    }

    @Override
    public boolean visit(MySqlHelpStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCharExpr x) {
        return false;
    }

    @Override
    public boolean visit(MySqlUnique x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableDiscardTablespace x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableImportTablespace x) {
        return false;
    }

    @Override
    public boolean visit(TableSpaceOption x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableAlterColumn x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableForce x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableLock x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableOrderBy x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableValidation x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCaseStatement x) {
        accept(x.getWhenList());
        return false;
    }

    @Override
    public boolean visit(MySqlSelectIntoStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlWhenStatement x) {
        accept(x.getStatements());
        return false;
    }

    @Override
    public boolean visit(MySqlLeaveStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlIterateStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlRepeatStatement x) {
        accept(x.getStatements());
        return false;
    }

    @Override
    public boolean visit(MySqlCursorDeclareStatement x) {
        accept(x.getSelect());
        return false;
    }

    @Override
    public boolean visit(MySqlUpdateTableSource x) {
        if (x.getUpdate() != null) {
            return this.visit(x.getUpdate());
        }
        return false;
    }

    @Override
    public boolean visit(MySqlSubPartitionByKey x) {
        return false;
    }

    @Override
    public boolean visit(MySqlSubPartitionByList x) {
        return false;
    }

	@Override
	public boolean visit(MySqlDeclareHandlerStatement x) {
		return false;
	}

	@Override
	public boolean visit(MySqlDeclareConditionStatement x) {
		return false;
	}

    @Override
    public boolean visit(MySqlFlushStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlEventSchedule x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCreateEventStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCreateAddLogFileGroupStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCreateServerStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCreateTableSpaceStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterEventStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterLogFileGroupStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterServerStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTablespaceStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlChecksumTableStatement x) {
        return true;
    }

    @Override
    public boolean visit(MySqlShowDatasourcesStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowNodeStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowHelpStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlFlashbackStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowConfigStatement x) {
        return false;
    }
    @Override
    public boolean visit(MySqlShowPlanCacheStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowPhysicalProcesslistStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlRenameSequenceStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlCheckTableStatement x) {
        for (SQLExprTableSource tableSource : x.getTables()) {
            tableSource.accept(this);
        }
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextCharFilterStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlShowFullTextStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlShowCreateFullTextStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlAlterFullTextStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlDropFullTextStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextTokenizerStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextTokenFilterStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextAnalyzerStatement x) {
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextDictionaryStatement x) {
        return false;
    }

    @Override public boolean visit(MySqlAlterTableAlterFullTextIndex x) {
        return false;
    }

    @Override
    public boolean visit(MySqlExecuteForAdsStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlManageInstanceGroupStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlRaftMemberChangeStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlRaftLeaderTransferStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlMigrateStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowClusterNameStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowJobStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlShowMigrateTaskStatusStatement x) {
        return false;
    }

    @Override
    public boolean visit(MySqlSubPartitionByValue x) {
        return false;
    }

    @Override public boolean visit(MySqlExtPartition x) {
        return true;
    }

    @Override public boolean visit(MySqlExtPartition.Item x) {
        return false;
    }
}
