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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLShowTablesStatement extends SQLStatementImpl {

    protected SQLName database;
    protected SQLExpr like;

    public SQLName getDatabase() {
        return database;
    }

    public void setDatabase(SQLName database) {
        if (database != null) {
            database.setParent(this);
        }

        this.database = database;
    }

    public SQLExpr getLike() {
        return like;
    }

    public void setLike(SQLExpr like) {
        if (like != null) {
            like.setParent(this);
        }

        this.like = like;
    }
    
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, database);
            acceptChild(visitor, like);
        }
    }
}
