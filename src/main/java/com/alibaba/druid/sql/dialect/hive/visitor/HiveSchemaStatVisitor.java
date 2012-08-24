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
package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;

public class HiveSchemaStatVisitor extends SchemaStatVisitor implements HiveASTVisitor {

    @Override
    public String getDbType() {
        return JdbcUtils.HIVE;
    }
    
    @Override
    public void endVisit(HiveCreateTableStatement x) {
        
    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        return visit((SQLCreateTableStatement) x);
    }

    @Override
    public void endVisit(HiveCreateTableStatement.PartitionedBy x) {

    }

    @Override
    public boolean visit(HiveCreateTableStatement.PartitionedBy x) {
        return false;
    }

    @Override
    public void endVisit(HiveShowTablesStatement x) {
        
    }

    @Override
    public boolean visit(HiveShowTablesStatement x) {
        return false;
    }
}
