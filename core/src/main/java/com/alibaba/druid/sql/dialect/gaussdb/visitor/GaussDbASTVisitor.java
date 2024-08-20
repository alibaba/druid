package com.alibaba.druid.sql.dialect.gaussdb.visitor;

import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface GaussDbASTVisitor extends SQLASTVisitor {
    default boolean visit(GaussDbCreateTableStatement x) {
        return true;
    }
    default void endVisit(GaussDbCreateTableStatement x) {
    }
    default boolean visit(GaussDbDistributeBy x) {
        return true;
    }
    default void endVisit(GaussDbDistributeBy x) {
    }
}
