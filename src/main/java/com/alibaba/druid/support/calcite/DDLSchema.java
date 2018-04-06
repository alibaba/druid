/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.calcite;

import com.google.common.collect.*;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.materialize.Lattice;
import org.apache.calcite.schema.*;
import org.apache.calcite.schema.Table;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by wenshao on 17/07/2017.
 */
public class DDLSchema implements Schema, SchemaPlus {
    private Map<String, DDLTable> tables;
    private Map<String, DDLTable> functions;
    private Map<String, DDLSchema> subSchemas;

    @Override
    public Table getTable(String name) {
        return tables.get(name);
    }

    @Override
    public Set<String> getTableNames() {
        return tables.keySet();
    }

    @Override
    public Collection<Function> getFunctions(String name) {
        return null;
    }

    @Override
    public Set<String> getFunctionNames() {
        return null;
    }

    @Override
    public SchemaPlus getParentSchema() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public SchemaPlus getSubSchema(String name) {
        return null;
    }

    @Override
    public SchemaPlus add(String name, Schema schema) {
        return null;
    }

    @Override
    public void add(String name, Table table) {

    }

    @Override
    public void add(String name, Function function) {

    }

    @Override
    public void add(String name, Lattice lattice) {

    }

    @Override
    public Set<String> getSubSchemaNames() {
        return subSchemas.keySet();
    }

    @Override
    public Expression getExpression(SchemaPlus parentSchema, String name) {
        return null;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Schema snapshot(SchemaVersion version) {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return null;
    }

    @Override
    public void setCacheEnabled(boolean cache) {

    }

    @Override
    public boolean isCacheEnabled() {
        return false;
    }

    public void setPath(ImmutableList<ImmutableList<String>> path) {

    }

    public boolean contentsHaveChangedSince(long lastCheck, long now) {
        return false;
    }

    public Schema snapshot(long now) {
        return null;
    }
}
