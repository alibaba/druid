package com.alibaba.druid.sql.dialect.oracle.ast.visitor;

import com.alibaba.druid.sql.ast.expr.SQLObjectCreateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeInterval;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleArrayAccessExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExtractExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleGroupComparisonCondition;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleGroupingSetsExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OraclePriorIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTableCollectionExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintNull;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintState;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleRefDataType;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleReferencesConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectForUpdate;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectHierachicalQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableColumn;
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

    void endVisit(OracleAlterTableStatement.ModifyColumnClause astNode);

    void endVisit(OracleAlterTableStatement.DropColumn astNode);

    void endVisit(OracleAlterTableStatement.RenameColumn astNode);

    void endVisit(OracleAlterTableStatement.DeallocateClause astNode);

    void endVisit(OracleAlterTableStatement.EnableTrigger astNode);

    void endVisit(OracleAlterTableStatement.RenameConstaint astNode);

    void endVisit(OracleAlterTableStatement.AddConstraint astNode);

    void endVisit(OracleAlterTableStatement.ModifyConstaint astNode);

    void endVisit(OracleConstraintState astNode);

    void endVisit(OracleAlterTableStatement astNode);

    void endVisit(OracleAlterTableStatement.EnableClause astNode);

    void endVisit(OracleAlterTableStatement.ParallelClause astNode);

    void endVisit(OracleAlterTableStatement.NoParallelClause astNode);

    void endVisit(OracleAlterTableStatement.ModifyCollectionRetrieval astNode);

    void endVisit(OracleAlterTableStatement.AddColumnClause astNode);

    void endVisit(OracleForeignKey astNode);

    void endVisit(OraclePLSQLCommitStatement astNode);

    void endVisit(OracleReferencesConstaint astNode);

    void endVisit(OracleConstraintNull astNode);

    void endVisit(OracleCheck astNode);

    void endVisit(OraclePrimaryKey astNode);

    void endVisit(OracleAnalytic x);

    void endVisit(OracleAnalyticWindowing x);

    void endVisit(OracleArrayAccessExpr x);

    void endVisit(OracleCreateViewStatement x);

    void endVisit(OracleDataTypeInterval x);

    void endVisit(OracleRefDataType x);

    void endVisit(OracleDateExpr x);

    void endVisit(OracleDbLinkExpr x);

    void endVisit(OracleDeleteStatement x);

    void endVisit(OracleExtractExpr x);

    void endVisit(OracleGroupComparisonCondition x);

    void endVisit(OracleGroupingSetsExpr x);

    void endVisit(OracleHint x);

    void endVisit(OracleInsertStatement x);

    void endVisit(OracleInsertStatement.InsertSource x);

    void endVisit(OracleInsertStatement.Into x);

    void endVisit(OracleInsertStatement.IntoSubQuery x);

    void endVisit(OracleInsertStatement.IntoValues x);

    void endVisit(OracleInsertStatement.SigleTableInert x);

    void endVisit(OracleIntervalExpr x);

    void endVisit(SQLObjectCreateExpr x);

    void endVisit(OracleOrderBy x);

    void endVisit(OracleOuterExpr x);

    void endVisit(OraclePriorIdentifierExpr x);

    void endVisit(OracleSelectForUpdate x);

    void endVisit(OracleSelectHierachicalQueryClause x);

    void endVisit(OracleSelectJoin x);

    void endVisit(OracleSelectOrderByItem x);

    void endVisit(OracleSelectPivot x);

    void endVisit(OracleSelectPivot.Item x);

    void endVisit(OracleSelectRestriction.CheckOption x);

    void endVisit(OracleSelectRestriction.ReadOnly x);

    void endVisit(OracleSelectSubqueryTableSource x);

    void endVisit(OracleSelectUnPivot x);

    void endVisit(OracleTableCollectionExpr x);

    void endVisit(OracleTableColumn x);

    void endVisit(OracleTableExpr x);

    void endVisit(OracleTableTypeDef x);

    void endVisit(OracleTimestampExpr x);

    void endVisit(OracleUpdateSetListClause x);

    void endVisit(OracleUpdateSetListMultiColumnItem x);

    void endVisit(OracleUpdateSetListSingleColumnItem x);

    void endVisit(OracleUpdateSetValueClause x);

    void endVisit(OracleUpdateStatement x);

    boolean visit(OracleAggregateExpr astNode);

    boolean visit(OracleAlterTableStatement.ModifyColumnClause astNode);

    boolean visit(OracleAlterTableStatement.DropColumn astNode);

    boolean visit(OracleAlterTableStatement.RenameColumn astNode);

    boolean visit(OracleAlterTableStatement.DeallocateClause astNode);

    boolean visit(OracleAlterTableStatement.EnableTrigger astNode);

    boolean visit(OracleAlterTableStatement.RenameConstaint astNode);

    boolean visit(OracleAlterTableStatement.AddConstraint astNode);

    boolean visit(OracleAlterTableStatement.ModifyConstaint astNode);

    boolean visit(OracleConstraintState astNode);

    boolean visit(OracleAlterTableStatement astNode);

    boolean visit(OracleAlterTableStatement.EnableClause astNode);

    boolean visit(OracleAlterTableStatement.ParallelClause astNode);

    boolean visit(OracleAlterTableStatement.NoParallelClause astNode);

    boolean visit(OracleAlterTableStatement.ModifyCollectionRetrieval astNode);

    boolean visit(OracleAlterTableStatement.AddColumnClause astNode);

    boolean visit(OracleForeignKey astNode);

    boolean visit(OraclePLSQLCommitStatement astNode);

    boolean visit(OracleReferencesConstaint astNode);

    boolean visit(OracleConstraintNull astNode);

    boolean visit(OracleCheck astNode);

    boolean visit(OraclePrimaryKey astNode);

    boolean visit(OracleAnalytic x);

    boolean visit(OracleAnalyticWindowing x);

    boolean visit(OracleArrayAccessExpr x);

    boolean visit(OracleCreateViewStatement x);

    boolean visit(OracleDataTypeInterval x);

    boolean visit(OracleRefDataType x);

    boolean visit(OracleDateExpr x);

    boolean visit(OracleDbLinkExpr x);

    boolean visit(OracleDeleteStatement x);

    boolean visit(OracleExtractExpr x);

    boolean visit(OracleGroupComparisonCondition x);

    boolean visit(OracleGroupingSetsExpr x);

    boolean visit(OracleHint x);

    boolean visit(OracleInsertStatement x);

    boolean visit(OracleInsertStatement.InsertSource x);

    boolean visit(OracleInsertStatement.Into x);

    boolean visit(OracleInsertStatement.IntoSubQuery x);

    boolean visit(OracleInsertStatement.IntoValues x);

    boolean visit(OracleInsertStatement.SigleTableInert x);

    boolean visit(OracleIntervalExpr x);

    boolean visit(SQLObjectCreateExpr x);

    boolean visit(OracleOrderBy x);

    boolean visit(OracleOuterExpr x);

    boolean visit(OraclePriorIdentifierExpr x);

    boolean visit(OracleSelectForUpdate x);

    boolean visit(OracleSelectHierachicalQueryClause x);

    boolean visit(OracleSelectJoin x);

    boolean visit(OracleSelectOrderByItem x);

    boolean visit(OracleSelectPivot x);

    boolean visit(OracleSelectPivot.Item x);

    boolean visit(OracleSelectRestriction.CheckOption x);

    boolean visit(OracleSelectRestriction.ReadOnly x);

    boolean visit(OracleSelectSubqueryTableSource x);

    boolean visit(OracleSelectUnPivot x);

    boolean visit(OracleTableCollectionExpr x);

    boolean visit(OracleTableColumn x);

    boolean visit(OracleTableExpr x);

    boolean visit(OracleTableTypeDef x);

    boolean visit(OracleTimestampExpr x);

    boolean visit(OracleUpdateSetListClause x);

    boolean visit(OracleUpdateSetListMultiColumnItem x);

    boolean visit(OracleUpdateSetListSingleColumnItem x);

    boolean visit(OracleUpdateSetValueClause x);

    boolean visit(OracleUpdateStatement x);

}
