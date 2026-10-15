package com.alibaba.druid.sql.dialect.sqlite.visitor;

import com.alibaba.druid.sql.dialect.sqlite.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface SQLiteASTVisitor extends SQLASTVisitor {
    default boolean visit(SQLitePragmaStatement x) {
        return true;
    }

    default void endVisit(SQLitePragmaStatement x) {
    }

    default boolean visit(SQLiteAttachStatement x) {
        return true;
    }

    default void endVisit(SQLiteAttachStatement x) {
    }

    default boolean visit(SQLiteDetachStatement x) {
        return true;
    }

    default void endVisit(SQLiteDetachStatement x) {
    }

    default boolean visit(SQLiteVacuumStatement x) {
        return true;
    }

    default void endVisit(SQLiteVacuumStatement x) {
    }

    default boolean visit(SQLiteReindexStatement x) {
        return true;
    }

    default void endVisit(SQLiteReindexStatement x) {
    }
}
