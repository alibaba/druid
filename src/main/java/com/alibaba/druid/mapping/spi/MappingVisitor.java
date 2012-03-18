package com.alibaba.druid.mapping.spi;

import java.util.Map;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface MappingVisitor extends SQLASTVisitor {

    Entity getFirstEntity();

    Entity getEntity(String name);

    Map<String, Entity> getEntities();

    Map<String, SQLTableSource> getTableSources();
}
