package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import junit.framework.TestCase;

public class SQLASTVisitorAdapterTest extends TestCase {
    public void test_adapter() throws Exception {
        SQLASTVisitorAdapter adapter = new SQLASTVisitorAdapter();
        new SQLBinaryOpExpr().accept(adapter);
        new SQLInListExpr().accept(adapter);
        new SQLSelectQueryBlock().accept(adapter);
        new SQLDropTableStatement().accept(adapter);
        new SQLCreateTableStatement().accept(adapter);
        new SQLDeleteStatement().accept(adapter);
        new SQLCurrentOfCursorExpr().accept(adapter);
        new SQLInsertStatement().accept(adapter);
        new SQLUpdateStatement().accept(adapter);
        new SQLNotNullConstraint().accept(adapter);
        new SQLMethodInvokeExpr().accept(adapter);
        new SQLCallStatement().accept(adapter);
        new SQLSomeExpr().accept(adapter);
        new SQLAnyExpr().accept(adapter);
        new SQLAllExpr().accept(adapter);
        new SQLDefaultExpr().accept(adapter);
        new SQLCommentStatement().accept(adapter);
        new SQLDropViewStatement().accept(adapter);
        new SQLSavePointStatement().accept(adapter);
        new SQLReleaseSavePointStatement().accept(adapter);
        new SQLCreateDatabaseStatement().accept(adapter);
        new SQLAlterTableDropIndex().accept(adapter);
        new SQLOver().accept(adapter);
        new SQLWithSubqueryClause().accept(adapter);
        new SQLAlterTableAlterColumn().accept(adapter);
        new SQLAlterTableStatement().accept(adapter);
        new SQLAlterTableDisableConstraint().accept(adapter);
        new SQLAlterTableEnableConstraint().accept(adapter);
        new SQLColumnCheck().accept(adapter);
        new SQLExprHint().accept(adapter);
        new SQLAlterTableDropConstraint().accept(adapter);
    }
}
