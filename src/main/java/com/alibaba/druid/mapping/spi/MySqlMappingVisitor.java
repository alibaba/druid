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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingContext;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class MySqlMappingVisitor extends MySqlASTVisitorAdapter implements MappingVisitor {

    private final MappingEngine               engine;
    private final Map<String, SQLTableSource> tableSources   = new LinkedHashMap<String, SQLTableSource>();

    private final MappingContext              context;
    private final List<PropertyValue>         propertyValues = new ArrayList<PropertyValue>();
    private int                               variantIndex   = 0;
    private final List<SQLExpr>               unresolveList  = new ArrayList<SQLExpr>();

    public MySqlMappingVisitor(MappingEngine engine){
        this(engine, new MappingContext());
    }

    public MySqlMappingVisitor(MappingEngine engine, MappingContext context){
        this.engine = engine;
        this.context = context;
    }

    public MappingEngine getEngine() {
        return engine;
    }

    public List<Object> getParameters() {
        return context.getParameters();
    }

    public MappingContext getContext() {
        return context;
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public Map<String, SQLTableSource> getTableSources() {
        return tableSources;
    }

    public Map<String, Entity> getEntities() {
        return engine.getEntities();
    }

    @Override
    public String resolveTableName(Entity entity) {
        return engine.resolveTableName(entity, context);
    }

    @Override
    public String resovleColumnName(Entity entity, Property property) {
        return engine.resovleColumnName(entity, property, context);
    }

    public Entity getEntity(String name) {
        return MappingVisitorUtils.getEntity(this, name);
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLPropertyExpr x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        return MappingVisitorUtils.visit(this, x);
    }
    
    @Override
    public boolean visit(SQLAllColumnExpr x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public int getAndIncrementVariantIndex() {
        return variantIndex++;
    }

    public int getVariantIndex() {
        return variantIndex;
    }

    public List<SQLExpr> getUnresolveList() {
        return unresolveList;
    }

    @Override
    public void afterResolve() {
        MappingVisitorUtils.afterResolve(this);
    }
}
