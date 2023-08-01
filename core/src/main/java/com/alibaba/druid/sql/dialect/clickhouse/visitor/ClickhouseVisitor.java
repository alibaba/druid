package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseAlterTableUpdateStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface ClickhouseVisitor extends SQLASTVisitor {
    default boolean visit(ClickhouseCreateTableStatement x) {
        return true;
    }

    default void endVisit(ClickhouseCreateTableStatement x) {
    }

    default boolean visit(ClickhouseAlterTableUpdateStatement x) {
        return true;
    }

    default void endVisit(ClickhouseAlterTableUpdateStatement x) {
    }
}
