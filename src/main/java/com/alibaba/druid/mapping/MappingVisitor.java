package com.alibaba.druid.mapping;

import java.util.Map;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;

public interface MappingVisitor {
	Entity getFirstEntity();
	
	Entity getEntity(String name);
	
	Map<String, Entity> getEntities();
	
	Map<String, SQLTableSource> getTableSources();
}
