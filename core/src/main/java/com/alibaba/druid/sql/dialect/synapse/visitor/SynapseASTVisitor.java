package com.alibaba.druid.sql.dialect.synapse.visitor;

import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.dialect.synapse.ast.stmt.SynapseCreateTableStatement;

public interface SynapseASTVisitor extends SQLServerASTVisitor {
    default boolean visit(SynapseCreateTableStatement x) {
        return true;
    }

    default void endVisit(SynapseCreateTableStatement x) {
    }
}
