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
package com.alibaba.druid.sql.dialect.db2.visitor;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.DB2Object;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateTableStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class DB2SchemaStatVisitor extends SchemaStatVisitor implements DB2ASTVisitor {
    public DB2SchemaStatVisitor() {
        super (JdbcConstants.DB2);
    }

    @Override
    public boolean visit(DB2SelectQueryBlock x) {
        return this.visit((SQLSelectQueryBlock) x);
    }

    @Override
    public void endVisit(DB2SelectQueryBlock x) {
        super.endVisit((SQLSelectQueryBlock) x);
    }

    @Override
    public boolean visit(DB2ValuesStatement x) {
        return false;
    }
    
    @Override
    public void endVisit(DB2ValuesStatement x) {
        
    }

    @Override
    public boolean visit(DB2CreateTableStatement x) {
        return visit((SQLCreateTableStatement) x);
    }

    @Override
    public void endVisit(DB2CreateTableStatement x) {

    }

    protected boolean isPseudoColumn(long hash64) {
        return hash64 == DB2Object.Constants.CURRENT_DATE || hash64 == DB2Object.Constants.CURRENT_TIME;
    }
}
