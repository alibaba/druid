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
package com.alibaba.druid.sql.dialect.sqlserver.visitor;

import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerColumnDefinition;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerDeclareItem;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelect;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerBlockStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerCommitStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerDeclareStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerIfStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerIfStatement.Else;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerRollbackStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerSetStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerSetTransactionIsolationLevelStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerWaitForStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class SQLServerASTVisitorAdapter extends SQLASTVisitorAdapter implements SQLServerASTVisitor {

    @Override
    public boolean visit(SQLServerSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerSelectQueryBlock x) {

    }

    @Override
    public boolean visit(SQLServerTop x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerTop x) {

    }

    @Override
    public boolean visit(SQLServerObjectReferenceExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerObjectReferenceExpr x) {

    }

    @Override
    public boolean visit(SQLServerInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerInsertStatement x) {

    }

    @Override
    public boolean visit(SQLServerUpdateStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerUpdateStatement x) {

    }

    @Override
    public boolean visit(SQLServerColumnDefinition.Identity x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerColumnDefinition.Identity x) {

    }

    @Override
    public boolean visit(SQLServerColumnDefinition x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerColumnDefinition x) {

    }

    @Override
    public boolean visit(SQLServerExecStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerExecStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerSetTransactionIsolationLevelStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerSetTransactionIsolationLevelStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerSetStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerSetStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerOutput x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerOutput x) {

    }

    @Override
    public boolean visit(SQLServerDeclareItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerDeclareItem x) {

    }

    @Override
    public boolean visit(SQLServerDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerDeclareStatement x) {

    }

    @Override
    public boolean visit(Else x) {
        return true;
    }

    @Override
    public void endVisit(Else x) {

    }

    @Override
    public boolean visit(SQLServerIfStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerIfStatement x) {

    }

    @Override
    public boolean visit(SQLServerBlockStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerBlockStatement x) {

    }
    
    @Override
    public boolean visit(SQLServerSelect x) {
        return true;
    }
    
    @Override
    public void endVisit(SQLServerSelect x) {
        
    }

    @Override
    public boolean visit(SQLServerCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerCommitStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerRollbackStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerRollbackStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerWaitForStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLServerWaitForStatement x) {
        
    }

}
