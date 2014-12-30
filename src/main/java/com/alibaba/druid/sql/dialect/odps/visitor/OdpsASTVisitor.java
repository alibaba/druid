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
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface OdpsASTVisitor extends SQLASTVisitor {

    void endVisit(OdpsCreateTableStatement x);

    boolean visit(OdpsCreateTableStatement x);

    void endVisit(OdpsInsertStatement x);

    boolean visit(OdpsInsertStatement x);
    
    void endVisit(OdpsInsert x);
    
    boolean visit(OdpsInsert x);
    
    void endVisit(OdpsUDTFSQLSelectItem x);
    
    boolean visit(OdpsUDTFSQLSelectItem x);
    
    void endVisit(OdpsShowPartitionsStmt x);
    
    boolean visit(OdpsShowPartitionsStmt x);
    
    void endVisit(OdpsShowStatisticStmt x);
    
    boolean visit(OdpsShowStatisticStmt x);
    
    void endVisit(OdpsSetLabelStatement x);
    
    boolean visit(OdpsSetLabelStatement x);
    
    void endVisit(OdpsSelectQueryBlock x);
    
    boolean visit(OdpsSelectQueryBlock x);
}
