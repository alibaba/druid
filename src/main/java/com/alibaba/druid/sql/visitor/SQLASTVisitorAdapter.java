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
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeUpdateClause;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInputOutputFormat;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.SQLAlterResourceGroupStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.SQLCreateResourceGroupStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.SQLListResourceGroupStatement;

public class SQLASTVisitorAdapter implements SQLASTVisitor {
    protected DbType dbType;
    protected int features;

    public void endVisit(SQLAllColumnExpr x) {
    }

    public void endVisit(SQLBetweenExpr x) {
    }

    public void endVisit(SQLBinaryOpExpr x) {
    }

    public void endVisit(SQLCaseExpr x) {
    }

    public void endVisit(SQLCaseExpr.Item x) {
    }

    public void endVisit(SQLCaseStatement x) {
    }

    public void endVisit(SQLCaseStatement.Item x) {
    }

    public void endVisit(SQLCharExpr x) {
    }

    public void endVisit(SQLIdentifierExpr x) {
    }

    public void endVisit(SQLInListExpr x) {
    }

    public void endVisit(SQLIntegerExpr x) {
    }

    public void endVisit(SQLSmallIntExpr x) {
    }

    public void endVisit(SQLTinyIntExpr x) {
    }

    public void endVisit(SQLBigIntExpr x) {
    }

    public void endVisit(SQLExistsExpr x) {
    }

    public void endVisit(SQLNCharExpr x) {
    }

    public void endVisit(SQLNotExpr x) {
    }

    public void endVisit(SQLNullExpr x) {
    }

    public void endVisit(SQLNumberExpr x) {
    }

    public void endVisit(SQLRealExpr x) {
    }

    public void endVisit(SQLPropertyExpr x) {
    }

    public void endVisit(SQLSelectGroupByClause x) {
    }

    public void endVisit(SQLSelectItem x) {
    }

    public void endVisit(SQLSelectStatement selectStatement) {
    }

    public void postVisit(SQLObject astNode) {
    }

    public void preVisit(SQLObject astNode) {
    }

    public boolean visit(SQLAllColumnExpr x) {
        return true;
    }

    public boolean visit(SQLBetweenExpr x) {
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        return true;
    }

    public boolean visit(SQLCaseExpr x) {
        return true;
    }

    public boolean visit(SQLCaseExpr.Item x) {
        return true;
    }

    public boolean visit(SQLCaseStatement x) {
        return true;
    }

    public boolean visit(SQLCaseStatement.Item x) {
        return true;
    }

    public boolean visit(SQLCastExpr x) {
        return true;
    }

    public boolean visit(SQLCharExpr x) {
        return true;
    }

    public boolean visit(SQLExistsExpr x) {
        return true;
    }

    public boolean visit(SQLIdentifierExpr x) {
        return true;
    }

    public boolean visit(SQLInListExpr x) {
        return true;
    }

    public boolean visit(SQLIntegerExpr x) {
        return true;
    }

    public boolean visit(SQLSmallIntExpr x) {
        return true;
    }
    public boolean visit(SQLTinyIntExpr x) {
        return true;
    }
    public boolean visit(SQLBigIntExpr x) {
        return true;
    }

    public boolean visit(SQLNCharExpr x) {
        return true;
    }

    public boolean visit(SQLNotExpr x) {
        return true;
    }

    public boolean visit(SQLNullExpr x) {
        return true;
    }

    public boolean visit(SQLNumberExpr x) {
        return true;
    }

    public boolean visit(SQLRealExpr x) {
        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        return true;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        return true;
    }

    public boolean visit(SQLSelectItem x) {
        return true;
    }

    public void endVisit(SQLCastExpr x) {
    }

    public boolean visit(SQLSelectStatement x) {
        return true;
    }

    public void endVisit(SQLAggregateExpr x) {
    }

    public boolean visit(SQLAggregateExpr x) {
        return true;
    }

    public boolean visit(SQLVariantRefExpr x) {
        return true;
    }

    public void endVisit(SQLVariantRefExpr x) {
    }

    public boolean visit(SQLQueryExpr x) {
        return true;
    }

    public void endVisit(SQLQueryExpr x) {
    }

    public boolean visit(SQLSelect x) {
        return true;
    }

    public void endVisit(SQLSelect select) {
    }

    public boolean visit(SQLSelectQueryBlock x) {
        return true;
    }

    public void endVisit(SQLSelectQueryBlock x) {
    }

    public boolean visit(SQLExprTableSource x) {
        return true;
    }

    public void endVisit(SQLExprTableSource x) {
    }

    public boolean visit(SQLOrderBy x) {
        return true;
    }

    public void endVisit(SQLOrderBy x) {
    }

    public boolean visit(SQLSelectOrderByItem x) {
        return true;
    }

    public void endVisit(SQLSelectOrderByItem x) {
    }

    public boolean visit(SQLDropTableStatement x) {
        return true;
    }

    public void endVisit(SQLDropTableStatement x) {
    }

    public boolean visit(SQLCreateTableStatement x) {
        return true;
    }

    public void endVisit(SQLCreateTableStatement x) {
    }

    public boolean visit(SQLColumnDefinition x) {
        return true;
    }

    public void endVisit(SQLColumnDefinition x) {
    }

    public boolean visit(SQLColumnDefinition.Identity x) {
        return true;
    }

    public void endVisit(SQLColumnDefinition.Identity x) {
    }

    public boolean visit(SQLDataType x) {
        return true;
    }

    public void endVisit(SQLDataType x) {
    }

    public boolean visit(SQLDeleteStatement x) {
        return true;
    }

    public void endVisit(SQLDeleteStatement x) {
    }

    public boolean visit(SQLCurrentOfCursorExpr x) {
        return true;
    }

    public void endVisit(SQLCurrentOfCursorExpr x) {
    }

    public boolean visit(SQLInsertStatement x) {
        return true;
    }

    public void endVisit(SQLInsertStatement x) {
    }

    public boolean visit(SQLUpdateSetItem x) {
        return true;
    }

    public void endVisit(SQLUpdateSetItem x) {
    }

    public boolean visit(SQLUpdateStatement x) {
        return true;
    }

    public void endVisit(SQLUpdateStatement x) {
    }

    public boolean visit(SQLCreateViewStatement x) {
        return true;
    }

    public void endVisit(SQLCreateViewStatement x) {
    }

    public boolean visit(SQLAlterViewStatement x) {
        return true;
    }

    public void endVisit(SQLAlterViewStatement x) {
    }

    public boolean visit(SQLAlterTableGroupStatement x) {
        return true;
    }

    public void endVisit(SQLAlterTableGroupStatement x) {
    }

    public boolean visit(SQLAlterSystemGetConfigStatement x) {
        return true;
    }

    public void endVisit(SQLAlterSystemGetConfigStatement x) {
    }

    public boolean visit(SQLAlterSystemSetConfigStatement x) {
        return true;
    }

    public void endVisit(SQLAlterSystemSetConfigStatement x) {
    }

    public boolean visit(SQLCreateViewStatement.Column x) {
        return true;
    }

    public void endVisit(SQLCreateViewStatement.Column x) {
    }

    public boolean visit(SQLNotNullConstraint x) {
        return true;
    }

    public void endVisit(SQLNotNullConstraint x) {
    }

    @Override
    public void endVisit(SQLMethodInvokeExpr x) {

    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnionQuery x) {

    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        return true;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnaryExpr x) {

    }

    @Override
    public boolean visit(SQLHexExpr x) {
        return false;
    }

    @Override
    public void endVisit(SQLHexExpr x) {

    }

    @Override
    public void endVisit(SQLSetStatement x) {

    }

    @Override
    public boolean visit(SQLSetStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAssignItem x) {

    }

    @Override
    public boolean visit(SQLAssignItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLCallStatement x) {

    }

    @Override
    public boolean visit(SQLCallStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLJoinTableSource x) {

    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLJoinTableSource.UDJ x) {

    }

    @Override
    public boolean visit(SQLJoinTableSource.UDJ x) {
        return true;
    }

    @Override
    public boolean visit(ValuesClause x) {
        return true;
    }

    @Override
    public void endVisit(ValuesClause x) {

    }

    @Override
    public void endVisit(SQLSomeExpr x) {

    }

    @Override
    public boolean visit(SQLSomeExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAnyExpr x) {

    }

    @Override
    public boolean visit(SQLAnyExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAllExpr x) {

    }

    @Override
    public boolean visit(SQLAllExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLInSubQueryExpr x) {

    }

    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLListExpr x) {

    }

    @Override
    public boolean visit(SQLListExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubqueryTableSource x) {

    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLTruncateStatement x) {

    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDefaultExpr x) {

    }

    @Override
    public boolean visit(SQLDefaultExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommentStatement x) {

    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLUseStatement x) {

    }

    @Override
    public boolean visit(SQLUseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddColumn x) {

    }

    @Override
    public boolean visit(SQLAlterTableDeleteByCondition x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableDeleteByCondition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropColumnItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropColumnItem x) {

    }

    @Override
    public boolean visit(SQLDropIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropIndexStatement x) {

    }

    @Override
    public boolean visit(SQLDropViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropViewStatement x) {

    }

    @Override
    public boolean visit(SQLSavePointStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLSavePointStatement x) {

    }

    @Override
    public boolean visit(SQLRollbackStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLRollbackStatement x) {

    }

    @Override
    public boolean visit(SQLReleaseSavePointStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReleaseSavePointStatement x) {
    }

    @Override
    public boolean visit(SQLCommentHint x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommentHint x) {

    }

    @Override
    public void endVisit(SQLCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLCreateDatabaseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableDropIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropIndex x) {

    }

    @Override
    public void endVisit(SQLOver x) {
    }

    @Override
    public boolean visit(SQLOver x) {
        return true;
    }
    
    @Override
    public void endVisit(SQLKeep x) {
    }
    
    @Override
    public boolean visit(SQLKeep x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnPrimaryKey x) {

    }

    @Override
    public boolean visit(SQLColumnPrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnUniqueKey x) {

    }

    @Override
    public boolean visit(SQLColumnUniqueKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLWithSubqueryClause x) {
    }

    @Override
    public boolean visit(SQLWithSubqueryClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLWithSubqueryClause.Entry x) {
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        return true;
    }

    @Override
    public boolean visit(SQLCharacterDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLCharacterDataType x) {

    }

    @Override
    public void endVisit(SQLAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(SQLAlterTableAlterColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableModifyClusteredBy x) {

    }

    @Override
    public boolean visit(SQLAlterTableModifyClusteredBy x) {
        return true;
    }

    @Override
    public boolean visit(SQLCheck x) {
        return true;
    }

    @Override
    public void endVisit(SQLCheck x) {

    }

    @Override
    public boolean visit(SQLDefault x) {
        return false;
    }

    @Override
    public void endVisit(SQLDefault x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropForeignKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropForeignKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropPrimaryKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableKeys x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableKeys x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableKeys x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableKeys x) {

    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableConstraint x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableConstraint x) {

    }

    @Override
    public boolean visit(SQLColumnCheck x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnCheck x) {

    }

    @Override
    public boolean visit(SQLExprHint x) {
        return true;
    }

    @Override
    public void endVisit(SQLExprHint x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropConstraint x) {

    }

    @Override
    public boolean visit(SQLUnique x) {
        for (SQLSelectOrderByItem column : x.getColumns()) {
            column.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(SQLUnique x) {

    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateIndexStatement x) {

    }

    @Override
    public boolean visit(SQLPrimaryKeyImpl x) {
        return true;
    }

    @Override
    public void endVisit(SQLPrimaryKeyImpl x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenameColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenameColumn x) {

    }

    @Override
    public boolean visit(SQLColumnReference x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnReference x) {

    }

    @Override
    public boolean visit(SQLForeignKeyImpl x) {
        return true;
    }

    @Override
    public void endVisit(SQLForeignKeyImpl x) {

    }

    @Override
    public boolean visit(SQLDropSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTriggerStatement x) {

    }

    @Override
    public void endVisit(SQLDropUserStatement x) {

    }

    @Override
    public boolean visit(SQLDropUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLExplainStatement x) {

    }

    @Override
    public boolean visit(SQLExplainStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLGrantStatement x) {

    }

    @Override
    public boolean visit(SQLGrantStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLIndexOptions x) {

    }

    @Override
    public boolean visit(SQLIndexOptions x) {
        return false;
    }

    @Override
    public void endVisit(SQLIndexDefinition x) {

    }

    @Override
    public boolean visit(SQLIndexDefinition x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableAddIndex x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddConstraint x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(SQLCreateTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLDropFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTableSpaceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTableSpaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLDropProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBooleanExpr x) {

    }

    @Override
    public boolean visit(SQLBooleanExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnionQueryTableSource x) {

    }

    @Override
    public boolean visit(SQLUnionQueryTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLTimestampExpr x) {

    }

    @Override
    public boolean visit(SQLTimestampExpr x) {
        return true;
    }


    @Override
    public boolean visit(SQLTimeExpr x) {
        return true;
    }


    @Override
    public void endVisit(SQLTimeExpr x) {

    }


    @Override
    public boolean visit(SQLDateTimeExpr x) {
        return true;
    }


    @Override
    public void endVisit(SQLDateTimeExpr x) {

    }


    @Override
    public boolean visit(SQLDoubleExpr x) {
        return true;
    }


    @Override
    public void endVisit(SQLDoubleExpr x) {

    }

    @Override
    public boolean visit(SQLFloatExpr x) {
        return true;
    }


    @Override
    public void endVisit(SQLFloatExpr x) {

    }

    @Override
    public boolean visit(SQLDropCatalogStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropCatalogStatement x) {

    }

    @Override
    public void endVisit(SQLRevokeStatement x) {

    }

    @Override
    public boolean visit(SQLRevokeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBinaryExpr x) {

    }

    @Override
    public boolean visit(SQLBinaryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRename x) {

    }

    @Override
    public boolean visit(SQLAlterTableRename x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterViewRenameStatement x) {

    }

    @Override
    public boolean visit(SQLAlterViewRenameStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowTablesStatement x) {

    }

    @Override
    public boolean visit(SQLShowTablesStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddExtPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddExtPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropExtPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropExtPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenamePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenamePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSetComment x) {

    }

    @Override
    public boolean visit(SQLAlterTableSetComment x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSetLifecycle x) {

    }

    @Override public boolean visit(SQLPrivilegeItem x) {
        return false;
    }

    @Override public void endVisit(SQLPrivilegeItem x) {

    }

    @Override
    public boolean visit(SQLAlterTableSetLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableLifecycle x) {
        return true;
    }


    @Override
    public void endVisit(SQLAlterTablePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTablePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTablePartitionSetProperties x) {

    }

    @Override
    public boolean visit(SQLAlterTablePartitionSetProperties x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableTouch x) {

    }

    @Override
    public boolean visit(SQLAlterTableTouch x) {
        return true;
    }

    @Override
    public void endVisit(SQLArrayExpr x) {

    }

    @Override
    public boolean visit(SQLArrayExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLOpenStatement x) {

    }

    @Override
    public boolean visit(SQLOpenStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLFetchStatement x) {

    }

    @Override
    public boolean visit(SQLFetchStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCloseStatement x) {

    }

    @Override
    public boolean visit(SQLCloseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLGroupingSetExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLGroupingSetExpr x) {

    }

    @Override
    public boolean visit(SQLIfStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement x) {

    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement.Else x) {

    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement.ElseIf x) {

    }

    @Override
    public boolean visit(SQLLoopStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLLoopStatement x) {

    }

    @Override
    public boolean visit(SQLParameter x) {
        return true;
    }

    @Override
    public void endVisit(SQLParameter x) {

    }

    @Override
    public boolean visit(SQLCreateProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBlockStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropKey x) {

    }

    @Override
    public boolean visit(SQLDeclareItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLDeclareItem x) {
    }

    @Override
    public boolean visit(SQLPartitionValue x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionValue x) {

    }

    @Override
    public boolean visit(SQLPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartition x) {

    }

    @Override
    public boolean visit(SQLPartitionByRange x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionByRange x) {

    }

    @Override
    public boolean visit(SQLPartitionByHash x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionByHash x) {

    }

    @Override
    public boolean visit(SQLPartitionByList x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionByList x) {

    }

    @Override
    public boolean visit(SQLSubPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartition x) {

    }

    @Override
    public boolean visit(SQLSubPartitionByHash x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartitionByHash x) {

    }

    @Override
    public boolean visit(SQLSubPartitionByRange x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartitionByRange x) {

    }

    @Override
    public boolean visit(SQLSubPartitionByList x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartitionByList x) {

    }

    @Override
    public boolean visit(SQLAlterDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableConvertCharSet x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableConvertCharSet x) {

    }

    @Override
    public boolean visit(SQLAlterTableReOrganizePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableReOrganizePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableCoalescePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableCoalescePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableTruncatePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableTruncatePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDiscardPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDiscardPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableImportPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableImportPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableAnalyzePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAnalyzePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableCheckPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableCheckPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableOptimizePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableOptimizePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRebuildPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRebuildPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRepairPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRepairPartition x) {

    }
    
    @Override
    public boolean visit(SQLSequenceExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(SQLSequenceExpr x) {
        
    }

    @Override
    public boolean visit(SQLMergeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLMergeStatement x) {
        
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
    public boolean visit(SQLErrorLoggingClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLErrorLoggingClause x) {

    }

    @Override
    public boolean visit(SQLNullConstraint x) {
	return true;
    }

    @Override
    public void endVisit(SQLNullConstraint x) {
    }

    @Override
    public boolean visit(SQLCreateSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateSequenceStatement x) {
    }

    @Override
    public boolean visit(SQLDateExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLDateExpr x) {

    }

    @Override
    public boolean visit(SQLLimit x) {
        return true;
    }

    @Override
    public void endVisit(SQLLimit x) {

    }

    @Override
    public void endVisit(SQLStartTransactionStatement x) {

    }

    @Override
    public boolean visit(SQLStartTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDescribeStatement x) {

    }

    @Override
    public boolean visit(SQLDescribeStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLWhileStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLWhileStatement x) {

    }


    @Override
    public boolean visit(SQLDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDeclareStatement x) {

    }

    @Override
    public boolean visit(SQLReturnStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReturnStatement x) {

    }

    @Override
    public boolean visit(SQLArgument x) {
        return true;
    }

    @Override
    public void endVisit(SQLArgument x) {

    }

    @Override
    public boolean visit(SQLCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommitStatement x) {

    }

    @Override
    public boolean visit(SQLFlashbackExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLFlashbackExpr x) {

    }

    @Override
    public boolean visit(SQLCreateMaterializedViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateMaterializedViewStatement x) {

    }

    @Override
    public boolean visit(SQLShowCreateMaterializedViewStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLShowCreateMaterializedViewStatement x) {

    }

    @Override
    public boolean visit(SQLBinaryOpExprGroup x) {
        return true;
    }

    @Override
    public void endVisit(SQLBinaryOpExprGroup x) {

    }

    public void config(VisitorFeature feature, boolean state) {
        features = VisitorFeature.config(features, feature, state);
    }

    @Override
    public boolean visit(SQLScriptCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLScriptCommitStatement x) {

    }

    @Override
    public boolean visit(SQLReplaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReplaceStatement x) {

    }

    @Override
    public boolean visit(SQLCreateUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateUserStatement x) {

    }

    @Override
    public boolean visit(SQLAlterFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTypeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTypeStatement x) {

    }

    @Override
    public boolean visit(SQLIntervalExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLIntervalExpr x) {

    }

    @Override
    public boolean visit(SQLLateralViewTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLLateralViewTableSource x) {

    }

    @Override
    public boolean visit(SQLShowErrorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowErrorsStatement x) {

    }

    @Override
    public boolean visit(SQLShowGrantsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowGrantsStatement x) {

    }

    @Override
    public boolean visit(SQLShowPackagesStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowPackagesStatement x) {

    }

    @Override
    public boolean visit(SQLShowRecylebinStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowRecylebinStatement x) {

    }

    @Override
    public boolean visit(SQLAlterCharacter x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterCharacter x) {

    }

    @Override
    public boolean visit(SQLExprStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLExprStatement x) {

    }

    @Override
    public boolean visit(SQLAlterProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLDropEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropEventStatement x) {

    }

    @Override
    public boolean visit(SQLDropLogFileGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(SQLDropServerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropServerStatement x) {

    }

    @Override
    public boolean visit(SQLDropSynonymStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropSynonymStatement x) {

    }

    @Override
    public boolean visit(SQLDropTypeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTypeStatement x) {

    }

    @Override
    public boolean visit(SQLRecordDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLRecordDataType x) {

    }

    public boolean visit(SQLExternalRecordFormat x) {
        return true;
    }

    public void endVisit(SQLExternalRecordFormat x) {

    }

    @Override
    public boolean visit(SQLArrayDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLArrayDataType x) {

    }

    @Override
    public boolean visit(SQLMapDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLMapDataType x) {

    }

    @Override
    public boolean visit(SQLStructDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLStructDataType x) {

    }

    @Override
    public boolean visit(SQLRowDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLRowDataType x) {

    }

    @Override
    public boolean visit(SQLStructDataType.Field x) {
        return true;
    }

    @Override
    public void endVisit(SQLStructDataType.Field x) {

    }

    @Override
    public boolean visit(SQLShowMaterializedViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowMaterializedViewStatement x) {
    }

    @Override
    public boolean visit(SQLRefreshMaterializedViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLRefreshMaterializedViewStatement x) {

    }

    @Override
    public boolean visit(SQLAlterMaterializedViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterMaterializedViewStatement x) {

    }

    @Override
    public boolean visit(SQLDropMaterializedViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropMaterializedViewStatement x) {

    }

    @Override
    public boolean visit(SQLCreateTableGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateTableGroupStatement x) {

    }


    @Override
    public boolean visit(SQLDropTableGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTableGroupStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableSubpartitionAvailablePartitionNum x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSubpartitionAvailablePartitionNum x) {

    }

    @Override
    public void endVisit(SQLShowDatabasesStatement x) {

    }

    @Override
    public boolean visit(SQLShowDatabasesStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowTableGroupsStatement x) {

    }

    @Override
    public boolean visit(SQLShowTableGroupsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowColumnsStatement x) {

    }

    @Override
    public boolean visit(SQLShowColumnsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowCreateTableStatement x) {

    }

    @Override
    public boolean visit(SQLShowCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowProcessListStatement x) {

    }

    @Override
    public boolean visit(SQLShowProcessListStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSetOption x) {

    }

    @Override
    public boolean visit(SQLAlterTableSetOption x) {
        return true;
    }

    @Override
    public boolean visit(SQLShowCreateViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowCreateViewStatement x) {

    }

    @Override
    public boolean visit(SQLShowViewsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowViewsStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenameIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenameIndex x) {

    }

    @Override
    public boolean visit(SQLAlterSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableExchangePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableExchangePartition x) {

    }

    @Override
    public boolean visit(SQLCreateRoleStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateRoleStatement x) {

    }

    @Override
    public boolean visit(SQLDropRoleStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropRoleStatement x) {

    }

    @Override
    public boolean visit(SQLMatchAgainstExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLMatchAgainstExpr x) {

    }

    @Override
    public boolean visit(SQLAlterTableReplaceColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableReplaceColumn x) {

    }

    @Override
    public boolean visit(SQLShowPartitionsStmt x) {
        return true;
    }

    public void endVisit(SQLShowPartitionsStmt x) {

    }

    @Override
    public boolean visit(SQLValuesExpr x) {
        return true;
    }

    public void endVisit(SQLValuesExpr x) {

    }

    @Override
    public boolean visit(SQLContainsExpr x) {
        return true;
    }

    public void endVisit(SQLContainsExpr x) {

    }

    @Override
    public boolean visit(SQLDumpStatement x) {
        return true;
    }

    public void endVisit(SQLDumpStatement x) {

    }

    @Override
    public boolean visit(SQLValuesTableSource x) {
        return true;
    }

    public void endVisit(SQLValuesTableSource x) {

    }

    @Override
    public boolean visit(SQLExtractExpr x) {
        return true;
    }

    public void endVisit(SQLExtractExpr x) {

    }

    @Override
    public boolean visit(SQLWindow x) {
        return true;
    }

    public void endVisit(SQLWindow x) {

    }

    @Override
    public boolean visit(SQLJSONExpr x) {
        return true;
    }

    public void endVisit(SQLJSONExpr x) {

    }

    @Override
    public void endVisit(SQLDecimalExpr x) {

    }

    @Override
    public boolean visit(SQLDecimalExpr x) {
        return false;
    }

    @Override
    public void endVisit(SQLAnnIndex x) {

    }

    @Override
    public boolean visit(SQLAnnIndex x) {
        return false;
    }

    @Override
    public void endVisit(SQLUnionDataType x) {

    }

    @Override
    public boolean visit(SQLUnionDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRecoverPartitions x) {

    }

    @Override
    public boolean visit(SQLAlterTableRecoverPartitions x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowIndexesStatement x) {

    }

    @Override
    public boolean visit(SQLShowIndexesStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAnalyzeTableStatement x) {

    }

    @Override
    public boolean visit(SQLAnalyzeTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLExportTableStatement x) {

    }

    @Override
    public boolean visit(SQLExportTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLImportTableStatement x) {

    }

    @Override
    public boolean visit(SQLImportTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterIndexStatement x) {

    }

    @Override
    public boolean visit(SQLAlterIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterIndexStatement.Rebuild x) {

    }

    @Override
    public boolean visit(SQLAlterIndexStatement.Rebuild x) {
        return true;
    }

    @Override
    public void endVisit(SQLTableSampling x) {

    }

    @Override
    public boolean visit(SQLTableSampling x) {
        return true;
    }

    @Override
    public void endVisit(SQLSizeExpr x) {

    }

    @Override
    public boolean visit(SQLSizeExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableArchivePartition x) {
    }

    @Override
    public boolean visit(SQLAlterTableArchivePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableUnarchivePartition x) {
    }

    @Override
    public boolean visit(SQLCreateOutlineStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateOutlineStatement x) {
    }

    @Override
    public boolean visit(SQLDropOutlineStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropOutlineStatement x) {
    }

    @Override
    public boolean visit(SQLAlterOutlineStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterOutlineStatement x) {
    }

    @Override
    public boolean visit(SQLShowOutlinesStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowOutlinesStatement x) {
    }

    @Override
    public boolean visit(SQLShowQueryTaskStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowQueryTaskStatement x) {
    }

    @Override
    public boolean visit(SQLPurgeTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLPurgeTableStatement x) {
    }

    @Override
    public boolean visit(SQLPurgeLogsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLPurgeLogsStatement x) {
    }

    @Override
    public boolean visit(SQLPurgeRecyclebinStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLPurgeRecyclebinStatement x) {
    }

    @Override
    public boolean visit(SQLShowStatisticStmt x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowStatisticStmt x) {
    }

    @Override
    public boolean visit(SQLShowStatisticListStmt x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowStatisticListStmt x) {
    }

    @Override
    public boolean visit(SQLAlterTableAddSupplemental x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddSupplemental x) {
    }

    @Override
    public boolean visit(SQLShowCatalogsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowCatalogsStatement x) {
    }

    @Override
    public boolean visit(SQLShowFunctionsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowFunctionsStatement x) {
    }

    @Override
    public boolean visit(SQLShowSessionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowSessionStatement x) {
    }

    @Override
    public boolean visit(SQLDbLinkExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLDbLinkExpr x) {
    }

    @Override
    public boolean visit(SQLCurrentTimeExpr x) {
        return false;
    }

    @Override
    public void endVisit(SQLCurrentTimeExpr x) {

    }

    @Override
    public boolean visit(SQLCurrentUserExpr x) {
        return false;
    }

    @Override
    public void endVisit(SQLCurrentUserExpr x) {

    }

    @Override
    public boolean visit(SQLAdhocTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLAdhocTableSource x) {

    }

    @Override
    public boolean visit(SQLExplainAnalyzeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLExplainAnalyzeStatement x) {

    }

    @Override
    public boolean visit(SQLPartitionRef x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionRef x) {

    }

    @Override
    public boolean visit(SQLPartitionRef.Item x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionRef.Item x) {

    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        return visit((SQLCreateTableStatement) x);
    }

    @Override
    public void endVisit(HiveCreateTableStatement x) {

    }

    @Override
    public boolean visit(HiveInputOutputFormat x) {
        return true;
    }

    @Override
    public void endVisit(HiveInputOutputFormat x) {

    }

    @Override
    public boolean visit(SQLWhoamiStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLWhoamiStatement x) {

    }

    @Override
    public boolean visit(SQLDropResourceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropResourceStatement x) {

    }

    @Override
    public boolean visit(SQLForStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLForStatement x) {

    }

    @Override
    public boolean visit(SQLUnnestTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnnestTableSource x) {

    }

    @Override
    public boolean visit(SQLCopyFromStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCopyFromStatement x) {

    }

    @Override
    public boolean visit(SQLShowUsersStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowUsersStatement x) {

    }

    @Override
    public boolean visit(SQLSubmitJobStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLSubmitJobStatement x) {

    }

    @Override
    public boolean visit(SQLTableLike x) {
        return true;
    }

    @Override
    public void endVisit(SQLTableLike x) {

    }

    @Override
    public boolean visit(SQLSyncMetaStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLSyncMetaStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableUnarchivePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLValuesQuery x) {

    }

    @Override
    public boolean visit(SQLValuesQuery x) {
        return true;
    }

    @Override
    public void endVisit(SQLDataTypeRefExpr x) {

    }

    @Override
    public boolean visit(SQLDataTypeRefExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLArchiveTableStatement x) {

    }

    @Override
    public boolean visit(SQLArchiveTableStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLBackupStatement x) {

    }

    @Override
    public boolean visit(SQLBackupStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLRestoreStatement x) {

    }

    @Override
    public boolean visit(SQLRestoreStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLBuildTableStatement x) {

    }

    @Override
    public boolean visit(SQLBuildTableStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLCancelJobStatement x) {

    }

    @Override
    public boolean visit(SQLCancelJobStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLExportDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLExportDatabaseStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLImportDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLImportDatabaseStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLRenameUserStatement x) {

    }

    @Override
    public boolean visit(SQLRenameUserStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLPartitionByValue x) {

    }

    @Override
    public boolean visit(SQLPartitionByValue x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTablePartitionCount x) {

    }

    @Override
    public boolean visit(SQLAlterTablePartitionCount x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableBlockSize x) {

    }

    @Override
    public boolean visit(SQLAlterTableBlockSize x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableCompression x) {

    }

    @Override
    public boolean visit(SQLAlterTableCompression x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTablePartitionLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTablePartitionLifecycle x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableSubpartitionLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableSubpartitionLifecycle x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableDropSubpartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropSubpartition x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableDropClusteringKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropClusteringKey x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableAddClusteringKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddClusteringKey x) {
        return false;
    }


    @Override
    public void endVisit(MySqlKillStatement x) {
    }

    @Override
    public boolean visit(MySqlKillStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropResourceGroupStatement x) {
    }


    @Override
    public boolean visit(SQLCreateResourceGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateResourceGroupStatement x) {

    }

    @Override
    public boolean visit(SQLAlterResourceGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterResourceGroupStatement x) {

    }

    @Override
    public boolean visit(SQLDropResourceGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLListResourceGroupStatement x) {

    }

    @Override
    public boolean visit(SQLListResourceGroupStatement x) {
        return false;
    }

    public final boolean isEnabled(VisitorFeature feature) {
        return VisitorFeature.isEnabled(this.features, feature);
    }

    public int getFeatures() {
        return features;
    }

    public void setFeatures(int features) {
        this.features = features;
    }
}
