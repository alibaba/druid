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
package com.alibaba.druid.sql.dialect.oracle.visitor;

import com.alibaba.druid.sql.ast.expr.SQLObjectCreateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeInterval;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.VersionsFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.GroupingSetExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SubqueryFactoringClause;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleArrayAccessExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExtractExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleGroupComparisonCondition;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleGroupingSetsExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OraclePriorExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTableCollectionExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintNull;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintState;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleRefDataType;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleReferencesConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectForUpdate;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectHierachicalQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableTypeDef;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListMultiColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListSingleColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetValueClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface OracleASTVisitor extends SQLASTVisitor {

    void endVisit(OracleAggregateExpr astNode);

    void endVisit(OracleConstraintState astNode);

    void endVisit(OraclePLSQLCommitStatement astNode);

    void endVisit(OracleReferencesConstaint astNode);

    void endVisit(OracleConstraintNull astNode);

    void endVisit(OracleAnalytic x);

    void endVisit(OracleAnalyticWindowing x);

    void endVisit(OracleArrayAccessExpr x);

    void endVisit(OracleDataTypeInterval x);

    void endVisit(OracleRefDataType x);

    void endVisit(OracleDateExpr x);

    void endVisit(OracleDbLinkExpr x);

    void endVisit(OracleDeleteStatement x);

    void endVisit(OracleExtractExpr x);

    void endVisit(OracleGroupComparisonCondition x);

    void endVisit(OracleGroupingSetsExpr x);

    void endVisit(OracleHint x);

    void endVisit(OracleIntervalExpr x);

    void endVisit(SQLObjectCreateExpr x);

    void endVisit(OracleOrderBy x);

    void endVisit(OracleOuterExpr x);

    void endVisit(OracleSelectForUpdate x);

    void endVisit(OracleSelectHierachicalQueryClause x);

    void endVisit(OracleSelectJoin x);

    void endVisit(OracleOrderByItem x);

    void endVisit(OracleSelectPivot x);

    void endVisit(OracleSelectPivot.Item x);

    void endVisit(OracleSelectRestriction.CheckOption x);

    void endVisit(OracleSelectRestriction.ReadOnly x);

    void endVisit(OracleSelectSubqueryTableSource x);

    void endVisit(OracleSelectUnPivot x);

    void endVisit(OracleTableCollectionExpr x);

    void endVisit(OracleTableExpr x);

    void endVisit(OracleTableTypeDef x);

    void endVisit(OracleTimestampExpr x);

    void endVisit(OracleUpdateSetListClause x);

    void endVisit(OracleUpdateSetListMultiColumnItem x);

    void endVisit(OracleUpdateSetListSingleColumnItem x);

    void endVisit(OracleUpdateSetValueClause x);

    void endVisit(OracleUpdateStatement x);

    boolean visit(OracleAggregateExpr astNode);

    boolean visit(OracleConstraintState astNode);

    boolean visit(OraclePLSQLCommitStatement astNode);

    boolean visit(OracleReferencesConstaint astNode);

    boolean visit(OracleConstraintNull astNode);

    boolean visit(OracleAnalytic x);

    boolean visit(OracleAnalyticWindowing x);

    boolean visit(OracleArrayAccessExpr x);

    boolean visit(OracleDataTypeInterval x);

    boolean visit(OracleRefDataType x);

    boolean visit(OracleDateExpr x);

    boolean visit(OracleDbLinkExpr x);

    boolean visit(OracleDeleteStatement x);

    boolean visit(OracleExtractExpr x);

    boolean visit(OracleGroupComparisonCondition x);

    boolean visit(OracleGroupingSetsExpr x);

    boolean visit(OracleHint x);

    boolean visit(OracleIntervalExpr x);

    boolean visit(SQLObjectCreateExpr x);

    boolean visit(OracleOrderBy x);

    boolean visit(OracleOuterExpr x);

    boolean visit(OracleSelectForUpdate x);

    boolean visit(OracleSelectHierachicalQueryClause x);

    boolean visit(OracleSelectJoin x);

    boolean visit(OracleOrderByItem x);

    boolean visit(OracleSelectPivot x);

    boolean visit(OracleSelectPivot.Item x);

    boolean visit(OracleSelectRestriction.CheckOption x);

    boolean visit(OracleSelectRestriction.ReadOnly x);

    boolean visit(OracleSelectSubqueryTableSource x);

    boolean visit(OracleSelectUnPivot x);

    boolean visit(OracleTableCollectionExpr x);

    boolean visit(OracleTableExpr x);

    boolean visit(OracleTableTypeDef x);

    boolean visit(OracleTimestampExpr x);

    boolean visit(OracleUpdateSetListClause x);

    boolean visit(OracleUpdateSetListMultiColumnItem x);

    boolean visit(OracleUpdateSetListSingleColumnItem x);

    boolean visit(OracleUpdateSetValueClause x);

    boolean visit(OracleUpdateStatement x);

    boolean visit(SampleClause x);

    void endVisit(SampleClause x);

    boolean visit(OracleSelectTableReference x);

    void endVisit(OracleSelectTableReference x);

    boolean visit(PartitionExtensionClause x);

    void endVisit(PartitionExtensionClause x);

    boolean visit(VersionsFlashbackQueryClause x);

    void endVisit(VersionsFlashbackQueryClause x);

    boolean visit(AsOfFlashbackQueryClause x);

    void endVisit(AsOfFlashbackQueryClause x);

    boolean visit(GroupingSetExpr x);

    void endVisit(GroupingSetExpr x);

    boolean visit(OraclePriorExpr x);

    void endVisit(OraclePriorExpr x);

    boolean visit(SubqueryFactoringClause x);

    void endVisit(SubqueryFactoringClause x);

    boolean visit(SubqueryFactoringClause.Entry x);

    void endVisit(SubqueryFactoringClause.Entry x);

    boolean visit(SearchClause x);

    void endVisit(SearchClause x);

    boolean visit(CycleClause x);

    void endVisit(CycleClause x);

    boolean visit(OracleBinaryFloatExpr x);

    void endVisit(OracleBinaryFloatExpr x);

    boolean visit(OracleBinaryDoubleExpr x);

    void endVisit(OracleBinaryDoubleExpr x);

    boolean visit(OracleSelect x);

    void endVisit(OracleSelect x);
}
