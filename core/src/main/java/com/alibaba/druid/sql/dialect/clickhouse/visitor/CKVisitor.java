package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.sql.dialect.clickhouse.ast.CKAlterTableUpdateStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface CKVisitor extends SQLASTVisitor {
    default boolean visit(CKCreateTableStatement x) {
        return true;
    }

    default void endVisit(CKCreateTableStatement x) {
    }

    default boolean visit(CKAlterTableUpdateStatement x) {
        return true;
    }

    default void endVisit(CKAlterTableUpdateStatement x) {
    }
}
