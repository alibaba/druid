package com.alibaba.druid.mapping.spi;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingContext;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface MappingVisitor extends SQLASTVisitor {
    MappingEngine getEngine();
    
    List<Object> getParameters();
    
    MappingContext getContext();

    Entity getFirstEntity();

    Entity getEntity(String name);

    Map<String, Entity> getEntities();

    Map<String, SQLTableSource> getTableSources();
    
    String resolveTableName(Entity entity);
    
    String resovleColumnName(Entity entity, Property property);
    
    List<PropertyValue> getPropertyValues();
    
    int getAndIncrementVariantIndex();
    
    List<SQLExpr> getUnresolveList();
    
    void afterResolve();
}
