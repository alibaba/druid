package com.alibaba.druid.sql.dialect.bigquery.visitor;

import com.alibaba.druid.sql.dialect.bigquery.ast.BigQuerySelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface BigQueryVisitor extends SQLASTVisitor {
    default boolean visit(BigQuerySelectQueryBlock x) {
        return true;
    }

    default void endVisit(BigQuerySelectQueryBlock x) {
    }

    default boolean visit(BigQuerySelectQueryBlock.DifferentialPrivacy x) {
        return true;
    }

    default void endVisit(BigQuerySelectQueryBlock.DifferentialPrivacy x) {
    }
}
