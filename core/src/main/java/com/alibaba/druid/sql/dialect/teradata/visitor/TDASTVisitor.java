package com.alibaba.druid.sql.dialect.teradata.visitor;

import com.alibaba.druid.sql.dialect.teradata.ast.TDDateDataType;
import com.alibaba.druid.sql.dialect.teradata.ast.TDNormalize;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface TDASTVisitor extends SQLASTVisitor {
    default boolean visit(TDNormalize x) {
        return true;
    }
    default void endVisit(TDNormalize x) {}
    default boolean visit(TDDateDataType x) {
        return true;
    }
    default void endVisit(TDDateDataType x) {}
}
