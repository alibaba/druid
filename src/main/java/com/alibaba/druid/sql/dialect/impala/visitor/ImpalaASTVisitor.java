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
package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.sql.dialect.impala.ast.ImpalaMultiInsertStatement;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaInsert;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaInsertStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaMetaStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface ImpalaASTVisitor extends SQLASTVisitor {
    boolean visit(ImpalaCreateTableStatement x);
    void endVisit(ImpalaCreateTableStatement x);

    boolean visit(ImpalaMultiInsertStatement x);
    void endVisit(ImpalaMultiInsertStatement x);

    boolean visit(ImpalaInsertStatement x);
    void endVisit(ImpalaInsertStatement x);

    boolean visit(ImpalaInsert x);
    void endVisit(ImpalaInsert x);

    boolean visit(ImpalaMetaStatement x);
    void endVisit(ImpalaMetaStatement x);
}
