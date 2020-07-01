package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnCheck;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class SQLASTVisitorAdapterTest extends TestCase {

    public void test_adapter() throws Exception {
        SQLASTVisitorAdapter adapter = new SQLASTVisitorAdapter();
        new SQLBinaryOpExpr().accept(adapter);
        new SQLInListExpr().accept(adapter);
        new SQLSelectQueryBlock().accept(adapter);
        new SQLDropTableStatement().accept(adapter);
        new SQLCreateTableStatement().accept(adapter);
        new SQLDeleteStatement().accept(adapter);
        new SQLCurrentOfCursorExpr ().accept(adapter);
        new SQLInsertStatement ().accept(adapter);
        new SQLUpdateStatement ().accept(adapter);
        new SQLNotNullConstraint ().accept(adapter);
        new SQLMethodInvokeExpr ().accept(adapter);
        new SQLCallStatement ().accept(adapter);
        new SQLSomeExpr ().accept(adapter);
        new SQLAnyExpr ().accept(adapter);
        new SQLAllExpr ().accept(adapter);
        new SQLDefaultExpr ().accept(adapter);
        new SQLCommentStatement ().accept(adapter);
        new SQLDropViewStatement ().accept(adapter);
        new SQLSavePointStatement ().accept(adapter);
        new SQLReleaseSavePointStatement ().accept(adapter);
        new SQLCreateDatabaseStatement ().accept(adapter);
        new SQLAlterTableDropIndex ().accept(adapter);
        new SQLOver ().accept(adapter);
        new SQLWithSubqueryClause().accept(adapter);
        new SQLAlterTableAlterColumn ().accept(adapter);
        new SQLAlterTableStatement ().accept(adapter);
        new SQLAlterTableDisableConstraint ().accept(adapter);
        new SQLAlterTableEnableConstraint ().accept(adapter);
        new SQLColumnCheck ().accept(adapter);
        new SQLExprHint ().accept(adapter);
        new SQLAlterTableDropConstraint ().accept(adapter);
    }
}
