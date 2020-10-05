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
package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.odps.ast.*;
import com.alibaba.druid.sql.repository.SchemaRepository;

public class OdpsSchemaStatVisitor extends HiveSchemaStatVisitor implements OdpsASTVisitor {

    public OdpsSchemaStatVisitor() {
        super(DbType.odps);
    }

    public OdpsSchemaStatVisitor(SchemaRepository repository) {
        super (repository);
    }

    @Override
    public void endVisit(OdpsCreateTableStatement x) {
        super.endVisit((SQLCreateTableStatement) x);
    }

    @Override
    public boolean visit(OdpsCreateTableStatement x) {
        return super.visit((SQLCreateTableStatement) x);
    }

    @Override
    public void endVisit(OdpsUDTFSQLSelectItem x) {

    }

    @Override
    public boolean visit(OdpsUDTFSQLSelectItem x) {
        return true;
    }

    @Override
    public void endVisit(OdpsSetLabelStatement x) {

    }

    @Override
    public boolean visit(OdpsSetLabelStatement x) {
        if (x.getTable() != null) {
            x.getTable().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OdpsSelectQueryBlock x) {
        super.endVisit((SQLSelectQueryBlock) x);
    }

    @Override
    public boolean visit(OdpsSelectQueryBlock x) {
        return this.visit((SQLSelectQueryBlock) x);
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
    public void endVisit(OdpsStatisticClause.DistinctValue x) {

    }

    @Override
    public boolean visit(OdpsStatisticClause.DistinctValue x) {
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
        super.endVisit((SQLGrantStatement) x);
    }

    @Override
    public boolean visit(OdpsGrantStmt x) {
        return super.visit((SQLGrantStatement) x);
    }

    @Override
    public boolean visit(OdpsAddTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsAddTableStatement x) {

    }

    @Override
    public boolean visit(OdpsAddFileStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsAddFileStatement x) {

    }

    @Override
    public boolean visit(OdpsAddUserStatement x) {
        return false;
    }

    @Override
    public void endVisit(OdpsAddUserStatement x) {

    }

    @Override
    public boolean visit(OdpsRemoveUserStatement x) {
        return false;
    }

    @Override
    public void endVisit(OdpsRemoveUserStatement x) {

    }

    @Override
    public boolean visit(OdpsAlterTableSetChangeLogs x) {
        return false;
    }

    @Override
    public void endVisit(OdpsAlterTableSetChangeLogs x) {

    }

    @Override
    public boolean visit(OdpsCountStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsCountStatement x) {

    }

    @Override
    public boolean visit(OdpsQueryAliasStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsQueryAliasStatement x) {

    }

    @Override
    public boolean visit(OdpsTransformExpr x) {
        return true;
    }

    @Override
    public void endVisit(OdpsTransformExpr x) {

    }

    @Override
    public boolean visit(OdpsExstoreStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsExstoreStatement x) {

    }

}
