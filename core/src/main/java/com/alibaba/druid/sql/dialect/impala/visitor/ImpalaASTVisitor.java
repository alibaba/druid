package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaInsertStatement;

public interface ImpalaASTVisitor extends HiveASTVisitor {
    default boolean visit(ImpalaCreateTableStatement x) {
        return true;
    }

    default void endVisit(ImpalaCreateTableStatement x) {
    }

    default boolean visit(ImpalaInsertStatement x) {
        return true;
    }

    default void endVisit(ImpalaInsertStatement x) {
    }
}
