package com.alibaba.druid.mapping;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public interface MappingProvider {

    MappingVisitor createMappingVisitor(MappingEngine engine);

    SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out);

    SQLSelectQueryBlock explainToSQLObject(MappingEngine engine, String sql);
}
