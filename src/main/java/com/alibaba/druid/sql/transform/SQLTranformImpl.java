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
package com.alibaba.druid.sql.transform;

import com.alibaba.druid.DbType;

public abstract class SQLTranformImpl implements SQLTranform {
    protected DbType sourceDbType;
    protected DbType targetDbType;

    @Override
    public DbType getSourceDbType() {
        return sourceDbType;
    }

    @Override
    public DbType getTargetDbType() {
        return targetDbType;
    }
}
