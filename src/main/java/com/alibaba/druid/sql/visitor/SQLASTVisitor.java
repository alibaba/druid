/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") {
        return true;
    }
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
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInputOutputFormat;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.SQLAlterResourceGroupStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.SQLCreateResourceGroupStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.SQLListResourceGroupStatement;

public interface SQLASTVisitor {

    default void endVisit(SQLAllColumnExpr x) {
    }

    default void endVisit(SQLBetweenExpr x) {
    }

    default void endVisit(SQLBinaryOpExpr x) {
    }

    default void endVisit(SQLCaseExpr x) {
    }

    default void endVisit(SQLCaseExpr.Item x) {
    }

    default void endVisit(SQLCaseStatement x) {
    }

    default void endVisit(SQLCaseStatement.Item x) {
    }

    default void endVisit(SQLCharExpr x) {
    }

    default void endVisit(SQLIdentifierExpr x) {
    }

    default void endVisit(SQLInListExpr x) {
    }

    default void endVisit(SQLIntegerExpr x) {
    }

    default void endVisit(SQLSmallIntExpr x) {
    }

    default void endVisit(SQLBigIntExpr x) {
    }

    default void endVisit(SQLTinyIntExpr x) {
    }

    default void endVisit(SQLExistsExpr x) {
    }

    default void endVisit(SQLNCharExpr x) {
    }

    default void endVisit(SQLNotExpr x) {
    }

    default void endVisit(SQLNullExpr x) {
    }

    default void endVisit(SQLNumberExpr x) {
    }

    default void endVisit(SQLRealExpr x) {
    }

    default void endVisit(SQLPropertyExpr x) {
    }

    default void endVisit(SQLSelectGroupByClause x) {
    }

    default void endVisit(SQLSelectItem x) {
    }

    default void endVisit(SQLSelectStatement x) {
    }

    default void postVisit(SQLObject x) {
    }

    default void preVisit(SQLObject x) {
    }

    default boolean visit(SQLAllColumnExpr x) {
        return true;
    }

    default boolean visit(SQLBetweenExpr x) {
        return true;
    }

    default boolean visit(SQLBinaryOpExpr x) {
        return true;
    }

    default boolean visit(SQLCaseExpr x) {
        return true;
    }

    default boolean visit(SQLCaseExpr.Item x) {
        return true;
    }

    default boolean visit(SQLCaseStatement x) {
        return true;
    }

    default boolean visit(SQLCaseStatement.Item x) {
        return true;
    }

    default boolean visit(SQLCastExpr x) {
        return true;
    }

    default boolean visit(SQLCharExpr x) {
        return true;
    }

    default boolean visit(SQLExistsExpr x) {
        return true;
    }

    default boolean visit(SQLIdentifierExpr x) {
        return true;
    }

    default boolean visit(SQLInListExpr x) {
        return true;
    }

    default boolean visit(SQLIntegerExpr x) {
        return true;
    }

    default boolean visit(SQLSmallIntExpr x) {
        return true;
    }

    default boolean visit(SQLBigIntExpr x) {
        return true;
    }

    default boolean visit(SQLTinyIntExpr x) {
        return true;
    }

    default boolean visit(SQLNCharExpr x) {
        return true;
    }

    default boolean visit(SQLNotExpr x) {
        return true;
    }

    default boolean visit(SQLNullExpr x) {
        return true;
    }

    default boolean visit(SQLNumberExpr x) {
        return true;
    }

    default boolean visit(SQLRealExpr x) {
        return true;
    }

    default boolean visit(SQLPropertyExpr x) {
        return true;
    }

    default boolean visit(SQLSelectGroupByClause x) {
        return true;
    }

    default boolean visit(SQLSelectItem x) {
        return true;
    }

    default void endVisit(SQLCastExpr x) {
    }

    default boolean visit(SQLSelectStatement x) {
        return true;
    }

    default void endVisit(SQLAggregateExpr x) {
    }

    default boolean visit(SQLAggregateExpr x) {
        return true;
    }

    default boolean visit(SQLVariantRefExpr x) {
        return true;
    }

    default void endVisit(SQLVariantRefExpr x) {

    }

    default boolean visit(SQLQueryExpr x) {
        return true;
    }

    default void endVisit(SQLQueryExpr x) {

    }

    default boolean visit(SQLUnaryExpr x) {
        return true;
    }

    default void endVisit(SQLUnaryExpr x) {

    }

    default boolean visit(SQLHexExpr x) {
        return true;
    }

    default void endVisit(SQLHexExpr x) {

    }

    default boolean visit(SQLSelect x) {
        return true;
    }

    default void endVisit(SQLSelect select) {
    }

    default boolean visit(SQLSelectQueryBlock x) {
        return true;
    }

    default void endVisit(SQLSelectQueryBlock x) {
    }

    default boolean visit(SQLExprTableSource x) {
        return true;
    }

    default void endVisit(SQLExprTableSource x) {
    }

    default boolean visit(SQLOrderBy x) {
        return true;
    }

    default void endVisit(SQLOrderBy x) {

    }

    default boolean visit(SQLSelectOrderByItem x) {
        return true;
    }

    default void endVisit(SQLSelectOrderByItem x) {
    }

    default boolean visit(SQLDropTableStatement x) {
        return true;
    }

    default void endVisit(SQLDropTableStatement x) {
    }

    default boolean visit(SQLCreateTableStatement x) {
        return true;
    }

    default void endVisit(SQLCreateTableStatement x) {
    }

    default boolean visit(SQLColumnDefinition x) {
        return true;
    }

    default void endVisit(SQLColumnDefinition x) {
    }

    default boolean visit(SQLColumnDefinition.Identity x) {
        return true;
    }

    default void endVisit(SQLColumnDefinition.Identity x) {
    }

    default boolean visit(SQLDataType x) {
        return true;
    }

    default void endVisit(SQLDataType x) {
    }

    default boolean visit(SQLCharacterDataType x) {
        return true;
    }

    default void endVisit(SQLCharacterDataType x) {
    }

    default boolean visit(SQLDeleteStatement x) {
        return true;
    }

    default void endVisit(SQLDeleteStatement x) {
    }

    default boolean visit(SQLCurrentOfCursorExpr x) {
        return true;
    }

    default void endVisit(SQLCurrentOfCursorExpr x) {
    }

    default boolean visit(SQLInsertStatement x) {
        return true;
    }

    default void endVisit(SQLInsertStatement x) {

    }

    default boolean visit(SQLInsertStatement.ValuesClause x) {
        return true;
    }

    default void endVisit(SQLInsertStatement.ValuesClause x) {
    }

    default boolean visit(SQLUpdateSetItem x) {
        return true;
    }

    default void endVisit(SQLUpdateSetItem x) {
    }

    default boolean visit(SQLUpdateStatement x) {
        return true;
    }

    default void endVisit(SQLUpdateStatement x) {
    }

    default boolean visit(SQLCreateViewStatement x) {
        return true;
    }

    default void endVisit(SQLCreateViewStatement x) {
    }

    default boolean visit(SQLCreateViewStatement.Column x) {
        return true;
    }

    default void endVisit(SQLCreateViewStatement.Column x) {
    }

    default boolean visit(SQLNotNullConstraint x) {
        return true;
    }

    default void endVisit(SQLNotNullConstraint x) {
    }

    default void endVisit(SQLMethodInvokeExpr x) {

    }

    default boolean visit(SQLMethodInvokeExpr x) {
        return true;
    }

    default void endVisit(SQLUnionQuery x) {

    }

    default boolean visit(SQLUnionQuery x) {
        return true;
    }

    default void endVisit(SQLSetStatement x) {
    }

    default boolean visit(SQLSetStatement x) {
        return true;
    }

    default void endVisit(SQLAssignItem x) {
    }

    default boolean visit(SQLAssignItem x) {
        return true;
    }

    default void endVisit(SQLCallStatement x) {

    }

    default boolean visit(SQLCallStatement x) {
        return true;
    }

    default void endVisit(SQLJoinTableSource x) {
    }

    default boolean visit(SQLJoinTableSource x) {
        return true;
    }

    default void endVisit(SQLJoinTableSource.UDJ x) {
    }

    default boolean visit(SQLJoinTableSource.UDJ x) {
        return true;
    }

    default void endVisit(SQLSomeExpr x) {
    }

    default boolean visit(SQLSomeExpr x) {
        return true;
    }

    default void endVisit(SQLAnyExpr x) {
    }

    default boolean visit(SQLAnyExpr x) {
        return true;
    }

    default void endVisit(SQLAllExpr x) {
    }

    default boolean visit(SQLAllExpr x) {
        return true;
    }

    default void endVisit(SQLInSubQueryExpr x) {
    }

    default boolean visit(SQLInSubQueryExpr x) {
        return true;
    }

    default void endVisit(SQLListExpr x) {
    }

    default boolean visit(SQLListExpr x) {
        return true;
    }

    default void endVisit(SQLSubqueryTableSource x) {
    }

    default boolean visit(SQLSubqueryTableSource x) {
        return true;
    }

    default void endVisit(SQLTruncateStatement x) {
    }

    default boolean visit(SQLTruncateStatement x) {
        return true;
    }

    default void endVisit(SQLDefaultExpr x) {
    }

    default boolean visit(SQLDefaultExpr x) {
        return true;
    }

    default void endVisit(SQLCommentStatement x) {
    }

    default boolean visit(SQLCommentStatement x) {
        return true;
    }

    default void endVisit(SQLUseStatement x) {
    }

    default boolean visit(SQLUseStatement x) {
        return true;
    }

    default boolean visit(SQLAlterTableAddColumn x) {
        return true;
    }

    default void endVisit(SQLAlterTableAddColumn x) {
    }

    default boolean visit(SQLAlterTableDeleteByCondition x) {
        return true;
    }

    default void endVisit(SQLAlterTableDeleteByCondition x) {

    }

    default boolean visit(SQLAlterTableModifyClusteredBy x) {
        return true;
    }

    default void endVisit(SQLAlterTableModifyClusteredBy x) {
    }

    default boolean visit(SQLAlterTableDropColumnItem x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropColumnItem x) {
    }

    default boolean visit(SQLAlterTableDropIndex x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropIndex x) {
    }

    default boolean visit(SQLAlterTableGroupStatement x) {
        return true;
    }

    default void endVisit(SQLAlterTableGroupStatement x) {
    }

    default boolean visit(SQLAlterSystemSetConfigStatement x) {
        return true;
    }

    default void endVisit(SQLAlterSystemSetConfigStatement x) {
    }

    default boolean visit(SQLAlterSystemGetConfigStatement x) {
        return true;
    }

    default void endVisit(SQLAlterSystemGetConfigStatement x) {
    }

    default boolean visit(SQLDropIndexStatement x) {
        return true;
    }

    default void endVisit(SQLDropIndexStatement x) {
    }

    default boolean visit(SQLDropViewStatement x) {
        return true;
    }

    default void endVisit(SQLDropViewStatement x) {
    }

    default boolean visit(SQLSavePointStatement x) {
        return true;
    }

    default void endVisit(SQLSavePointStatement x) {
    }

    default boolean visit(SQLRollbackStatement x) {
        return true;
    }

    default void endVisit(SQLRollbackStatement x) {
    }

    default boolean visit(SQLReleaseSavePointStatement x) {
        return true;
    }

    default void endVisit(SQLReleaseSavePointStatement x) {
    }

    default void endVisit(SQLCommentHint x) {
    }

    default boolean visit(SQLCommentHint x) {
        return true;
    }

    default void endVisit(SQLCreateDatabaseStatement x) {
    }

    default boolean visit(SQLCreateDatabaseStatement x) {
        return true;
    }

    default void endVisit(SQLOver x) {
    }

    default boolean visit(SQLOver x) {
        return true;
    }

    default void endVisit(SQLKeep x) {
    }

    default boolean visit(SQLKeep x) {
        return true;
    }

    default void endVisit(SQLColumnPrimaryKey x) {
    }

    default boolean visit(SQLColumnPrimaryKey x) {
        return true;
    }

    default boolean visit(SQLColumnUniqueKey x) {
        return true;
    }

    default void endVisit(SQLColumnUniqueKey x) {
    }

    default void endVisit(SQLWithSubqueryClause x) {

    }

    default boolean visit(SQLWithSubqueryClause x) {
        return true;
    }

    default void endVisit(SQLWithSubqueryClause.Entry x) {
    }

    default boolean visit(SQLWithSubqueryClause.Entry x) {
        return true;
    }

    default void endVisit(SQLAlterTableAlterColumn x) {
    }

    default boolean visit(SQLAlterTableAlterColumn x) {
        return true;
    }

    default boolean visit(SQLCheck x) {
        return true;
    }

    default void endVisit(SQLCheck x) {
    }

    default boolean visit(SQLDefault x) {
        return true;
    }

    default void endVisit(SQLDefault x) {
    }

    default boolean visit(SQLAlterTableDropForeignKey x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropForeignKey x) {
    }

    default boolean visit(SQLAlterTableDropPrimaryKey x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropPrimaryKey x) {

    }

    default boolean visit(SQLAlterTableDisableKeys x) {
        return true;
    }

    default void endVisit(SQLAlterTableDisableKeys x) {

    }

    default boolean visit(SQLAlterTableEnableKeys x) {
        return true;
    }

    default void endVisit(SQLAlterTableEnableKeys x) {

    }

    default boolean visit(SQLAlterTableStatement x) {
        return true;
    }

    default void endVisit(SQLAlterTableStatement x) {
    }

    default boolean visit(SQLAlterTableDisableConstraint x) {
        return true;
    }

    default void endVisit(SQLAlterTableDisableConstraint x) {

    }

    default boolean visit(SQLAlterTableEnableConstraint x) {
        return true;
    }

    default void endVisit(SQLAlterTableEnableConstraint x) {

    }

    default boolean visit(SQLColumnCheck x) {
        return true;
    }

    default void endVisit(SQLColumnCheck x) {

    }

    default boolean visit(SQLExprHint x) {
        return true;
    }

    default void endVisit(SQLExprHint x) {

    }

    default boolean visit(SQLAlterTableDropConstraint x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropConstraint x) {

    }

    default boolean visit(SQLUnique x) {
        return true;
    }

    default void endVisit(SQLUnique x) {

    }

    default boolean visit(SQLPrimaryKeyImpl x) {
        return true;
    }

    default void endVisit(SQLPrimaryKeyImpl x) {

    }

    default boolean visit(SQLCreateIndexStatement x) {
        return true;
    }

    default void endVisit(SQLCreateIndexStatement x) {

    }

    default boolean visit(SQLAlterTableRenameColumn x) {
        return true;
    }

    default void endVisit(SQLAlterTableRenameColumn x) {

    }

    default boolean visit(SQLColumnReference x) {
        return true;
    }

    default void endVisit(SQLColumnReference x) {

    }

    default boolean visit(SQLForeignKeyImpl x) {
        return true;
    }

    default void endVisit(SQLForeignKeyImpl x) {

    }

    default boolean visit(SQLDropSequenceStatement x) {
        return true;
    }

    default void endVisit(SQLDropSequenceStatement x) {

    }

    default boolean visit(SQLDropTriggerStatement x) {
        return true;
    }

    default void endVisit(SQLDropTriggerStatement x) {

    }

    default void endVisit(SQLDropUserStatement x) {

    }

    default boolean visit(SQLDropUserStatement x) {
        return true;
    }

    default void endVisit(SQLExplainStatement x) {

    }

    default boolean visit(SQLExplainStatement x) {
        return true;
    }

    default void endVisit(SQLGrantStatement x) {

    }

    default boolean visit(SQLGrantStatement x) {
        return true;
    }

    default void endVisit(SQLDropDatabaseStatement x) {

    }

    default boolean visit(SQLDropDatabaseStatement x) {
        return true;
    }

    default void endVisit(SQLIndexOptions x) {

    }

    default boolean visit(SQLIndexOptions x) {
        return true;
    }

    default void endVisit(SQLIndexDefinition x) {

    }

    default boolean visit(SQLIndexDefinition x) {
        return true;
    }

    default void endVisit(SQLAlterTableAddIndex x) {

    }

    default boolean visit(SQLAlterTableAddIndex x) {
        return true;
    }

    default void endVisit(SQLAlterTableAddConstraint x) {

    }

    default boolean visit(SQLAlterTableAddConstraint x) {
        return true;
    }

    default void endVisit(SQLCreateTriggerStatement x) {

    }

    default boolean visit(SQLCreateTriggerStatement x) {
        return true;
    }

    default void endVisit(SQLDropFunctionStatement x) {

    }

    default boolean visit(SQLDropFunctionStatement x) {
        return true;
    }

    default void endVisit(SQLDropTableSpaceStatement x) {

    }

    default boolean visit(SQLDropTableSpaceStatement x) {
        return true;
    }

    default void endVisit(SQLDropProcedureStatement x) {

    }

    default boolean visit(SQLDropProcedureStatement x) {
        return true;
    }

    default void endVisit(SQLBooleanExpr x) {

    }

    default boolean visit(SQLBooleanExpr x) {
        return true;
    }

    default void endVisit(SQLUnionQueryTableSource x) {

    }

    default boolean visit(SQLUnionQueryTableSource x) {
        return true;
    }

    default void endVisit(SQLTimestampExpr x) {

    }

    default boolean visit(SQLTimestampExpr x) {
        return true;
    }

    default void endVisit(SQLDateTimeExpr x) {

    }

    default boolean visit(SQLDateTimeExpr x) {
        return true;
    }

    default void endVisit(SQLDoubleExpr x) {

    }

    default boolean visit(SQLDoubleExpr x) {
        return true;
    }

    default void endVisit(SQLFloatExpr x) {

    }

    default boolean visit(SQLFloatExpr x) {
        return true;
    }

    default void endVisit(SQLRevokeStatement x) {

    }

    default boolean visit(SQLRevokeStatement x) {
        return true;
    }

    default void endVisit(SQLBinaryExpr x) {

    }

    default boolean visit(SQLBinaryExpr x) {
        return true;
    }

    default void endVisit(SQLAlterTableRename x) {

    }

    default boolean visit(SQLAlterTableRename x) {
        return true;
    }

    default void endVisit(SQLAlterViewRenameStatement x) {

    }

    default boolean visit(SQLAlterViewRenameStatement x) {
        return true;
    }

    default void endVisit(SQLShowTablesStatement x) {

    }

    default boolean visit(SQLShowTablesStatement x) {
        return true;
    }

    default void endVisit(SQLAlterTableAddPartition x) {

    }

    default boolean visit(SQLAlterTableAddPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableAddExtPartition x) {

    }

    default boolean visit(SQLAlterTableAddExtPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropExtPartition x) {

    }

    default boolean visit(SQLAlterTableDropExtPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropPartition x) {

    }

    default boolean visit(SQLAlterTableDropPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableRenamePartition x) {

    }

    default boolean visit(SQLAlterTableRenamePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableSetComment x) {

    }

    default boolean visit(SQLAlterTableSetComment x) {
        return true;
    }

    default void endVisit(SQLAlterTableSetLifecycle x) {

    }

    default boolean visit(SQLPrivilegeItem x) {
        return true;
    }

    default void endVisit(SQLPrivilegeItem x) {

    }

    default boolean visit(SQLAlterTableSetLifecycle x) {
        return true;
    }

    default void endVisit(SQLAlterTableEnableLifecycle x) {

    }

    default boolean visit(SQLAlterTableEnableLifecycle x) {
        return true;
    }

    default void endVisit(SQLAlterTablePartition x) {

    }

    default boolean visit(SQLAlterTablePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTablePartitionSetProperties x) {

    }

    default boolean visit(SQLAlterTablePartitionSetProperties x) {
        return true;
    }

    default void endVisit(SQLAlterTableDisableLifecycle x) {

    }

    default boolean visit(SQLAlterTableDisableLifecycle x) {
        return true;
    }

    default void endVisit(SQLAlterTableTouch x) {

    }

    default boolean visit(SQLAlterTableTouch x) {
        return true;
    }

    default void endVisit(SQLArrayExpr x) {

    }

    default boolean visit(SQLArrayExpr x) {
        return true;
    }

    default void endVisit(SQLOpenStatement x) {

    }

    default boolean visit(SQLOpenStatement x) {
        return true;
    }

    default void endVisit(SQLFetchStatement x) {

    }

    default boolean visit(SQLFetchStatement x) {
        return true;
    }

    default void endVisit(SQLCloseStatement x) {

    }

    default boolean visit(SQLCloseStatement x) {
        return true;
    }

    default boolean visit(SQLGroupingSetExpr x) {
        return true;
    }

    default void endVisit(SQLGroupingSetExpr x) {

    }

    default boolean visit(SQLIfStatement x) {
        return true;
    }

    default void endVisit(SQLIfStatement x) {

    }

    default boolean visit(SQLIfStatement.ElseIf x) {
        return true;
    }

    default void endVisit(SQLIfStatement.ElseIf x) {

    }

    default boolean visit(SQLIfStatement.Else x) {
        return true;
    }

    default void endVisit(SQLIfStatement.Else x) {

    }

    default boolean visit(SQLLoopStatement x) {
        return true;
    }

    default void endVisit(SQLLoopStatement x) {

    }

    default boolean visit(SQLParameter x) {
        return true;
    }

    default void endVisit(SQLParameter x) {

    }

    default boolean visit(SQLCreateProcedureStatement x) {
        return true;
    }

    default void endVisit(SQLCreateProcedureStatement x) {

    }

    default boolean visit(SQLCreateFunctionStatement x) {
        return true;
    }

    default void endVisit(SQLCreateFunctionStatement x) {

    }

    default boolean visit(SQLBlockStatement x) {
        return true;
    }

    default void endVisit(SQLBlockStatement x) {

    }

    default boolean visit(SQLAlterTableDropKey x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropKey x) {

    }

    default boolean visit(SQLDeclareItem x) {
        return true;
    }

    default void endVisit(SQLDeclareItem x) {

    }

    default boolean visit(SQLPartitionValue x) {
        return true;
    }

    default void endVisit(SQLPartitionValue x) {

    }

    default boolean visit(SQLPartition x) {
        return true;
    }

    default void endVisit(SQLPartition x) {

    }

    default boolean visit(SQLPartitionByRange x) {
        return true;
    }

    default void endVisit(SQLPartitionByRange x) {

    }

    default boolean visit(SQLPartitionByHash x) {
        return true;
    }

    default void endVisit(SQLPartitionByHash x) {

    }

    default boolean visit(SQLPartitionByList x) {
        return true;
    }

    default void endVisit(SQLPartitionByList x) {

    }

    default boolean visit(SQLSubPartition x) {
        return true;
    }

    default void endVisit(SQLSubPartition x) {

    }

    default boolean visit(SQLSubPartitionByHash x) {
        return true;
    }

    default void endVisit(SQLSubPartitionByHash x) {

    }

    default boolean visit(SQLSubPartitionByRange x) {
        return true;
    }

    default void endVisit(SQLSubPartitionByRange x) {

    }

    default boolean visit(SQLSubPartitionByList x) {
        return true;
    }

    default void endVisit(SQLSubPartitionByList x) {

    }

    default boolean visit(SQLAlterDatabaseStatement x) {
        return true;
    }

    default void endVisit(SQLAlterDatabaseStatement x) {

    }

    default boolean visit(SQLAlterTableConvertCharSet x) {
        return true;
    }

    default void endVisit(SQLAlterTableConvertCharSet x) {

    }

    default boolean visit(SQLAlterTableReOrganizePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableReOrganizePartition x) {

    }

    default boolean visit(SQLAlterTableCoalescePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableCoalescePartition x) {

    }

    default boolean visit(SQLAlterTableTruncatePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableTruncatePartition x) {

    }

    default boolean visit(SQLAlterTableDiscardPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableDiscardPartition x) {

    }

    default boolean visit(SQLAlterTableImportPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableImportPartition x) {

    }

    default boolean visit(SQLAlterTableAnalyzePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableAnalyzePartition x) {

    }

    default boolean visit(SQLAlterTableCheckPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableCheckPartition x) {

    }

    default boolean visit(SQLAlterTableOptimizePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableOptimizePartition x) {

    }

    default boolean visit(SQLAlterTableRebuildPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableRebuildPartition x) {

    }

    default boolean visit(SQLAlterTableRepairPartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableRepairPartition x) {

    }

    default boolean visit(SQLSequenceExpr x) {
        return true;
    }

    default void endVisit(SQLSequenceExpr x) {

    }

    default boolean visit(SQLMergeStatement x) {
        return true;
    }

    default void endVisit(SQLMergeStatement x) {

    }

    default boolean visit(SQLMergeStatement.MergeUpdateClause x) {
        return true;
    }

    default void endVisit(SQLMergeStatement.MergeUpdateClause x) {

    }

    default boolean visit(SQLMergeStatement.MergeInsertClause x) {
        return true;
    }

    default void endVisit(SQLMergeStatement.MergeInsertClause x) {

    }

    default boolean visit(SQLErrorLoggingClause x) {
        return true;
    }

    default void endVisit(SQLErrorLoggingClause x) {

    }

    default boolean visit(SQLNullConstraint x) {
        return true;
    }

    default void endVisit(SQLNullConstraint x) {

    }

    default boolean visit(SQLCreateSequenceStatement x) {
        return true;
    }

    default void endVisit(SQLCreateSequenceStatement x) {

    }

    default boolean visit(SQLDateExpr x) {
        return true;
    }

    default void endVisit(SQLDateExpr x) {

    }

    default boolean visit(SQLLimit x) {
        return true;
    }

    default void endVisit(SQLLimit x) {
    }

    default void endVisit(SQLStartTransactionStatement x) {

    }

    default boolean visit(SQLStartTransactionStatement x) {
        return true;
    }

    default void endVisit(SQLDescribeStatement x) {

    }

    default boolean visit(SQLDescribeStatement x) {
        return true;
    }

    /**
     * support procedure
     */
    default boolean visit(SQLWhileStatement x) {
        return true;
    }

    default void endVisit(SQLWhileStatement x) {

    }

    default boolean visit(SQLDeclareStatement x) {
        return true;
    }

    default void endVisit(SQLDeclareStatement x) {

    }

    default boolean visit(SQLReturnStatement x) {
        return true;
    }

    default void endVisit(SQLReturnStatement x) {

    }

    default boolean visit(SQLArgument x) {
        return true;
    }

    default void endVisit(SQLArgument x) {

    }

    default boolean visit(SQLCommitStatement x) {
        return true;
    }

    default void endVisit(SQLCommitStatement x) {

    }

    default boolean visit(SQLFlashbackExpr x) {
        return true;
    }

    default void endVisit(SQLFlashbackExpr x) {

    }

    default boolean visit(SQLCreateMaterializedViewStatement x) {
        return true;
    }

    default void endVisit(SQLCreateMaterializedViewStatement x) {

    }

    default boolean visit(SQLShowCreateMaterializedViewStatement x) {
        return true;
    }

    default void endVisit(SQLShowCreateMaterializedViewStatement x) {

    }

    default boolean visit(SQLBinaryOpExprGroup x) {
        return true;
    }

    default void endVisit(SQLBinaryOpExprGroup x) {

    }

    default boolean visit(SQLScriptCommitStatement x) {
        return true;
    }

    default void endVisit(SQLScriptCommitStatement x) {

    }

    default boolean visit(SQLReplaceStatement x) {
        return true;
    }

    default void endVisit(SQLReplaceStatement x) {

    }

    default boolean visit(SQLCreateUserStatement x) {
        return true;
    }

    default void endVisit(SQLCreateUserStatement x) {

    }

    default boolean visit(SQLAlterFunctionStatement x) {
        return true;
    }

    default void endVisit(SQLAlterFunctionStatement x) {

    }

    default boolean visit(SQLAlterTypeStatement x) {
        return true;
    }

    default void endVisit(SQLAlterTypeStatement x) {

    }

    default boolean visit(SQLIntervalExpr x) {
        return true;
    }

    default void endVisit(SQLIntervalExpr x) {

    }

    default boolean visit(SQLLateralViewTableSource x) {
        return true;
    }

    default void endVisit(SQLLateralViewTableSource x) {

    }

    default boolean visit(SQLShowErrorsStatement x) {
        return true;
    }

    default void endVisit(SQLShowErrorsStatement x) {

    }

    default boolean visit(SQLShowGrantsStatement x) {
        return true;
    }

    default void endVisit(SQLShowGrantsStatement x) {

    }

    default boolean visit(SQLShowPackagesStatement x) {
        return true;
    }

    default void endVisit(SQLShowPackagesStatement x) {

    }

    default boolean visit(SQLShowRecylebinStatement x) {
        return true;
    }

    default void endVisit(SQLShowRecylebinStatement x) {

    }

    default boolean visit(SQLAlterCharacter x) {
        return true;
    }

    default void endVisit(SQLAlterCharacter x) {

    }

    default boolean visit(SQLExprStatement x) {
        return true;
    }

    default void endVisit(SQLExprStatement x) {

    }

    default boolean visit(SQLAlterProcedureStatement x) {
        return true;
    }

    default void endVisit(SQLAlterProcedureStatement x) {

    }

    default boolean visit(SQLAlterViewStatement x) {
        return true;
    }

    default void endVisit(SQLAlterViewStatement x) {

    }

    default boolean visit(SQLDropEventStatement x) {
        return true;
    }

    default void endVisit(SQLDropEventStatement x) {

    }

    default boolean visit(SQLDropLogFileGroupStatement x) {
        return true;
    }

    default void endVisit(SQLDropLogFileGroupStatement x) {

    }

    default boolean visit(SQLDropServerStatement x) {
        return true;
    }

    default void endVisit(SQLDropServerStatement x) {

    }

    default boolean visit(SQLDropSynonymStatement x) {
        return true;
    }

    default void endVisit(SQLDropSynonymStatement x) {

    }

    default boolean visit(SQLRecordDataType x) {
        return true;
    }

    default void endVisit(SQLRecordDataType x) {

    }

    default boolean visit(SQLDropTypeStatement x) {
        return true;
    }

    default void endVisit(SQLDropTypeStatement x) {

    }

    default boolean visit(SQLExternalRecordFormat x) {
        return true;
    }

    default void endVisit(SQLExternalRecordFormat x) {

    }

    default boolean visit(SQLArrayDataType x) {
        return true;
    }

    default void endVisit(SQLArrayDataType x) {

    }

    default boolean visit(SQLMapDataType x) {
        return true;
    }

    default void endVisit(SQLMapDataType x) {

    }

    default boolean visit(SQLStructDataType x) {
        return true;
    }

    default void endVisit(SQLStructDataType x) {

    }

    default boolean visit(SQLRowDataType x) {
        return true;
    }

    default void endVisit(SQLRowDataType x) {

    }

    default boolean visit(SQLStructDataType.Field x) {
        return true;
    }

    default void endVisit(SQLStructDataType.Field x) {

    }

    default boolean visit(SQLDropMaterializedViewStatement x) {
        return true;
    }

    default void endVisit(SQLDropMaterializedViewStatement x) {

    }

    default boolean visit(SQLShowMaterializedViewStatement x) {
        return true;
    }

    default void endVisit(SQLShowMaterializedViewStatement x) {

    }

    default boolean visit(SQLRefreshMaterializedViewStatement x) {
        return true;
    }

    default void endVisit(SQLRefreshMaterializedViewStatement x) {

    }

    default boolean visit(SQLAlterMaterializedViewStatement x) {
        return true;
    }

    default void endVisit(SQLAlterMaterializedViewStatement x) {

    }

    default boolean visit(SQLCreateTableGroupStatement x) {
        return true;
    }

    default void endVisit(SQLCreateTableGroupStatement x) {

    }

    default boolean visit(SQLDropTableGroupStatement x) {
        return true;
    }

    default void endVisit(SQLDropTableGroupStatement x) {

    }

    default boolean visit(SQLAlterTableSubpartitionAvailablePartitionNum x) {
        return true;
    }

    default void endVisit(SQLAlterTableSubpartitionAvailablePartitionNum x) {

    }

    default void endVisit(SQLShowDatabasesStatement x) {

    }

    default boolean visit(SQLShowDatabasesStatement x) {
        return true;
    }

    default void endVisit(SQLShowTableGroupsStatement x) {

    }

    default boolean visit(SQLShowTableGroupsStatement x) {
        return true;
    }

    default void endVisit(SQLShowColumnsStatement x) {

    }

    default boolean visit(SQLShowColumnsStatement x) {
        return true;
    }

    default void endVisit(SQLShowCreateTableStatement x) {

    }

    default boolean visit(SQLShowCreateTableStatement x) {
        return true;
    }

    default void endVisit(SQLShowProcessListStatement x) {

    }

    default boolean visit(SQLShowProcessListStatement x) {
        return true;
    }

    default void endVisit(SQLAlterTableSetOption x) {

    }

    default boolean visit(SQLAlterTableSetOption x) {
        return true;
    }

    default boolean visit(SQLShowCreateViewStatement x) {
        return true;
    }

    default void endVisit(SQLShowCreateViewStatement x) {

    }

    default boolean visit(SQLShowViewsStatement x) {
        return true;
    }

    default void endVisit(SQLShowViewsStatement x) {

    }

    default boolean visit(SQLAlterTableRenameIndex x) {
        return true;
    }

    default void endVisit(SQLAlterTableRenameIndex x) {

    }

    default boolean visit(SQLAlterSequenceStatement x) {
        return true;
    }

    default void endVisit(SQLAlterSequenceStatement x) {

    }

    default boolean visit(SQLAlterTableExchangePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableExchangePartition x) {

    }

    default boolean visit(SQLCreateRoleStatement x) {
        return true;
    }

    default void endVisit(SQLCreateRoleStatement x) {

    }

    default boolean visit(SQLDropRoleStatement x) {
        return true;
    }

    default void endVisit(SQLDropRoleStatement x) {

    }

    default boolean visit(SQLAlterTableReplaceColumn x) {
        return true;
    }

    default void endVisit(SQLAlterTableReplaceColumn x) {

    }

    default boolean visit(SQLMatchAgainstExpr x) {
        return true;
    }

    default void endVisit(SQLMatchAgainstExpr x) {

    }

    default boolean visit(SQLTimeExpr x) {
        return true;
    }

    default void endVisit(SQLTimeExpr x) {

    }

    default boolean visit(SQLDropCatalogStatement x) {
        return true;
    }

    default void endVisit(SQLDropCatalogStatement x) {

    }

    default void endVisit(SQLShowPartitionsStmt x) {

    }

    default boolean visit(SQLShowPartitionsStmt x) {
        return true;
    }

    default void endVisit(SQLValuesExpr x) {

    }

    default boolean visit(SQLValuesExpr x) {
        return true;
    }

    default void endVisit(SQLContainsExpr x) {

    }

    default boolean visit(SQLContainsExpr x) {
        return true;
    }

    default void endVisit(SQLDumpStatement x) {

    }

    default boolean visit(SQLDumpStatement x) {
        return true;
    }

    default void endVisit(SQLValuesTableSource x) {

    }

    default boolean visit(SQLValuesTableSource x) {
        return true;
    }

    default void endVisit(SQLExtractExpr x) {

    }

    default boolean visit(SQLExtractExpr x) {
        return true;
    }

    default void endVisit(SQLWindow x) {

    }

    default boolean visit(SQLWindow x) {
        return true;
    }

    default void endVisit(SQLJSONExpr x) {

    }

    default boolean visit(SQLJSONExpr x) {
        return true;
    }

    default void endVisit(SQLDecimalExpr x) {

    }

    default boolean visit(SQLDecimalExpr x) {
        return true;
    }

    default void endVisit(SQLAnnIndex x) {

    }

    default boolean visit(SQLAnnIndex x) {
        return true;
    }

    default void endVisit(SQLUnionDataType x) {

    }

    default boolean visit(SQLUnionDataType x) {
        return true;
    }

    default void endVisit(SQLAlterTableRecoverPartitions x) {

    }

    default boolean visit(SQLAlterTableRecoverPartitions x) {
        return true;
    }

    default void endVisit(SQLAlterIndexStatement x) {

    }

    default boolean visit(SQLAlterIndexStatement x) {
        return true;
    }


    default boolean visit(SQLAlterIndexStatement.Rebuild x) {
        return true;
    }

    default void endVisit(SQLAlterIndexStatement.Rebuild x) {

    }

    default boolean visit(SQLShowIndexesStatement x) {
        return true;
    }

    default void endVisit(SQLShowIndexesStatement x) {

    }

    default boolean visit(SQLAnalyzeTableStatement x) {
        return true;
    }

    default void endVisit(SQLAnalyzeTableStatement x) {

    }

    default boolean visit(SQLExportTableStatement x) {
        return true;
    }

    default void endVisit(SQLExportTableStatement x) {

    }

    default boolean visit(SQLImportTableStatement x) {
        return true;
    }

    default void endVisit(SQLImportTableStatement x) {

    }

    default boolean visit(SQLTableSampling x) {
        return true;
    }

    default void endVisit(SQLTableSampling x) {

    }

    default boolean visit(SQLSizeExpr x) {
        return true;
    }

    default void endVisit(SQLSizeExpr x) {

    }

    default boolean visit(SQLAlterTableArchivePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableArchivePartition x) {

    }

    default boolean visit(SQLAlterTableUnarchivePartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableUnarchivePartition x) {

    }

    default boolean visit(SQLCreateOutlineStatement x) {
        return true;
    }

    default void endVisit(SQLCreateOutlineStatement x) {

    }

    default boolean visit(SQLDropOutlineStatement x) {
        return true;
    }

    default void endVisit(SQLDropOutlineStatement x) {

    }

    default boolean visit(SQLAlterOutlineStatement x) {
        return true;
    }

    default void endVisit(SQLAlterOutlineStatement x) {

    }

    default boolean visit(SQLShowOutlinesStatement x) {
        return true;
    }

    default void endVisit(SQLShowOutlinesStatement x) {

    }

    default boolean visit(SQLPurgeTableStatement x) {
        return true;
    }

    default void endVisit(SQLPurgeTableStatement x) {

    }

    default boolean visit(SQLPurgeLogsStatement x) {
        return true;
    }

    default void endVisit(SQLPurgeLogsStatement x) {

    }

    default boolean visit(SQLPurgeRecyclebinStatement x) {
        return true;
    }

    default void endVisit(SQLPurgeRecyclebinStatement x) {

    }

    default boolean visit(SQLShowStatisticStmt x) {
        return true;
    }

    default void endVisit(SQLShowStatisticStmt x) {

    }

    default boolean visit(SQLShowStatisticListStmt x) {
        return true;
    }

    default void endVisit(SQLShowStatisticListStmt x) {

    }

    default boolean visit(SQLAlterTableAddSupplemental x) {
        return true;
    }

    default void endVisit(SQLAlterTableAddSupplemental x) {

    }

    default boolean visit(SQLShowCatalogsStatement x) {
        return true;
    }

    default void endVisit(SQLShowCatalogsStatement x) {

    }

    default boolean visit(SQLShowFunctionsStatement x) {
        return true;
    }

    default void endVisit(SQLShowFunctionsStatement x) {

    }

    default boolean visit(SQLShowSessionStatement x) {
        return true;
    }

    default void endVisit(SQLShowSessionStatement x) {

    }

    default boolean visit(SQLDbLinkExpr x) {
        return true;
    }

    default void endVisit(SQLDbLinkExpr x) {

    }

    default boolean visit(SQLCurrentTimeExpr x) {
        return true;
    }

    default void endVisit(SQLCurrentTimeExpr x) {

    }

    default boolean visit(SQLCurrentUserExpr x) {
        return true;
    }

    default void endVisit(SQLCurrentUserExpr x) {

    }

    default boolean visit(SQLShowQueryTaskStatement x) {
        return true;
    }

    default void endVisit(SQLShowQueryTaskStatement x) {

    }

    default boolean visit(SQLAdhocTableSource x) {
        return true;
    }

    default void endVisit(SQLAdhocTableSource x) {

    }

    default boolean visit(HiveCreateTableStatement x) {
        return true;
    }

    default void endVisit(HiveCreateTableStatement x) {

    }

    default boolean visit(HiveInputOutputFormat x) {
        return true;
    }

    default void endVisit(HiveInputOutputFormat x) {

    }

    default boolean visit(SQLExplainAnalyzeStatement x) {
        return true;
    }

    default void endVisit(SQLExplainAnalyzeStatement x) {

    }

    default boolean visit(SQLPartitionRef x) {
        return true;
    }

    default void endVisit(SQLPartitionRef x) {

    }

    default boolean visit(SQLPartitionRef.Item x) {
        return true;
    }

    default void endVisit(SQLPartitionRef.Item x) {

    }

    default boolean visit(SQLWhoamiStatement x) {
        return true;
    }

    default void endVisit(SQLWhoamiStatement x) {

    }

    default boolean visit(SQLDropResourceStatement x) {
        return true;
    }

    default void endVisit(SQLDropResourceStatement x) {

    }

    default boolean visit(SQLForStatement x) {
        return true;
    }

    default void endVisit(SQLForStatement x) {

    }

    default boolean visit(SQLUnnestTableSource x) {
        return true;
    }

    default void endVisit(SQLUnnestTableSource x) {

    }

    default boolean visit(SQLCopyFromStatement x) {
        return true;
    }

    default void endVisit(SQLCopyFromStatement x) {

    }

    default boolean visit(SQLShowUsersStatement x) {
        return true;
    }

    default void endVisit(SQLShowUsersStatement x) {

    }

    default boolean visit(SQLSubmitJobStatement x) {
        return true;
    }

    default void endVisit(SQLSubmitJobStatement x) {

    }

    default boolean visit(SQLTableLike x) {
        return true;
    }

    default void endVisit(SQLTableLike x) {

    }

    default boolean visit(SQLSyncMetaStatement x) {
        return true;
    }

    default void endVisit(SQLSyncMetaStatement x) {

    }

    default void endVisit(SQLValuesQuery x) {
    }

    default boolean visit(SQLValuesQuery x) {
        return true;
    }

    default void endVisit(SQLDataTypeRefExpr x) {

    }

    default boolean visit(SQLDataTypeRefExpr x) {
        return true;
    }

    default void endVisit(SQLArchiveTableStatement x) {

    }

    default boolean visit(SQLArchiveTableStatement x) {
        return true;
    }

    default void endVisit(SQLBackupStatement x) {

    }

    default boolean visit(SQLBackupStatement x) {
        return true;
    }

    default void endVisit(SQLRestoreStatement x) {

    }

    default boolean visit(SQLRestoreStatement x) {
        return true;
    }

    default void endVisit(SQLBuildTableStatement x) {

    }

    default boolean visit(SQLBuildTableStatement x) {
        return true;
    }

    default void endVisit(SQLCancelJobStatement x) {

    }

    default boolean visit(SQLCancelJobStatement x) {
        return true;
    }

    default void endVisit(SQLExportDatabaseStatement x) {

    }

    default boolean visit(SQLExportDatabaseStatement x) {
        return true;
    }

    default void endVisit(SQLImportDatabaseStatement x) {

    }

    default boolean visit(SQLImportDatabaseStatement x) {
        return true;
    }

    default void endVisit(SQLRenameUserStatement x) {

    }

    default boolean visit(SQLRenameUserStatement x) {
        return true;
    }

    default void endVisit(SQLPartitionByValue x) {

    }

    default boolean visit(SQLPartitionByValue x) {
        return true;
    }

    default void endVisit(SQLAlterTablePartitionCount x) {

    }

    default boolean visit(SQLAlterTablePartitionCount x) {
        return true;
    }

    default void endVisit(SQLAlterTableBlockSize x) {

    }

    default boolean visit(SQLAlterTableBlockSize x) {
        return true;
    }

    default void endVisit(SQLAlterTableCompression x) {

    }

    default boolean visit(SQLAlterTableCompression x) {
        return true;
    }

    default void endVisit(SQLAlterTablePartitionLifecycle x) {

    }

    default boolean visit(SQLAlterTablePartitionLifecycle x) {
        return true;
    }

    default void endVisit(SQLAlterTableSubpartitionLifecycle x) {

    }

    default boolean visit(SQLAlterTableSubpartitionLifecycle x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropSubpartition x) {

    }

    default boolean visit(SQLAlterTableDropSubpartition x) {
        return true;
    }

    default void endVisit(SQLAlterTableDropClusteringKey x) {

    }

    default boolean visit(SQLAlterTableDropClusteringKey x) {
        return true;
    }

    default void endVisit(SQLAlterTableAddClusteringKey x) {

    }

    default boolean visit(SQLAlterTableAddClusteringKey x) {
        return true;
    }

    default void endVisit(MySqlKillStatement x) {

    }

    default boolean visit(MySqlKillStatement x) {
        return true;
    }


    default boolean visit(SQLCreateResourceGroupStatement x) {
        return true;
    }

    default void endVisit(SQLCreateResourceGroupStatement x) {

    }

    default boolean visit(SQLAlterResourceGroupStatement x) {
        return true;
    }

    default void endVisit(SQLAlterResourceGroupStatement x) {

    }

    default void endVisit(SQLDropResourceGroupStatement x) {

    }

    default boolean visit(SQLDropResourceGroupStatement x) {
        return true;
    }

    default void endVisit(SQLListResourceGroupStatement x) {

    }

    default boolean visit(SQLListResourceGroupStatement x) {
        return true;
    }

    default void endVisit(SQLAlterTableMergePartition x) {

    }

    default boolean visit(SQLAlterTableMergePartition x) {
        return true;
    }

    default void endVisit(SQLPartitionSpec x) {

    }

    default boolean visit(SQLPartitionSpec x) {
        return true;
    }

    default void endVisit(SQLPartitionSpec.Item x) {

    }

    default boolean visit(SQLPartitionSpec.Item x) {
        return true;
    }

}
