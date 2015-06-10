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
package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsert;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsertStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSetLabelStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowPartitionsStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowStatisticStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;


public class OdpsASTVisitorAdapter extends SQLASTVisitorAdapter implements OdpsASTVisitor {

    @Override
    public void endVisit(OdpsCreateTableStatement x) {
        
    }

    @Override
    public boolean visit(OdpsCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsInsertStatement x) {
        
    }

    @Override
    public boolean visit(OdpsInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsInsert x) {
        
    }

    @Override
    public boolean visit(OdpsInsert x) {
        return true;
    }

    @Override
    public void endVisit(OdpsUDTFSQLSelectItem x) {
        
    }

    @Override
    public boolean visit(OdpsUDTFSQLSelectItem x) {
        return true;
    }

    @Override
    public void endVisit(OdpsShowPartitionsStmt x) {
        
    }

    @Override
    public boolean visit(OdpsShowPartitionsStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsShowStatisticStmt x) {
        
    }

    @Override
    public boolean visit(OdpsShowStatisticStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsSetLabelStatement x) {
        
    }

    @Override
    public boolean visit(OdpsSetLabelStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsSelectQueryBlock x) {
        
    }

    @Override
    public boolean visit(OdpsSelectQueryBlock x) {
        return true;
    }

}
