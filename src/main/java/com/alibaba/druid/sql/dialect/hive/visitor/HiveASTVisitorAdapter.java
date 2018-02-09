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
package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class HiveASTVisitorAdapter extends SQLASTVisitorAdapter implements HiveASTVisitor {
    @Override
    public boolean visit(HiveCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveCreateTableStatement x) {

    }

    @Override
    public boolean visit(HiveMultiInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveMultiInsertStatement x) {

    }

    @Override
    public boolean visit(HiveInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveInsertStatement x) {

    }

    @Override
    public boolean visit(HiveInsert x) {
        return true;
    }

    @Override
    public void endVisit(HiveInsert x) {

    }
}
