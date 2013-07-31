/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
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
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddForeignKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropForeinKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCharactorDataType;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
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

    boolean visit(SQLCharactorDataType x);

    void endVisit(SQLCharactorDataType x);

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

    boolean visit(SQLAlterTableAddPrimaryKey x);

    void endVisit(SQLAlterTableAddPrimaryKey x);

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

    void endVisit(SQLWithSubqueryClause x);

    boolean visit(SQLWithSubqueryClause x);

    void endVisit(SQLWithSubqueryClause.Entry x);

    boolean visit(SQLWithSubqueryClause.Entry x);

    void endVisit(SQLAlterTableAlterColumn x);

    boolean visit(SQLAlterTableAlterColumn x);

    boolean visit(SQLCheck x);

    void endVisit(SQLCheck x);

    boolean visit(SQLAlterTableDropForeinKey x);

    void endVisit(SQLAlterTableDropForeinKey x);

    boolean visit(SQLAlterTableDropPrimaryKey x);

    void endVisit(SQLAlterTableDropPrimaryKey x);

    boolean visit(SQLAlterTableDisableKeys x);

    void endVisit(SQLAlterTableDisableKeys x);

    boolean visit(SQLAlterTableEnableKeys x);

    void endVisit(SQLAlterTableEnableKeys x);

    boolean visit(SQLAlterTableStatement x);

    void endVisit(SQLAlterTableStatement x);

    boolean visit(SQLAlterTableAddForeignKey x);

    void endVisit(SQLAlterTableAddForeignKey x);

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
}
