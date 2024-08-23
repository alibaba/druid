package com.alibaba.druid.sql.dialect.athena.visitor;

import com.alibaba.druid.sql.dialect.athena.ast.stmt.AthenaCreateTableStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoASTVisitor;

public interface AthenaASTVisitor extends PrestoASTVisitor {
    default boolean visit(AthenaCreateTableStatement x) {
        return true;
    }

    default void endVisit(AthenaCreateTableStatement x) {
    }
}
