package com.alibaba.druid.sql.dialect.dm.visitor;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface DmASTVisitor extends SQLASTVisitor {
    default void endVisit(DmSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(DmSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(DmSelectQueryBlock.FetchClause x) {
    }

    default boolean visit(DmSelectQueryBlock.FetchClause x) {
        return true;
    }

    default void endVisit(DmSelectQueryBlock.ForClause x) {
    }

    default boolean visit(DmSelectQueryBlock.ForClause x) {
        return true;
    }

    default void endVisit(DmDeleteStatement x) {
    }

    default boolean visit(DmDeleteStatement x) {
        return true;
    }

    default void endVisit(DmInsertStatement x) {
    }

    default boolean visit(DmInsertStatement x) {
        return true;
    }

    default void endVisit(DmSelectStatement x) {
        endVisit((SQLSelectStatement) x);
    }

    default boolean visit(DmSelectStatement x) {
        return visit((SQLSelectStatement) x);
    }

    default void endVisit(DmUpdateStatement x) {
    }

    default boolean visit(DmUpdateStatement x) {
        return true;
    }
}
