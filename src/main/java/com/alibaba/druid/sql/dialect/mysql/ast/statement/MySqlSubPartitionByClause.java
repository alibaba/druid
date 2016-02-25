/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLPartitioningClause;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;

public abstract class MySqlSubPartitionByClause extends MySqlObjectImpl implements SQLPartitioningClause {

    protected SQLExpr             subPartitionsCount;

    protected boolean             linear;

    protected List<SQLAssignItem> options = new ArrayList<SQLAssignItem>();

    public SQLExpr getSubPartitionsCount() {
        return subPartitionsCount;
    }

    public void setSubPartitionsCount(SQLExpr subPartitionsCount) {
        this.subPartitionsCount = subPartitionsCount;
    }

    public boolean isLinear() {
        return linear;
    }

    public void setLinear(boolean linear) {
        this.linear = linear;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

}
