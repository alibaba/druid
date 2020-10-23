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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

public abstract class MySqlStatementImpl extends SQLStatementImpl implements MySqlStatement {

    public MySqlStatementImpl() {
        super(DbType.mysql);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    public void accept0(MySqlASTVisitor v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public List<SQLObject> getChildren() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
