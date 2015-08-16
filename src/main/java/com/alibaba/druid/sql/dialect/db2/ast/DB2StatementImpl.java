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
package com.alibaba.druid.sql.dialect.db2.ast;

import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public abstract class DB2StatementImpl extends SQLStatementImpl implements DB2Object {

    public DB2StatementImpl() {
        super(JdbcConstants.DB2);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof DB2ASTVisitor) {
            accept0((DB2ASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    @Override
    public abstract void accept0(DB2ASTVisitor visitor);
}
