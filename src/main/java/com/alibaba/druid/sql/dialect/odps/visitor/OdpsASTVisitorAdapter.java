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
package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.*;
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
    public void endVisit(HiveInsert x) {
        
    }

    public boolean visit(HiveCreateTableStatement x) {
        return false;
    }

    public void endVisit(HiveCreateTableStatement x) {

    }

    public boolean visit(HiveMultiInsertStatement x) {
        return false;
    }

    public void endVisit(HiveMultiInsertStatement x) {

    }

    public boolean visit(HiveInsertStatement x) {
        return false;
    }

    public void endVisit(HiveInsertStatement x) {

    }

    @Override
    public boolean visit(HiveInsert x) {
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
    
    @Override
    public void endVisit(OdpsAnalyzeTableStatement x) {
        
    }
    
    @Override
    public boolean visit(OdpsAnalyzeTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsAddStatisticStatement x) {
        
    }

    @Override
    public boolean visit(OdpsAddStatisticStatement x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsRemoveStatisticStatement x) {
        
    }
    
    @Override
    public boolean visit(OdpsRemoveStatisticStatement x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsStatisticClause.TableCount x) {
        
    }
    
    @Override
    public boolean visit(OdpsStatisticClause.TableCount x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsStatisticClause.ExpressionCondition x) {
        
    }
    
    @Override
    public boolean visit(OdpsStatisticClause.ExpressionCondition x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsStatisticClause.NullValue x) {
        
    }
    
    @Override
    public boolean visit(OdpsStatisticClause.NullValue x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsStatisticClause.ColumnSum x) {
        
    }
    
    @Override
    public boolean visit(OdpsStatisticClause.ColumnSum x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsStatisticClause.ColumnMax x) {
        
    }
    
    @Override
    public boolean visit(OdpsStatisticClause.ColumnMax x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsStatisticClause.ColumnMin x) {
        
    }
    
    @Override
    public boolean visit(OdpsStatisticClause.ColumnMin x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsReadStatement x) {
        
    }
    
    @Override
    public boolean visit(OdpsReadStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsShowGrantsStmt x) {
        
    }

    @Override
    public boolean visit(OdpsShowGrantsStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsListStmt x) {
        
    }

    @Override
    public boolean visit(OdpsListStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsGrantStmt x) {
        
    }

    @Override
    public boolean visit(OdpsGrantStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsValuesTableSource x) {

    }

    @Override
    public boolean visit(OdpsValuesTableSource x) {
        return true;
    }
}
