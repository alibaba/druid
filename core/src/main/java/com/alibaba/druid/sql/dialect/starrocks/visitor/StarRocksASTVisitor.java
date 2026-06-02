package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateCatalogStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateMaterializedViewStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksSubmitTaskStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface StarRocksASTVisitor extends SQLASTVisitor {
    default boolean visit(StarRocksCreateTableStatement x) {
        return true;
    }

    default void endVisit(StarRocksCreateTableStatement x) {
    }

    default boolean visit(StarRocksCreateMaterializedViewStatement x) {
        return true;
    }

    default void endVisit(StarRocksCreateMaterializedViewStatement x) {
    }

    default boolean visit(StarRocksSubmitTaskStatement x) {
        return true;
    }

    default void endVisit(StarRocksSubmitTaskStatement x) {
    }

    default boolean visit(StarRocksCreateCatalogStatement x) {
        return true;
    }

    default void endVisit(StarRocksCreateCatalogStatement x) {
    }
}
