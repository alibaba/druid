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
package com.alibaba.druid.wall;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;

/**
 * Created by wenshao on 14/08/2017.
 */
public class WallUpdateCheckItem {
    public final String tableName;
    public final String columnName;
    public final SQLExpr value;
    public final List<SQLExpr> filterValues;

    public WallUpdateCheckItem(String tableName, String columnName, SQLExpr value, List<SQLExpr> filterValues) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.value = value;
        this.filterValues = filterValues;
    }
}
