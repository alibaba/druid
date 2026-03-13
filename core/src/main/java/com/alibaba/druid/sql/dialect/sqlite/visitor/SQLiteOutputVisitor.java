package com.alibaba.druid.sql.dialect.sqlite.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.sqlite.SQLite;
import com.alibaba.druid.sql.dialect.sqlite.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class SQLiteOutputVisitor extends SQLASTOutputVisitor implements SQLiteASTVisitor {
    public SQLiteOutputVisitor(StringBuilder appender) {
        super(appender, DbType.sqlite, SQLite.DIALECT);
    }

    public SQLiteOutputVisitor(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
    }

    public SQLiteOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.sqlite, parameterized);
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        printCreateTable(x, true);
        return false;
    }

    @Override
    public boolean visit(SQLitePragmaStatement x) {
        printUcase("PRAGMA ");
        x.getName().accept(this);
        if (x.getValue() != null) {
            print0(" = ");
            x.getValue().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLiteAttachStatement x) {
        printUcase("ATTACH DATABASE ");
        x.getDatabase().accept(this);
        printUcase(" AS ");
        x.getSchemaName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLiteDetachStatement x) {
        printUcase("DETACH DATABASE ");
        x.getSchemaName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLiteVacuumStatement x) {
        printUcase("VACUUM");
        if (x.getSchemaName() != null) {
            print(' ');
            x.getSchemaName().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLiteReindexStatement x) {
        printUcase("REINDEX");
        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }
        return false;
    }
}
