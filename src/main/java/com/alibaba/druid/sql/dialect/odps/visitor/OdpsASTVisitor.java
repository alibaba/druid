/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") {
        return true;
    }
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

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.dialect.odps.ast.*;

public interface OdpsASTVisitor extends HiveASTVisitor {

    default void endVisit(OdpsCreateTableStatement x) {
        endVisit((SQLCreateTableStatement) x);
    }

    default boolean visit(OdpsCreateTableStatement x) {
        return visit((SQLCreateTableStatement) x);
    }

    default void endVisit(OdpsUDTFSQLSelectItem x) {

    }

    default boolean visit(OdpsUDTFSQLSelectItem x) {
        return true;
    }

    default void endVisit(OdpsSetLabelStatement x) {

    }

    default boolean visit(OdpsSetLabelStatement x) {
        return true;
    }

    default void endVisit(OdpsSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(OdpsSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(OdpsAddStatisticStatement x) {

    }

    default boolean visit(OdpsAddStatisticStatement x) {
        return true;
    }

    default void endVisit(OdpsRemoveStatisticStatement x) {

    }

    default boolean visit(OdpsRemoveStatisticStatement x) {
        return true;
    }

    default void endVisit(OdpsStatisticClause.TableCount x) {

    }

    default boolean visit(OdpsStatisticClause.TableCount x) {
        return true;
    }

    default void endVisit(OdpsStatisticClause.ExpressionCondition x) {

    }

    default boolean visit(OdpsStatisticClause.ExpressionCondition x) {
        return true;
    }

    default void endVisit(OdpsStatisticClause.NullValue x) {

    }

    default boolean visit(OdpsStatisticClause.NullValue x) {
        return true;
    }

    default void endVisit(OdpsStatisticClause.DistinctValue x) {

    }

    default boolean visit(OdpsStatisticClause.DistinctValue x) {
        return true;
    }

    default void endVisit(OdpsStatisticClause.ColumnSum x) {

    }

    default boolean visit(OdpsStatisticClause.ColumnSum x) {
        return true;
    }

    default void endVisit(OdpsStatisticClause.ColumnMax x) {

    }

    default boolean visit(OdpsStatisticClause.ColumnMax x) {
        return true;
    }

    default void endVisit(OdpsStatisticClause.ColumnMin x) {

    }

    default boolean visit(OdpsStatisticClause.ColumnMin x) {
        return true;
    }

    default void endVisit(OdpsReadStatement x) {

    }

    default boolean visit(OdpsReadStatement x) {
        return true;
    }

    default void endVisit(OdpsShowGrantsStmt x) {

    }

    default boolean visit(OdpsShowGrantsStmt x) {
        return true;
    }

    default void endVisit(OdpsListStmt x) {

    }

    default boolean visit(OdpsListStmt x) {
        return true;
    }

    default void endVisit(OdpsGrantStmt x) {
        endVisit((SQLGrantStatement) x);
    }

    default boolean visit(OdpsGrantStmt x) {
        return visit((SQLGrantStatement) x);
    }

    default boolean visit(OdpsAddTableStatement x) {
        return true;
    }

    default void endVisit(OdpsAddTableStatement x) {
    }

    default boolean visit(OdpsAddFileStatement x) {
        return true;
    }

    default void endVisit(OdpsAddFileStatement x) {

    }

    default boolean visit(OdpsAddUserStatement x) {
        return true;
    }

    default void endVisit(OdpsAddUserStatement x) {

    }

    default boolean visit(OdpsRemoveUserStatement x) {
        return true;
    }

    default void endVisit(OdpsRemoveUserStatement x) {

    }

    default boolean visit(OdpsAlterTableSetChangeLogs x) {
        return true;
    }

    default void endVisit(OdpsAlterTableSetChangeLogs x) {

    }

    default boolean visit(OdpsCountStatement x) {
        return true;
    }

    default void endVisit(OdpsCountStatement x) {

    }

    default boolean visit(OdpsQueryAliasStatement x) {
        return true;
    }

    default void endVisit(OdpsQueryAliasStatement x) {

    }

    default boolean visit(OdpsTransformExpr x) {
        return true;
    }

    default void endVisit(OdpsTransformExpr x) {

    }

    default boolean visit(OdpsExstoreStatement x) {
        return true;
    }

    default void endVisit(OdpsExstoreStatement x) {

    }

}
