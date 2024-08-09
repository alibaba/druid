package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;

/**
 * @author lingo
 * @date 2024/8/9 17:46
 * @description
 */
public interface ImpalaASTVisitor {
    default boolean visit(ImpalaCreateTableStatement x) {
        return true;
    }

    default void endVisit(ImpalaCreateTableStatement x) {
    }
}
