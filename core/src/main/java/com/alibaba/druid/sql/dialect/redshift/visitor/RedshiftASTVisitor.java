package com.alibaba.druid.sql.dialect.redshift.visitor;

import com.alibaba.druid.sql.dialect.redshift.stmt.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface RedshiftASTVisitor extends SQLASTVisitor {
    default boolean visit(RedshiftSelectQueryBlock x) {
        return true;
    }
    default void endVisit(RedshiftSelectQueryBlock x) {}
    default boolean visit(RedshiftSortKey x) {
        return true;
    }
    default void endVisit(RedshiftSortKey x) {}
    default boolean visit(RedshiftCreateTableStatement x) {
        return true;
    }
    default void endVisit(RedshiftCreateTableStatement x) {}
    default boolean visit(RedshiftColumnEncode x) { return true; }
    default void endVisit(RedshiftColumnEncode x) {}

    default boolean visit(RedshiftColumnKey x) {
        return true;
    }

    default void endVisit(RedshiftColumnKey x) {
    }
}
