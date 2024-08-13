package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.sql.dialect.clickhouse.ast.CKAlterTableUpdateStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKCreateTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKSelectQueryBlock;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseColumnCodec;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseColumnTTL;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface CKASTVisitor extends SQLASTVisitor {
    default boolean visit(CKCreateTableStatement x) {
        return true;
    }

    default void endVisit(CKCreateTableStatement x) {
    }

    default boolean visit(CKSelectQueryBlock x) {
        return true;
    }

    default void endVisit(CKSelectQueryBlock x) {
    }

    default boolean visit(CKAlterTableUpdateStatement x) {
        return true;
    }

    default void endVisit(CKAlterTableUpdateStatement x) {
    }

    default boolean visit(ClickhouseColumnCodec x) {
        return true;
    }

    default void endVisit(ClickhouseColumnCodec x) {
    }

    default boolean visit(ClickhouseColumnTTL x) {
        return true;
    }

    default void endVisit(ClickhouseColumnTTL x) {
    }
}
