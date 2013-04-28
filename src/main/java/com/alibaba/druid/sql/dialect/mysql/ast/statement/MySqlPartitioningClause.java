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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLPartitioningClause;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;

@SuppressWarnings("serial")
public abstract class MySqlPartitioningClause extends MySqlObjectImpl implements SQLPartitioningClause {

    private List<MySqlPartitioningDef> partitions = new ArrayList<MySqlPartitioningDef>();

    public List<MySqlPartitioningDef> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<MySqlPartitioningDef> partitions) {
        this.partitions = partitions;
    }

}
