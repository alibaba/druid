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
package com.alibaba.druid.sql.dialect.oscar.visitor;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oscar.ast.OscarTop;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface OscarASTVisitor extends SQLASTVisitor {
    default void endVisit(OscarSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(OscarSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(OscarSelectQueryBlock.FetchClause x) {
    }

    default boolean visit(OscarSelectQueryBlock.FetchClause x) {
        return true;
    }

    default void endVisit(OscarSelectQueryBlock.ForClause x) {
    }

    default boolean visit(OscarSelectQueryBlock.ForClause x) {
        return true;
    }

    default void endVisit(OscarDeleteStatement x) {
    }

    default boolean visit(OscarDeleteStatement x) {
        return true;
    }

    default void endVisit(OscarInsertStatement x) {}

    default boolean visit(OscarInsertStatement x) {
        return true;
    }

    default void endVisit(OscarSelectStatement x) {
        endVisit((SQLSelectStatement) x);
    }

    default boolean visit(OscarSelectStatement x) {
        return visit((SQLSelectStatement) x);
    }

    default void endVisit(OscarUpdateStatement x) {
    }

    default boolean visit(OscarUpdateStatement x) {
        return true;
    }

    default void endVisit(OscarFunctionTableSource x) {
    }

    default boolean visit(OscarFunctionTableSource x) {
        return true;
    }

    default void endVisit(OscarShowStatement x) {
    }

    default boolean visit(OscarShowStatement x) {
        return true;
    }

    default void endVisit(OscarStartTransactionStatement x) {
    }

    default boolean visit(OscarStartTransactionStatement x) {
        return true;
    }

    default void endVisit(OscarConnectToStatement x) {
    }

    default boolean visit(OscarConnectToStatement x) {
        return true;
    }

    default void endVisit(OscarCreateSchemaStatement x) {
    }

    default boolean visit(OscarCreateSchemaStatement x) {
        return true;
    }

    default void endVisit(OscarDropSchemaStatement x) {
    }

    default boolean visit(OscarDropSchemaStatement x) {
        return true;
    }

    default void endVisit(OscarAlterSchemaStatement x) {
    }

    default boolean visit(OscarAlterSchemaStatement x) {
        return true;
    }

    default boolean visit(OscarTop x) {
        return true;
    }

    default void endVisit(OscarTop x) {}
}
