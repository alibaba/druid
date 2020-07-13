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

import com.alibaba.druid.sql.dialect.impala.ast.ImpalaInsert;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaInsertStatement;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaMultiInsertStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaMetaStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaUpdateStatements;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class ImpalaASTVisitorAdapter extends SQLASTVisitorAdapter implements ImpalaASTVisitor {
    @Override
    public boolean visit(ImpalaCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(ImpalaCreateTableStatement x) {

    }

    @Override
    public boolean visit(ImpalaMultiInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(ImpalaMultiInsertStatement x) {

    }

    @Override
    public boolean visit(ImpalaInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(ImpalaInsertStatement x) {

    }

    @Override
    public boolean visit(ImpalaInsert x) {
        return true;
    }

    @Override
    public void endVisit(ImpalaInsert x) {

    }

    @Override
    public boolean visit(ImpalaMetaStatement x) {
        return true;
    }

    @Override
    public void endVisit(ImpalaMetaStatement x) {

    }

    @Override
    public boolean visit(ImpalaUpdateStatements x) {
        return true;
    }

    @Override
    public void endVisit(ImpalaUpdateStatements x) {

    }
}
