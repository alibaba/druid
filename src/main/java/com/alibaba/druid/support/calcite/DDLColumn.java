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

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;

/**
 * Created by wenshao on 17/07/2017.
 */
public class DDLColumn implements RelDataTypeField {
    private DDLTable table;
    private final SQLColumnDefinition column;
    private final int index;

    private final String name;

    public DDLColumn(DDLTable table, SQLColumnDefinition column, int index) {
        this.table = table;
        this.column = column;
        this.name = column.getName().getSimpleName();
        this.index = index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public RelDataType getType() {
        return table;
    }

    @Override
    public boolean isDynamicStar() {
        return false;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public RelDataType getValue() {
        return null;
    }

    @Override
    public RelDataType setValue(RelDataType value) {
        return null;
    }
}
