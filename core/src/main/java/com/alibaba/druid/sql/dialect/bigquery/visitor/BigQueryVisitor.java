package com.alibaba.druid.sql.dialect.bigquery.visitor;

import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryAssertStatement;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryCreateTableStatement;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQuerySelectAsStruct;
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

    default boolean visit(BigQuerySelectAsStruct x) {
        return true;
    }

    default void endVisit(BigQuerySelectAsStruct x) {
    }

    default boolean visit(BigQueryAssertStatement x) {
        return true;
    }

    default void endVisit(BigQueryAssertStatement x) {
    }

    default boolean visit(BigQueryCreateTableStatement x) {
        return true;
    }

    default void endVisit(BigQueryCreateTableStatement x) {
    }
}
