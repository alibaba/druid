package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.sql.dialect.starrocks.ast.statement.*;
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

    default boolean visit(StarRocksCreatePipeStatement x) {
        return true;
    }

    default void endVisit(StarRocksCreatePipeStatement x) {
    }

    default boolean visit(StarRocksCreateDictionaryStatement x) {
        return true;
    }

    default void endVisit(StarRocksCreateDictionaryStatement x) {
    }

    default boolean visit(StarRocksCreateStorageVolumeStatement x) {
        return true;
    }

    default void endVisit(StarRocksCreateStorageVolumeStatement x) {
    }
}
