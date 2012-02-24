package com.alibaba.druid.sql.dialect.oracle.visitor;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLObjectCreateExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.VersionsFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.GroupingSetExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignment;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignmentItem;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.MainModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumnClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelRulesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.QueryPartitionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ReturnRowsClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SubqueryFactoringClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SubqueryFactoringClause.Entry;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExtractExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintState;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.ErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeUpdateClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectForUpdate;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectHierachicalQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListMultiColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListSingleColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetValueClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Mode;

public class OracleSchemaStatVisitor extends SchemaStatVisitor implements OracleASTVisitor {

    public boolean visit(OracleSelectTableReference x) {
        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLName) {
            String ident = ((SQLName) expr).toString();

            Map<String, String> aliasMap = aliasLocal.get();

            if (aliasMap.containsKey(ident) && aliasMap.get(ident) == null) {
                return false;
            }

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }

            Mode mode = modeLocal.get();
            switch (mode) {
                case Delete:
                    stat.incrementDeleteCount();
                    break;
                case Insert:
                    stat.incrementInsertCount();
                    break;
                case Update:
                    stat.incrementUpdateCount();
                    break;
                case Select:
                    stat.incrementSelectCount();
                    break;
                default:
                    break;
            }

            if (aliasMap != null) {
                if (x.getAlias() != null) {
                    aliasMap.put(x.getAlias(), ident);
                }
                aliasMap.put(ident, ident);
            }
            return false;
        }

        accept(x.getExpr());

        return false;
    }

    public boolean visit(SQLAggregateExpr x) {
        accept(x.getArguments());
        return false;
    }

    public boolean visit(OracleAggregateExpr x) {
        accept(x.getArguments());
        accept(x.getOver());
        return false;
    }

    public void endVisit(OracleSelect x) {
    }

    public boolean visit(OracleSelect x) {
        if (x.getOrderBy() != null) {
            x.getOrderBy().setParent(x);
        }

        accept(x.getFactoring());
        accept(x.getQuery());

        String originalTable = currentTableLocal.get();

        currentTableLocal.set((String) x.getQuery().getAttribute("table"));
        x.putAttribute("_old_local_", originalTable);

        accept(x.getOrderBy());

        currentTableLocal.set(originalTable);

        return false;
    }

    public void endVisit(SQLSelect x) {
    }

    public boolean visit(OracleUpdateStatement x) {
        aliasLocal.set(new HashMap<String, String>());

        if (x.getTable() instanceof SQLIdentifierExpr) {
            String ident = x.getTable().toString();
            currentTableLocal.set(ident);

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
            stat.incrementUpdateCount();

            Map<String, String> aliasMap = aliasLocal.get();
            aliasMap.put(ident, ident);
        } else {
            accept(x.getTable());
        }

        accept(x.getSetClause());
        accept(x.getWhere());

        return false;
    }

    public void endVisit(OracleUpdateStatement x) {
        aliasLocal.set(null);
    }

    public boolean visit(OracleDeleteStatement x) {
        return visit((SQLDeleteStatement) x);
    }

    public void endVisit(OracleDeleteStatement x) {
        aliasLocal.set(null);
    }

    public boolean visit(OracleSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    public void endVisit(OracleSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    public boolean visit(SQLPropertyExpr x) {
        if ("ROWNUM".equalsIgnoreCase(x.getName())) {
            return false;
        }

        if (x.getOwner() instanceof SQLIdentifierExpr) {
            String owner = ((SQLIdentifierExpr) x.getOwner()).getName();

            if (owner != null) {
                Map<String, String> aliasMap = aliasLocal.get();
                if (aliasMap != null) {
                    String table = aliasMap.get(owner);

                    // table == null时是SubQuery
                    if (table != null) {
                        columns.add(new Column(table, x.getName()));
                    }
                }
            }
        }
        return false;
    }

    public boolean visit(SQLIdentifierExpr x) {
        if ("ROWNUM".equalsIgnoreCase(x.getName())) {
            return false;
        }

        if ("SYSDATE".equalsIgnoreCase(x.getName())) {
            return false;
        }

        if ("+".equalsIgnoreCase(x.getName())) {
            return false;
        }

        String currentTable = currentTableLocal.get();

        if (currentTable != null) {
            columns.add(new Column(currentTable, x.getName()));
        }
        return false;
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
    public void endVisit(OracleAnalytic x) {

    }

    @Override
    public void endVisit(OracleAnalyticWindowing x) {

    }

    @Override
    public void endVisit(OracleDateExpr x) {

    }

    @Override
    public void endVisit(OracleDbLinkExpr x) {

    }

    @Override
    public void endVisit(OracleExtractExpr x) {

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
    public void endVisit(OracleTableExpr x) {

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
    public boolean visit(OracleConstraintState astNode) {

        return true;
    }

    @Override
    public boolean visit(OraclePLSQLCommitStatement astNode) {

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
    public boolean visit(OracleDateExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleDbLinkExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleExtractExpr x) {

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
        return this.visit((SQLOrderBy) x);
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
    public boolean visit(OracleTableExpr x) {

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
    public boolean visit(SampleClause x) {

        return true;
    }

    @Override
    public void endVisit(SampleClause x) {

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
    public boolean visit(SubqueryFactoringClause x) {

        return true;
    }

    @Override
    public void endVisit(SubqueryFactoringClause x) {

    }

    @Override
    public boolean visit(Entry x) {
        Map<String, String> aliasMap = aliasLocal.get();
        if (aliasMap != null) {
            String alias = null;
            if (x.getName() != null) {
                alias = x.getName().toString();
            }

            if (alias != null) {
                aliasMap.put(alias, null);
            }
        }
        x.getSubQuery().accept(this);
        return false;
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

    @Override
    public boolean visit(OracleCursorExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleCursorExpr x) {

    }

    @Override
    public boolean visit(OracleIsSetExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleIsSetExpr x) {

    }

    @Override
    public boolean visit(ReturnRowsClause x) {

        return true;
    }

    @Override
    public void endVisit(ReturnRowsClause x) {

    }

    @Override
    public boolean visit(MainModelClause x) {

        return true;
    }

    @Override
    public void endVisit(MainModelClause x) {

    }

    @Override
    public boolean visit(ModelColumnClause x) {

        return true;
    }

    @Override
    public void endVisit(ModelColumnClause x) {

    }

    @Override
    public boolean visit(QueryPartitionClause x) {

        return true;
    }

    @Override
    public void endVisit(QueryPartitionClause x) {

    }

    @Override
    public boolean visit(ModelColumn x) {

        return true;
    }

    @Override
    public void endVisit(ModelColumn x) {

    }

    @Override
    public boolean visit(ModelRulesClause x) {

        return true;
    }

    @Override
    public void endVisit(ModelRulesClause x) {

    }

    @Override
    public boolean visit(CellAssignmentItem x) {

        return true;
    }

    @Override
    public void endVisit(CellAssignmentItem x) {

    }

    @Override
    public boolean visit(CellAssignment x) {

        return true;
    }

    @Override
    public void endVisit(CellAssignment x) {

    }

    @Override
    public boolean visit(ModelClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause x) {

    }

    @Override
    public boolean visit(OracleMergeStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleMergeStatement x) {
        
    }

    @Override
    public boolean visit(MergeUpdateClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeUpdateClause x) {
        
    }

    @Override
    public boolean visit(MergeInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeInsertClause x) {
        
    }

    @Override
    public boolean visit(ErrorLoggingClause x) {
        return true;
    }

    @Override
    public void endVisit(ErrorLoggingClause x) {
        
    }

}
