package com.alibaba.druid.sql.dialect.doris.visitor;

import com.alibaba.druid.sql.dialect.doris.ast.DorisExprTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface DorisASTVisitor extends SQLASTVisitor {
    default boolean visit(DorisExprTableSource x) { return true; }
    default void endVisit(DorisExprTableSource x) {}
}
