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
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddPartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropForeignKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRename;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRenameColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRenamePartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableSetComment;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableSetLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableTouch;
import com.alibaba.druid.sql.ast.statement.SQLAlterViewRenameStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLColumnReference;
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropProcedureStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropSequenceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableSpaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropUserStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExplainStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLRevokeStatement;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowTablesStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;

public class SQLASTVisitorAdapter implements SQLASTVisitor {

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

    public void endVisit(SQLCharExpr x) {
    }

    public void endVisit(SQLIdentifierExpr x) {
    }

    public void endVisit(SQLInListExpr x) {
    }

    public void endVisit(SQLIntegerExpr x) {
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

    public boolean visit(SQLSelectStatement astNode) {
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
    
    public boolean visit(SQLCreateViewStatement.Column x) {
        return true;
    }
    
    public void endVisit(SQLCreateViewStatement.Column x) {
    }

    public boolean visit(NotNullConstraint x) {
        return true;
    }

    public void endVisit(NotNullConstraint x) {
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
        return true;
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
    public boolean visit(SQLCheck x) {
        return true;
    }

    @Override
    public void endVisit(SQLCheck x) {

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
        for (SQLExpr column : x.getColumns()) {
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
    
}
