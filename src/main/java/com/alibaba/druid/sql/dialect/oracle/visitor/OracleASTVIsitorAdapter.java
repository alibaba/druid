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
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SubqueryFactoringClause.Entry;
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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
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
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class OracleASTVIsitorAdapter extends SQLASTVisitorAdapter implements OracleASTVisitor {

    public boolean visit(OracleSelect x) {
        return true;
    }

    public void endVisit(OracleSelect x) {
    }

    @Override
    public void endVisit(OracleAggregateExpr astNode) {

    }

    @Override
    public void endVisit(OracleConstraintState astNode) {

    }

    @Override
    public void endVisit(OraclePLSQLCommitStatement astNode) {

    }

    @Override
    public void endVisit(OracleReferencesConstaint astNode) {

    }

    @Override
    public void endVisit(OracleConstraintNull astNode) {

    }

    @Override
    public void endVisit(OracleAnalytic x) {

    }

    @Override
    public void endVisit(OracleAnalyticWindowing x) {

    }

    @Override
    public void endVisit(OracleArrayAccessExpr x) {

    }

    @Override
    public void endVisit(OracleDataTypeInterval x) {

    }

    @Override
    public void endVisit(OracleRefDataType x) {

    }

    @Override
    public void endVisit(OracleDateExpr x) {

    }

    @Override
    public void endVisit(OracleDbLinkExpr x) {

    }

    @Override
    public void endVisit(OracleDeleteStatement x) {

    }

    @Override
    public void endVisit(OracleExtractExpr x) {

    }

    @Override
    public void endVisit(OracleGroupComparisonCondition x) {

    }

    @Override
    public void endVisit(OracleGroupingSetsExpr x) {

    }

    @Override
    public void endVisit(OracleHint x) {

    }

    @Override
    public void endVisit(OracleIntervalExpr x) {

    }

    @Override
    public void endVisit(SQLObjectCreateExpr x) {

    }

    @Override
    public void endVisit(OracleOrderBy x) {

    }

    @Override
    public void endVisit(OracleOuterExpr x) {

    }

    @Override
    public void endVisit(OracleSelectForUpdate x) {

    }

    @Override
    public void endVisit(OracleSelectHierachicalQueryClause x) {

    }

    @Override
    public void endVisit(OracleSelectJoin x) {

    }

    @Override
    public void endVisit(OracleOrderByItem x) {

    }

    @Override
    public void endVisit(OracleSelectPivot x) {

    }

    @Override
    public void endVisit(Item x) {

    }

    @Override
    public void endVisit(CheckOption x) {

    }

    @Override
    public void endVisit(ReadOnly x) {

    }

    @Override
    public void endVisit(OracleSelectSubqueryTableSource x) {

    }

    @Override
    public void endVisit(OracleSelectUnPivot x) {

    }

    @Override
    public void endVisit(OracleTableCollectionExpr x) {

    }

    @Override
    public void endVisit(OracleTableExpr x) {

    }

    @Override
    public void endVisit(OracleTableTypeDef x) {

    }

    @Override
    public void endVisit(OracleTimestampExpr x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListClause x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListMultiColumnItem x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListSingleColumnItem x) {

    }

    @Override
    public void endVisit(OracleUpdateSetValueClause x) {

    }

    @Override
    public void endVisit(OracleUpdateStatement x) {

    }

    @Override
    public boolean visit(OracleAggregateExpr astNode) {

        return true;
    }

    @Override
    public boolean visit(OracleConstraintState astNode) {

        return true;
    }

    @Override
    public boolean visit(OraclePLSQLCommitStatement astNode) {

        return true;
    }

    @Override
    public boolean visit(OracleReferencesConstaint astNode) {

        return true;
    }

    @Override
    public boolean visit(OracleConstraintNull astNode) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalytic x) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalyticWindowing x) {

        return true;
    }

    @Override
    public boolean visit(OracleArrayAccessExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleDataTypeInterval x) {

        return true;
    }

    @Override
    public boolean visit(OracleRefDataType x) {

        return true;
    }

    @Override
    public boolean visit(OracleDateExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleDbLinkExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {

        return true;
    }

    @Override
    public boolean visit(OracleExtractExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleGroupComparisonCondition x) {

        return true;
    }

    @Override
    public boolean visit(OracleGroupingSetsExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleHint x) {

        return true;
    }

    @Override
    public boolean visit(OracleIntervalExpr x) {

        return true;
    }

    @Override
    public boolean visit(SQLObjectCreateExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleOrderBy x) {

        return true;
    }

    @Override
    public boolean visit(OracleOuterExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectForUpdate x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectHierachicalQueryClause x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {

        return true;
    }

    @Override
    public boolean visit(OracleOrderByItem x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectPivot x) {

        return true;
    }

    @Override
    public boolean visit(Item x) {

        return true;
    }

    @Override
    public boolean visit(CheckOption x) {

        return true;
    }

    @Override
    public boolean visit(ReadOnly x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectSubqueryTableSource x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectUnPivot x) {

        return true;
    }

    @Override
    public boolean visit(OracleTableCollectionExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleTableExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleTableTypeDef x) {

        return true;
    }

    @Override
    public boolean visit(OracleTimestampExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleUpdateSetListClause x) {

        return true;
    }

    @Override
    public boolean visit(OracleUpdateSetListMultiColumnItem x) {

        return true;
    }

    @Override
    public boolean visit(OracleUpdateSetListSingleColumnItem x) {

        return true;
    }

    @Override
    public boolean visit(OracleUpdateSetValueClause x) {

        return true;
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {

        return true;
    }

    @Override
    public boolean visit(SampleClause x) {

        return true;
    }

    @Override
    public void endVisit(SampleClause x) {

    }

    @Override
    public boolean visit(OracleSelectTableReference x) {

        return true;
    }

    @Override
    public void endVisit(OracleSelectTableReference x) {

    }

    @Override
    public boolean visit(PartitionExtensionClause x) {

        return true;
    }

    @Override
    public void endVisit(PartitionExtensionClause x) {

    }

    @Override
    public boolean visit(VersionsFlashbackQueryClause x) {

        return true;
    }

    @Override
    public void endVisit(VersionsFlashbackQueryClause x) {

    }

    @Override
    public boolean visit(AsOfFlashbackQueryClause x) {

        return true;
    }

    @Override
    public void endVisit(AsOfFlashbackQueryClause x) {

    }

    @Override
    public boolean visit(GroupingSetExpr x) {

        return true;
    }

    @Override
    public void endVisit(GroupingSetExpr x) {

    }

    @Override
    public boolean visit(OraclePriorExpr x) {

        return true;
    }

    @Override
    public void endVisit(OraclePriorExpr x) {

    }

    @Override
    public boolean visit(SubqueryFactoringClause x) {

        return true;
    }

    @Override
    public void endVisit(SubqueryFactoringClause x) {

    }

    @Override
    public boolean visit(Entry x) {

        return true;
    }

    @Override
    public void endVisit(Entry x) {

    }

    @Override
    public boolean visit(SearchClause x) {

        return true;
    }

    @Override
    public void endVisit(SearchClause x) {

    }

    @Override
    public boolean visit(CycleClause x) {

        return true;
    }

    @Override
    public void endVisit(CycleClause x) {

    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryFloatExpr x) {

    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryDoubleExpr x) {

    }

}
