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
package com.alibaba.druid.sql.dialect.sqlserver.visitor;

import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement.SQLServerParameter;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface SQLServerASTVisitor extends SQLASTVisitor {

    default boolean visit(SQLServerSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(SQLServerSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(SQLServerTop x) {
        return true;
    }

    default void endVisit(SQLServerTop x) {}
    
    default boolean visit(SQLServerObjectReferenceExpr x) {
        return true;
    }
    
    default void endVisit(SQLServerObjectReferenceExpr x) {}
    
    default boolean visit(SQLServerInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }
    
    default void endVisit(SQLServerInsertStatement x) {
        endVisit((SQLInsertStatement) x);
    }

    default boolean visit(SQLServerUpdateStatement x) {
        return true;
    }
    
    default void endVisit(SQLServerUpdateStatement x) {}
    
    default boolean visit(SQLServerExecStatement x) {
        return true;
    }
    
    default void endVisit(SQLServerExecStatement x) {}
    
    default boolean visit(SQLServerSetTransactionIsolationLevelStatement x) {
        return true;
    }

    default void endVisit(SQLServerSetTransactionIsolationLevelStatement x) {}

    default boolean visit(SQLServerOutput x) {
        return true;
    }

    default void endVisit(SQLServerOutput x) {}

    default boolean visit(SQLServerRollbackStatement x) {
        return true;
    }

    default void endVisit(SQLServerRollbackStatement x) {}
    
    default boolean visit(SQLServerWaitForStatement x) {
        return true;
    }

    default void endVisit(SQLServerWaitForStatement x) {}
    
    default boolean visit(SQLServerParameter x) {
        return true;
    }

    default void endVisit(SQLServerParameter x) {}

}
