package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;

public interface ImpalaASTVisitor {
    default boolean visit(ImpalaCreateTableStatement x) {
        return true;
    }

    default void endVisit(ImpalaCreateTableStatement x) {
    }
}
