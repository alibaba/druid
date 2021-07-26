package com.alibaba.druid.sql.dialect.dm.visitor;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.dm.ast.DMSQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author two brother
 * @date 2021/7/26 11:32
 */
public interface DMASTVisitor extends SQLASTVisitor {

    default boolean visit(DMSQLSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(DMSQLSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }
}
