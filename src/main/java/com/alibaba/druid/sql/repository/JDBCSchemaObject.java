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
package com.alibaba.druid.sql.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.util.FnvHash;

/**
 * Created by zone1511 for issue #2702
 */

public class JDBCSchemaObject implements SchemaObject {

    private final String                     name;
    private final long                       hashCode64;

    private final SchemaObjectType           type;
    private Map<String, SQLColumnDefinition> colNameMapping = new LinkedHashMap<String, SQLColumnDefinition>();
    private Map<Long, SQLColumnDefinition>   colHashMapping = new LinkedHashMap<Long, SQLColumnDefinition>();

    public Collection<SQLColumnDefinition> getColumns() {
        return colNameMapping.values();
    }

    public JDBCSchemaObject(String name, SchemaObjectType type){
        super();
        this.name = name;
        this.hashCode64 = FnvHash.hashCode64(name);
        this.type = type;
    }

    public void addColumn(SQLColumnDefinition newCol) {
        colNameMapping.put(newCol.getNameAsString(), newCol);
        colHashMapping.put(newCol.nameHashCode64(), newCol);
    }

    @Override
    public SQLStatement getStatement() {
        return null;
    }

    @Override
    public SQLColumnDefinition findColumn(String columName) {
        return colNameMapping.get(columName);
    }

    @Override
    public SQLColumnDefinition findColumn(long columNameHash) {
        return colHashMapping.get(columNameHash);
    }

    @Override
    public boolean matchIndex(String columnName) {
        return false;
    }

    @Override
    public boolean matchKey(String columnName) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SchemaObjectType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long nameHashCode64() {
        return hashCode64;
    }

    @Override
    public long getRowCount() {
        return 0;
    }

}
