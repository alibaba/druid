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
package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class OdpsInsertStatement extends SQLStatementImpl implements SQLStatement {

    private SQLSubqueryTableSource from;

    private List<OdpsInsert>       items = new ArrayList<OdpsInsert>();
    
    public OdpsInsertStatement() {
        super (JdbcConstants.ODPS);
    }

    public void setFrom(SQLSubqueryTableSource from) {
        this.from = from;
    }

    public SQLSubqueryTableSource getFrom() {
        return from;
    }

    public List<OdpsInsert> getItems() {
        return items;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }
    
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, from);
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }
}
