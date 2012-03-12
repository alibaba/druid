package com.alibaba.druid.mapping.spi;

import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public interface MappingProvider {

    MappingVisitor createMappingVisitor(MappingEngine engine);

    SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out);

    SQLSelectQueryBlock explainToSQLObject(MappingEngine engine, String sql);
}
