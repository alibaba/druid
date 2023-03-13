package com.alibaba.druid.sql.dialect.presto.visitor;

import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface PrestoVisitor extends SQLASTVisitor {
    default boolean visit(PrestoCreateTableStatement x) {
        return true;
    }

    default void endVisit(PrestoCreateTableStatement x) {
    }
}
