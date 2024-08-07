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
package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.dialect.transact.ast.TransactSQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface SQLServerObject extends TransactSQLObject {
    default void accept0(SQLASTVisitor v) {
        if (v instanceof SQLServerASTVisitor) {
            accept0((SQLServerASTVisitor) v);
        }
    }
    void accept0(SQLServerASTVisitor visitor);
}
