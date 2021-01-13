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
package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.*;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface PGASTVisitor extends SQLASTVisitor {

    default void endVisit(PGSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(PGSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(PGSelectQueryBlock.FetchClause x) {
    }

    default boolean visit(PGSelectQueryBlock.FetchClause x) {
        return true;
    }

    default void endVisit(PGSelectQueryBlock.ForClause x) {
    }

    default boolean visit(PGSelectQueryBlock.ForClause x) {
        return true;
    }

    default void endVisit(PGDeleteStatement x) {
    }

    default boolean visit(PGDeleteStatement x) {
        return true;
    }

    default void endVisit(PGInsertStatement x) {}

    default boolean visit(PGInsertStatement x) {
        return true;
    }

    default void endVisit(PGSelectStatement x) {
        endVisit((SQLSelectStatement) x);
    }

    default boolean visit(PGSelectStatement x) {
        return visit((SQLSelectStatement) x);
    }

    default void endVisit(PGUpdateStatement x) {
    }

    default boolean visit(PGUpdateStatement x) {
        return true;
    }

    default void endVisit(PGFunctionTableSource x) {
    }

    default boolean visit(PGFunctionTableSource x) {
        return true;
    }

    default void endVisit(PGTypeCastExpr x) {
    }

    default boolean visit(PGTypeCastExpr x) {
        return true;
    }

    default void endVisit(PGExtractExpr x) {
    }

    default boolean visit(PGExtractExpr x) {
        return true;
    }

    default void endVisit(PGBoxExpr x) {
    }

    default boolean visit(PGBoxExpr x) {
        return true;
    }

    default void endVisit(PGPointExpr x) {
    }

    default boolean visit(PGPointExpr x) {
        return true;
    }

    default void endVisit(PGMacAddrExpr x) {
    }

    default boolean visit(PGMacAddrExpr x) {
        return true;
    }

    default void endVisit(PGInetExpr x) {
    }

    default boolean visit(PGInetExpr x) {
        return true;
    }

    default void endVisit(PGCidrExpr x) {
    }

    default boolean visit(PGCidrExpr x) {
        return true;
    }

    default void endVisit(PGPolygonExpr x) {
    }

    default boolean visit(PGPolygonExpr x) {
        return true;
    }

    default void endVisit(PGCircleExpr x) {
    }

    default boolean visit(PGCircleExpr x) {
        return true;
    }

    default void endVisit(PGLineSegmentsExpr x) {
    }

    default boolean visit(PGLineSegmentsExpr x) {
        return true;
    }

    default void endVisit(PGShowStatement x) {
    }

    default boolean visit(PGShowStatement x) {
        return true;
    }

    default void endVisit(PGStartTransactionStatement x) {
    }

    default boolean visit(PGStartTransactionStatement x) {
        return true;
    }

    default void endVisit(PGConnectToStatement x) {
    }

    default boolean visit(PGConnectToStatement x) {
        return true;
    }

    default void endVisit(PGCreateSchemaStatement x) {
    }

    default boolean visit(PGCreateSchemaStatement x) {
        return true;
    }

    default void endVisit(PGDropSchemaStatement x) {
    }

    default boolean visit(PGDropSchemaStatement x) {
        return true;
    }

    default void endVisit(PGAlterSchemaStatement x) {
    }

    default boolean visit(PGAlterSchemaStatement x) {
        return true;
    }

}
