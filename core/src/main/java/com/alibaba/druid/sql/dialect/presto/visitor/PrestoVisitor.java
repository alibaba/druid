package com.alibaba.druid.sql.dialect.presto.visitor;

import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterFunctionStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterSchemaStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoCreateTableStatement;
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
}
