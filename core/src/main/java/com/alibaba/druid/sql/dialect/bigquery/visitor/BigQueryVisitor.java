package com.alibaba.druid.sql.dialect.bigquery.visitor;

import com.alibaba.druid.sql.dialect.bigquery.ast.*;
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

    default boolean visit(BigQueryCharExpr x) {
        return true;
    }

    default void endVisit(BigQueryCharExpr x) {
    }

    default boolean visit(BigQueryExecuteImmediateStatement x) {
        return true;
    }

    default void endVisit(BigQueryExecuteImmediateStatement x) {
    }

    default boolean visit(BigQueryCreateModelStatement x) {
        return true;
    }

    default void endVisit(BigQueryCreateModelStatement x) {
    }

    default boolean visit(BigQueryModelExpr x) {
        return true;
    }

    default void endVisit(BigQueryModelExpr x) {
    }

    default boolean visit(BigQueryTableExpr x) {
        return true;
    }

    default void endVisit(BigQueryTableExpr x) {
    }
}
