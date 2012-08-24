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
package com.alibaba.druid.sql.dialect.sqlserver.visitor;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.Top;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;


public class SQLServerSchemaStatVisitor extends SchemaStatVisitor implements SQLServerASTVisitor {

    @Override
    public String getDbType() {
        return JdbcUtils.SQL_SERVER;
    }
    
    @Override
    public boolean visit(SQLServerSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    @Override
    public void endVisit(SQLServerSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    @Override
    public boolean visit(Top x) {
        return false;
    }

    @Override
    public void endVisit(Top x) {
        
    }

    @Override
    public boolean visit(SQLServerObjectReferenceExpr x) {
        return false;
    }

    @Override
    public void endVisit(SQLServerObjectReferenceExpr x) {
        
    }


}
