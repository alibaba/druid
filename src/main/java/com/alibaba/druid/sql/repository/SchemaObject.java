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

/**
 * Created by wenshao on 03/06/2017.
 */
public interface SchemaObject {

    SQLStatement getStatement();

    SQLColumnDefinition findColumn(String columName);
    SQLColumnDefinition findColumn(long columNameHash);

    boolean matchIndex(String columnName);

    boolean matchKey(String columnName);

    SchemaObjectType getType();

    String getName();
    long nameHashCode64();

    long getRowCount();
}
