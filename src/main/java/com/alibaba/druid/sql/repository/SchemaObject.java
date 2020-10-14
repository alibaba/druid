/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint;
import com.alibaba.druid.util.FnvHash;

/**
 * Created by wenshao on 03/06/2017.
 */
public class SchemaObject {
    private final Schema schema;
    private final String name;
    private final long   hashCode64;

    private final SchemaObjectType type;
    private SQLStatement statement;
    private long rowCount = -1;

    protected SchemaObject(Schema schema, String name, SchemaObjectType type) {
        this(schema, name, type, null);
    }

    protected SchemaObject(Schema schema, String name, SchemaObjectType type, SQLStatement statement) {
        this.schema = schema;
        this.name = name;
        this.type = type;
        this.statement = statement;

        this.hashCode64 = FnvHash.hashCode64(name);
    }

    public SchemaObject clone() {
        SchemaObject x = new SchemaObject(schema, name, type);

        if (statement != null) {
            x.statement = statement.clone();
        }
        x.rowCount = rowCount;

        return x;
    }

    public long nameHashCode64() {
        return hashCode64;
    }

    public static enum Type {
        Sequence, Table, View, Index, Function
    }

    public SQLStatement getStatement() {
        return statement;
    }

    public SQLColumnDefinition findColumn(String columName) {
        long hash = FnvHash.hashCode64(columName);
        return findColumn(hash);
    }

    public SQLColumnDefinition findColumn(long columNameHash) {
        if (statement == null) {
            return null;
        }

        if (statement instanceof SQLCreateTableStatement) {
            return ((SQLCreateTableStatement) statement).findColumn(columNameHash);
        }

        return null;
    }

    public boolean matchIndex(String columnName) {
        if (statement == null) {
            return false;
        }

        if (statement instanceof SQLCreateTableStatement) {
            SQLTableElement index = ((SQLCreateTableStatement) statement).findIndex(columnName);
            return index != null;
        }

        return false;
    }

    public boolean matchKey(String columnName) {
        if (statement == null) {
            return false;
        }

        if (statement instanceof SQLCreateTableStatement) {
            SQLTableElement index = ((SQLCreateTableStatement) statement).findIndex(columnName);
            return index instanceof SQLUniqueConstraint;
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public SchemaObjectType getType() {
        return type;
    }

    public long getRowCount() {
        return rowCount;
    }

    public Schema getSchema() {
        return schema;
    }
}
