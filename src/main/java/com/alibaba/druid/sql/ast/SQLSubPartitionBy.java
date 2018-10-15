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
package com.alibaba.druid.sql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLAssignItem;

public abstract class SQLSubPartitionBy extends SQLObjectImpl {

    protected SQLExpr               subPartitionsCount;
    protected boolean               linear;
    protected List<SQLAssignItem>   options              = new ArrayList<SQLAssignItem>();
    protected List<SQLSubPartition> subPartitionTemplate = new ArrayList<SQLSubPartition>();

    public SQLExpr getSubPartitionsCount() {
        return subPartitionsCount;
    }

    public void setSubPartitionsCount(SQLExpr subPartitionsCount) {
        if (subPartitionsCount != null) {
            subPartitionsCount.setParent(this);
        }

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

    public List<SQLSubPartition> getSubPartitionTemplate() {
        return subPartitionTemplate;
    }

    public void cloneTo(SQLSubPartitionBy x) {
        if (subPartitionsCount != null) {
            x.setSubPartitionsCount(subPartitionsCount.clone());
        }
        x.linear = linear;
        for (SQLAssignItem option : options) {
            SQLAssignItem option2 = option.clone();
            option2.setParent(x);
            x.options.add(option2);
        }

        for (SQLSubPartition p : subPartitionTemplate) {
            SQLSubPartition p2 = p.clone();
            p2.setParent(x);
            x.subPartitionTemplate.add(p2);
        }
    }

    public abstract SQLSubPartitionBy clone();
}
