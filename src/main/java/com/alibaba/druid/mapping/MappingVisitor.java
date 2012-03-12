package com.alibaba.druid.mapping;

import java.util.Map;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface MappingVisitor extends SQLASTVisitor {

    Entity getFirstEntity();

    Entity getEntity(String name);

    Map<String, Entity> getEntities();

    Map<String, SQLTableSource> getTableSources();
}
