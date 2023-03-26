package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface StarRocksASTVisitor extends SQLASTVisitor {
    default boolean visit(StarRocksCreateTableStatement x) {
        return true;
    }

    default void endVisit(StarRocksCreateTableStatement x) {
    }
}
