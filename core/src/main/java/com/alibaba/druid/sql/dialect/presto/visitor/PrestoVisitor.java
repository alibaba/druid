package com.alibaba.druid.sql.dialect.presto.visitor;

import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterFunctionStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterSchemaStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoCreateTableStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoDeallocatePrepareStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoExecuteStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoPrepareStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface PrestoVisitor extends SQLASTVisitor {
    default boolean visit(PrestoCreateTableStatement x) {
        return true;
    }

    default void endVisit(PrestoCreateTableStatement x) {
    }

    default boolean visit(PrestoAlterFunctionStatement x) {
        return true;
    }

    default void endVisit(PrestoAlterFunctionStatement x) {
    }

    default boolean visit(PrestoAlterSchemaStatement x) {
        return true;
    }

    default void endVisit(PrestoAlterSchemaStatement x) {
    }

    default boolean visit(PrestoPrepareStatement x) {
        return true;
    }

    default void endVisit(PrestoPrepareStatement x) {
    }

    default boolean visit(PrestoExecuteStatement x) {
        return true;
    }

    default void endVisit(PrestoExecuteStatement x) {
    }

    default boolean visit(PrestoDeallocatePrepareStatement x) {
        return true;
    }

    default void endVisit(PrestoDeallocatePrepareStatement x) {
    }
}
