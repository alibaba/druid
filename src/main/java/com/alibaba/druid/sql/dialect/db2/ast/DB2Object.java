/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.alibaba.druid.util.FnvHash;


public interface DB2Object extends SQLObject {
    void accept0(DB2ASTVisitor visitor);

    interface Constants {
        long CURRENT_DATE = FnvHash.fnv1a_64_lower("CURRENT DATE");
        long CURRENT_DATE2 = FnvHash.fnv1a_64_lower("CURRENT_DATE");
        long CURRENT_TIME = FnvHash.fnv1a_64_lower("CURRENT TIME");
        long CURRENT_SCHEMA = FnvHash.fnv1a_64_lower("CURRENT SCHEMA");
    }
}
