/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
