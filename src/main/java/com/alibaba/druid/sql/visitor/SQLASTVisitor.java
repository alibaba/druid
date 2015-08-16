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

public interface SQLASTVisitor {

    void endVisit(SQLAllColumnExpr x);

    void endVisit(SQLBetweenExpr x);

    void endVisit(SQLBinaryOpExpr x);

    void endVisit(SQLCaseExpr x);

    void endVisit(SQLCaseExpr.Item x);

    void endVisit(SQLCharExpr x);

    void endVisit(SQLIdentifierExpr x);

    void endVisit(SQLInListExpr x);

    void endVisit(SQLIntegerExpr x);

    void endVisit(SQLExistsExpr x);

    void endVisit(SQLNCharExpr x);

    void endVisit(SQLNotExpr x);

    void endVisit(SQLNullExpr x);

    void endVisit(SQLNumberExpr x);

    void endVisit(SQLPropertyExpr x);

    void endVisit(SQLSelectGroupByClause x);

    void endVisit(SQLSelectItem x);

    void endVisit(SQLSelectStatement selectStatement);

    void postVisit(SQLObject astNode);

    void preVisit(SQLObject astNode);

    boolean visit(SQLAllColumnExpr x);

    boolean visit(SQLBetweenExpr x);

    boolean visit(SQLBinaryOpExpr x);

    boolean visit(SQLCaseExpr x);

    boolean visit(SQLCaseExpr.Item x);

    boolean visit(SQLCastExpr x);

    boolean visit(SQLCharExpr x);

    boolean visit(SQLExistsExpr x);

    boolean visit(SQLIdentifierExpr x);

    boolean visit(SQLInListExpr x);

    boolean visit(SQLIntegerExpr x);

    boolean visit(SQLNCharExpr x);

    boolean visit(SQLNotExpr x);

    boolean visit(SQLNullExpr x);

    boolean visit(SQLNumberExpr x);

    boolean visit(SQLPropertyExpr x);

    boolean visit(SQLSelectGroupByClause x);

    boolean visit(SQLSelectItem x);

    void endVisit(SQLCastExpr x);

    boolean visit(SQLSelectStatement astNode);

    void endVisit(SQLAggregateExpr astNode);

    boolean visit(SQLAggregateExpr astNode);

    boolean visit(SQLVariantRefExpr x);

    void endVisit(SQLVariantRefExpr x);

    boolean visit(SQLQueryExpr x);

    void endVisit(SQLQueryExpr x);

    boolean visit(SQLUnaryExpr x);

    void endVisit(SQLUnaryExpr x);

    boolean visit(SQLHexExpr x);

    void endVisit(SQLHexExpr x);

    boolean visit(SQLSelect x);

    void endVisit(SQLSelect select);

    boolean visit(SQLSelectQueryBlock x);

    void endVisit(SQLSelectQueryBlock x);

    boolean visit(SQLExprTableSource x);

    void endVisit(SQLExprTableSource x);

    boolean visit(SQLOrderBy x);

    void endVisit(SQLOrderBy x);

    boolean visit(SQLSelectOrderByItem x);

    void endVisit(SQLSelectOrderByItem x);

    boolean visit(SQLDropTableStatement x);

    void endVisit(SQLDropTableStatement x);

    boolean visit(SQLCreateTableStatement x);

    void endVisit(SQLCreateTableStatement x);

    boolean visit(SQLColumnDefinition x);

    void endVisit(SQLColumnDefinition x);

    boolean visit(SQLDataType x);

    void endVisit(SQLDataType x);

    boolean visit(SQLCharacterDataType x);

    void endVisit(SQLCharacterDataType x);

    boolean visit(SQLDeleteStatement x);

    void endVisit(SQLDeleteStatement x);

    boolean visit(SQLCurrentOfCursorExpr x);

    void endVisit(SQLCurrentOfCursorExpr x);

    boolean visit(SQLInsertStatement x);

    void endVisit(SQLInsertStatement x);

    boolean visit(SQLInsertStatement.ValuesClause x);

    void endVisit(SQLInsertStatement.ValuesClause x);

    boolean visit(SQLUpdateSetItem x);

    void endVisit(SQLUpdateSetItem x);

    boolean visit(SQLUpdateStatement x);

    void endVisit(SQLUpdateStatement x);

    boolean visit(SQLCreateViewStatement x);

    void endVisit(SQLCreateViewStatement x);
    
    boolean visit(SQLCreateViewStatement.Column x);
    
    void endVisit(SQLCreateViewStatement.Column x);

    boolean visit(NotNullConstraint x);

    void endVisit(NotNullConstraint x);

    void endVisit(SQLMethodInvokeExpr x);

    boolean visit(SQLMethodInvokeExpr x);

    void endVisit(SQLUnionQuery x);

    boolean visit(SQLUnionQuery x);

    void endVisit(SQLSetStatement x);

    boolean visit(SQLSetStatement x);

    void endVisit(SQLAssignItem x);

    boolean visit(SQLAssignItem x);

    void endVisit(SQLCallStatement x);

    boolean visit(SQLCallStatement x);

    void endVisit(SQLJoinTableSource x);

    boolean visit(SQLJoinTableSource x);

    void endVisit(SQLSomeExpr x);

    boolean visit(SQLSomeExpr x);

    void endVisit(SQLAnyExpr x);

    boolean visit(SQLAnyExpr x);

    void endVisit(SQLAllExpr x);

    boolean visit(SQLAllExpr x);

    void endVisit(SQLInSubQueryExpr x);

    boolean visit(SQLInSubQueryExpr x);

    void endVisit(SQLListExpr x);

    boolean visit(SQLListExpr x);

    void endVisit(SQLSubqueryTableSource x);

    boolean visit(SQLSubqueryTableSource x);

    void endVisit(SQLTruncateStatement x);

    boolean visit(SQLTruncateStatement x);

    void endVisit(SQLDefaultExpr x);

    boolean visit(SQLDefaultExpr x);

    void endVisit(SQLCommentStatement x);

    boolean visit(SQLCommentStatement x);

    void endVisit(SQLUseStatement x);

    boolean visit(SQLUseStatement x);

    boolean visit(SQLAlterTableAddColumn x);

    void endVisit(SQLAlterTableAddColumn x);

    boolean visit(SQLAlterTableDropColumnItem x);

    void endVisit(SQLAlterTableDropColumnItem x);

    boolean visit(SQLAlterTableDropIndex x);

    void endVisit(SQLAlterTableDropIndex x);

    boolean visit(SQLDropIndexStatement x);

    void endVisit(SQLDropIndexStatement x);

    boolean visit(SQLDropViewStatement x);

    void endVisit(SQLDropViewStatement x);

    boolean visit(SQLSavePointStatement x);

    void endVisit(SQLSavePointStatement x);

    boolean visit(SQLRollbackStatement x);

    void endVisit(SQLRollbackStatement x);

    boolean visit(SQLReleaseSavePointStatement x);

    void endVisit(SQLReleaseSavePointStatement x);

    void endVisit(SQLCommentHint x);

    boolean visit(SQLCommentHint x);

    void endVisit(SQLCreateDatabaseStatement x);

    boolean visit(SQLCreateDatabaseStatement x);

    void endVisit(SQLOver x);

    boolean visit(SQLOver x);

    void endVisit(SQLColumnPrimaryKey x);

    boolean visit(SQLColumnPrimaryKey x);

    boolean visit(SQLColumnUniqueKey x);

    void endVisit(SQLColumnUniqueKey x);

    void endVisit(SQLWithSubqueryClause x);

    boolean visit(SQLWithSubqueryClause x);

    void endVisit(SQLWithSubqueryClause.Entry x);

    boolean visit(SQLWithSubqueryClause.Entry x);

    void endVisit(SQLAlterTableAlterColumn x);

    boolean visit(SQLAlterTableAlterColumn x);

    boolean visit(SQLCheck x);

    void endVisit(SQLCheck x);

    boolean visit(SQLAlterTableDropForeignKey x);

    void endVisit(SQLAlterTableDropForeignKey x);

    boolean visit(SQLAlterTableDropPrimaryKey x);

    void endVisit(SQLAlterTableDropPrimaryKey x);

    boolean visit(SQLAlterTableDisableKeys x);

    void endVisit(SQLAlterTableDisableKeys x);

    boolean visit(SQLAlterTableEnableKeys x);

    void endVisit(SQLAlterTableEnableKeys x);

    boolean visit(SQLAlterTableStatement x);

    void endVisit(SQLAlterTableStatement x);

    boolean visit(SQLAlterTableDisableConstraint x);

    void endVisit(SQLAlterTableDisableConstraint x);

    boolean visit(SQLAlterTableEnableConstraint x);

    void endVisit(SQLAlterTableEnableConstraint x);

    boolean visit(SQLColumnCheck x);

    void endVisit(SQLColumnCheck x);

    boolean visit(SQLExprHint x);

    void endVisit(SQLExprHint x);

    boolean visit(SQLAlterTableDropConstraint x);

    void endVisit(SQLAlterTableDropConstraint x);

    boolean visit(SQLUnique x);

    void endVisit(SQLUnique x);

    boolean visit(SQLPrimaryKeyImpl x);

    void endVisit(SQLPrimaryKeyImpl x);

    boolean visit(SQLCreateIndexStatement x);

    void endVisit(SQLCreateIndexStatement x);

    boolean visit(SQLAlterTableRenameColumn x);

    void endVisit(SQLAlterTableRenameColumn x);

    boolean visit(SQLColumnReference x);

    void endVisit(SQLColumnReference x);

    boolean visit(SQLForeignKeyImpl x);

    void endVisit(SQLForeignKeyImpl x);

    boolean visit(SQLDropSequenceStatement x);

    void endVisit(SQLDropSequenceStatement x);

    boolean visit(SQLDropTriggerStatement x);

    void endVisit(SQLDropTriggerStatement x);

    void endVisit(SQLDropUserStatement x);

    boolean visit(SQLDropUserStatement x);

    void endVisit(SQLExplainStatement x);

    boolean visit(SQLExplainStatement x);

    void endVisit(SQLGrantStatement x);

    boolean visit(SQLGrantStatement x);

    void endVisit(SQLDropDatabaseStatement x);

    boolean visit(SQLDropDatabaseStatement x);

    void endVisit(SQLAlterTableAddIndex x);

    boolean visit(SQLAlterTableAddIndex x);

    void endVisit(SQLAlterTableAddConstraint x);

    boolean visit(SQLAlterTableAddConstraint x);

    void endVisit(SQLCreateTriggerStatement x);

    boolean visit(SQLCreateTriggerStatement x);
    
    void endVisit(SQLDropFunctionStatement x);
    
    boolean visit(SQLDropFunctionStatement x);
    
    void endVisit(SQLDropTableSpaceStatement x);
    
    boolean visit(SQLDropTableSpaceStatement x);
    
    void endVisit(SQLDropProcedureStatement x);
    
    boolean visit(SQLDropProcedureStatement x);
    
    void endVisit(SQLBooleanExpr x);
    
    boolean visit(SQLBooleanExpr x);
    
    void endVisit(SQLUnionQueryTableSource x);

    boolean visit(SQLUnionQueryTableSource x);
    
    void endVisit(SQLTimestampExpr x);
    
    boolean visit(SQLTimestampExpr x);
    
    void endVisit(SQLRevokeStatement x);
    
    boolean visit(SQLRevokeStatement x);
    
    void endVisit(SQLBinaryExpr x);
    
    boolean visit(SQLBinaryExpr x);
    
    void endVisit(SQLAlterTableRename x);
    
    boolean visit(SQLAlterTableRename x);
    
    void endVisit(SQLAlterViewRenameStatement x);
    
    boolean visit(SQLAlterViewRenameStatement x);
    
    void endVisit(SQLShowTablesStatement x);
    
    boolean visit(SQLShowTablesStatement x);
    
    void endVisit(SQLAlterTableAddPartition x);
    
    boolean visit(SQLAlterTableAddPartition x);
    
    void endVisit(SQLAlterTableDropPartition x);
    
    boolean visit(SQLAlterTableDropPartition x);
    
    void endVisit(SQLAlterTableRenamePartition x);
    
    boolean visit(SQLAlterTableRenamePartition x);
    
    void endVisit(SQLAlterTableSetComment x);
    
    boolean visit(SQLAlterTableSetComment x);
    
    void endVisit(SQLAlterTableSetLifecycle x);
    
    boolean visit(SQLAlterTableSetLifecycle x);
    
    void endVisit(SQLAlterTableEnableLifecycle x);
    
    boolean visit(SQLAlterTableEnableLifecycle x);
    
    void endVisit(SQLAlterTableDisableLifecycle x);
    
    boolean visit(SQLAlterTableDisableLifecycle x);
    
    void endVisit(SQLAlterTableTouch x);
    
    boolean visit(SQLAlterTableTouch x);
    
    void endVisit(SQLArrayExpr x);
    
    boolean visit(SQLArrayExpr x);
    
}
