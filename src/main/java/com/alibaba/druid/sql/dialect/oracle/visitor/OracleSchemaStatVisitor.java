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
package com.alibaba.druid.sql.dialect.oracle.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeTimestamp;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfSnapshotClause;
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
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleParameter;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OraclePartitionByRangeClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleRangeValuesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExtractExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleRangeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSizeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableAddConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleBlockStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateDatabaseDbLinkStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateSequenceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDropDbLinkStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExprStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleFetchStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleFileSpecification;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement.Else;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement.ElseIf;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLabelStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLoopStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement.MergeUpdateClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSavePointStatement;
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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUnique;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Mode;
import com.alibaba.druid.stat.TableStat.Relationship;
import com.alibaba.druid.util.JdbcUtils;

public class OracleSchemaStatVisitor extends SchemaStatVisitor implements OracleASTVisitor {

    public OracleSchemaStatVisitor(){
        this(new ArrayList<Object>());
    }

    public OracleSchemaStatVisitor(List<Object> parameters){
        super(parameters);
        this.variants.put("DUAL", null);
        this.variants.put("NOTFOUND", null);
        this.variants.put("TRUE", null);
        this.variants.put("FALSE", null);
    }

    @Override
    public String getDbType() {
        return JdbcUtils.ORACLE;
    }

    protected Column getColumn(SQLExpr expr) {
        if (expr instanceof OracleOuterExpr) {
            expr = ((OracleOuterExpr) expr).getExpr();
        }

        return super.getColumn(expr);
    }

    public boolean visit(OracleSelectTableReference x) {
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodInvoke = (SQLMethodInvokeExpr) expr;
            if ("TABLE".equalsIgnoreCase(methodInvoke.getMethodName()) && methodInvoke.getParameters().size() == 1) {
                expr = methodInvoke.getParameters().get(0);
            }
        }

        Map<String, String> aliasMap = getAliasMap();

        if (expr instanceof SQLName) {
            String ident;
            if (expr instanceof SQLPropertyExpr) {
                String owner = ((SQLPropertyExpr) expr).getOwner().toString();
                String name = ((SQLPropertyExpr) expr).getName();

                if (aliasMap.containsKey(owner)) {
                    owner = aliasMap.get(owner);
                }
                ident = owner + "." + name;
            } else {
                ident = expr.toString();
            }

            if (subQueryMap.containsKey(ident)) {
                return false;
            }

            if ("DUAL".equalsIgnoreCase(ident)) {
                return false;
            }

            x.putAttribute(ATTR_TABLE, ident);

            TableStat stat = getTableStat(ident);

            Mode mode = getMode();
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
                case Merge:
                    stat.incrementMergeCount();
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

    public void endVisit(OracleSelect x) {
        endVisit((SQLSelect) x);
    }

    public boolean visit(OracleSelect x) {
        return visit((SQLSelect) x);
    }

    public void endVisit(SQLSelect x) {
        if (x.getQuery() != null) {
            String table = (String) x.getQuery().getAttribute(ATTR_TABLE);
            if (table != null) {
                x.putAttribute(ATTR_TABLE, table);
            }
        }
        restoreCurrentTable(x);
    }

    public boolean visit(OracleUpdateStatement x) {
        setAliasMap();
        setMode(x, Mode.Update);

        SQLTableSource tableSource = x.getTableSource();
        SQLExpr tableExpr = null;

        if (tableSource instanceof SQLExprTableSource) {
            tableExpr = ((SQLExprTableSource) tableSource).getExpr();
        }

        if (tableExpr instanceof SQLName) {
            String ident = tableExpr.toString();
            setCurrentTable(ident);

            TableStat stat = getTableStat(ident);
            stat.incrementUpdateCount();

            Map<String, String> aliasMap = getAliasMap();
            aliasMap.put(ident, ident);
            aliasMap.put(tableSource.getAlias(), ident);
        } else {
            tableSource.accept(this);
        }

        accept(x.getItems());
        accept(x.getWhere());

        return false;
    }

    public void endVisit(OracleUpdateStatement x) {
    }

    public boolean visit(OracleDeleteStatement x) {
        return visit((SQLDeleteStatement) x);
    }

    public void endVisit(OracleDeleteStatement x) {
    }

    public boolean visit(OracleSelectQueryBlock x) {
        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
        }

        if (x.getInto() instanceof SQLName) {
            String tableName = x.getInto().toString();
            TableStat stat = getTableStat(tableName);
            if (stat != null) {
                stat.incrementInsertCount();
            }
        }

        visit((SQLSelectQueryBlock) x);

        return true;
    }

    public void endVisit(OracleSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    public boolean visit(SQLPropertyExpr x) {
        if ("ROWNUM".equalsIgnoreCase(x.getName())) {
            return false;
        }

        return super.visit(x);
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        accept(x.getParameters());
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

        if ("LEVEL".equals(x.getName())) {
            return false;
        }

        return super.visit(x);
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
    public void endVisit(OracleIntervalExpr x) {

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
    public boolean visit(OracleIntervalExpr x) {

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
        x.getLeft().accept(this);
        x.getRight().accept(this);

        {
            String leftTable = (String) x.getLeft().getAttribute(ATTR_TABLE);
            String rightTable = (String) x.getRight().getAttribute(ATTR_TABLE);
            if (leftTable != null && leftTable.equals(rightTable)) {
                x.putAttribute(ATTR_TABLE, leftTable);
            }
        }
        if (x.getCondition() != null) {
            x.getCondition().accept(this);
        }

        for (SQLExpr item : x.getUsing()) {
            if (item instanceof SQLIdentifierExpr) {
                String columnName = ((SQLIdentifierExpr) item).getName();
                String leftTable = (String) x.getLeft().getAttribute(ATTR_TABLE);
                String rightTable = (String) x.getRight().getAttribute(ATTR_TABLE);
                if (leftTable != null && rightTable != null) {
                    Relationship relationship = new Relationship();
                    relationship.setLeft(new Column(leftTable, columnName));
                    relationship.setRight(new Column(rightTable, columnName));
                    relationship.setOperator("USING");
                    relationships.add(relationship);
                }

                if (leftTable != null) {
                    addColumn(leftTable, columnName);
                }

                if (rightTable != null) {
                    addColumn(rightTable, columnName);
                }
            }
        }

        return false;
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
        accept(x.getSelect());
        accept(x.getPivot());
        accept(x.getFlashback());

        String table = (String) x.getSelect().getAttribute(ATTR_TABLE);
        if (x.getAlias() != null) {
            if (table != null) {
                this.aliasMap.put(x.getAlias(), table);
            }
            this.subQueryMap.put(x.getAlias(), x.getSelect());
            this.setCurrentTable(x.getAlias());
        }

        if (table != null) {
            x.putAttribute(ATTR_TABLE, table);
        }
        return false;
    }

    @Override
    public boolean visit(OracleSelectUnPivot x) {

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
    public boolean visit(OracleWithSubqueryEntry x) {
        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap != null) {
            String alias = null;
            if (x.getName() != null) {
                alias = x.getName().toString();
            }

            if (alias != null) {
                aliasMap.put(alias, null);
                subQueryMap.put(alias, x.getSubQuery());
            }
        }
        x.getSubQuery().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleWithSubqueryEntry x) {

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
        setAliasMap();

        String originalTable = getCurrentTable();

        setMode(x.getUsing(), Mode.Select);
        x.getUsing().accept(this);

        setMode(x, Mode.Merge);

        String ident = x.getInto().toString();
        setCurrentTable(x, ident);
        x.putAttribute("_old_local_", originalTable);

        TableStat stat = getTableStat(ident);
        stat.incrementMergeCount();

        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap != null) {
            if (x.getAlias() != null) {
                aliasMap.put(x.getAlias(), ident);
            }
            aliasMap.put(ident, ident);
        }

        x.getOn().accept(this);

        if (x.getUpdateClause() != null) {
            x.getUpdateClause().accept(this);
        }

        if (x.getInsertClause() != null) {
            x.getInsertClause().accept(this);
        }

        return false;
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
    public boolean visit(OracleErrorLoggingClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleErrorLoggingClause x) {

    }

    @Override
    public boolean visit(OracleReturningClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleReturningClause x) {

    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    @Override
    public void endVisit(OracleInsertStatement x) {
        endVisit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(InsertIntoClause x) {

        if (x.getTableName() instanceof SQLName) {
            String ident = ((SQLName) x.getTableName()).toString();
            setCurrentTable(x, ident);

            TableStat stat = getTableStat(ident);
            stat.incrementInsertCount();

            Map<String, String> aliasMap = getAliasMap();
            if (aliasMap != null) {
                if (x.getAlias() != null) {
                    aliasMap.put(x.getAlias(), ident);
                }
                aliasMap.put(ident, ident);
            }
        }

        accept(x.getColumns());
        accept(x.getQuery());
        accept(x.getReturning());
        accept(x.getErrorLogging());

        return false;
    }

    @Override
    public void endVisit(InsertIntoClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        x.putAttribute("_original_use_mode", getMode());
        setMode(x, Mode.Insert);

        setAliasMap();

        accept(x.getSubQuery());

        for (OracleMultiInsertStatement.Entry entry : x.getEntries()) {
            entry.setParent(x);
        }

        accept(x.getEntries());

        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {

    }

    @Override
    public boolean visit(ConditionalInsertClause x) {
        for (ConditionalInsertClauseItem item : x.getItems()) {
            item.setParent(x);
        }
        if (x.getElseItem() != null) {
            x.getElseItem().setParent(x);
        }
        return true;
    }

    @Override
    public void endVisit(ConditionalInsertClause x) {

    }

    @Override
    public boolean visit(ConditionalInsertClauseItem x) {
        SQLObject parent = x.getParent();
        if (parent instanceof ConditionalInsertClause) {
            parent = parent.getParent();
        }
        if (parent instanceof OracleMultiInsertStatement) {
            SQLSelect subQuery = ((OracleMultiInsertStatement) parent).getSubQuery();
            if (subQuery != null) {
                String table = (String) subQuery.getAttribute("_table_");
                setCurrentTable(x, table);
            }
        }
        x.getWhen().accept(this);
        x.getThen().accept(this);
        restoreCurrentTable(x);
        return false;
    }

    @Override
    public void endVisit(ConditionalInsertClauseItem x) {

    }

    @Override
    public boolean visit(OracleBlockStatement x) {
        for (OracleParameter param : x.getParameters()) {
            param.setParent(x);

            SQLExpr name = param.getName();
            this.variants.put(name.toString(), name);
        }
        return true;
    }

    @Override
    public void endVisit(OracleBlockStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterSessionStatement x) {

    }

    @Override
    public boolean visit(OracleExprStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleExprStatement x) {

    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        String tableName = x.getTable().toString();
        getTableStat(tableName);
        return false;
    }

    @Override
    public void endVisit(OracleLockTableStatement x) {

    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleDatetimeExpr x) {

    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleSysdateExpr x) {

    }

    @Override
    public void endVisit(com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement.Item x) {

    }

    @Override
    public boolean visit(com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement.Item x) {
        return true;
    }

    @Override
    public boolean visit(OracleExceptionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExceptionStatement x) {

    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleArgumentExpr x) {

    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleSetTransactionStatement x) {

    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleExplainStatement x) {

    }

    @Override
    public boolean visit(OracleAlterProcedureStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterProcedureStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableDropPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableTruncatePartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableStatement x) {
        String tableName = x.getName().toString();
        TableStat stat = getTableStat(tableName);
        stat.incrementAlterCount();

        setCurrentTable(x, tableName);

        for (SQLObject item : x.getItems()) {
            item.setParent(x);
            item.accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OracleAlterTableStatement x) {
        restoreCurrentTable(x);
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.TableSpaceItem x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableModify x) {
        OracleAlterTableStatement stmt = (OracleAlterTableStatement) x.getParent();
        String table = stmt.getName().toString();

        for (SQLColumnDefinition column : x.getColumns()) {
            String columnName = column.getName().toString();
            addColumn(table, columnName);

        }

        return false;
    }

    @Override
    public void endVisit(OracleAlterTableModify x) {

    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        return visit((SQLCreateIndexStatement) x);
    }

    @Override
    public void endVisit(OracleCreateIndexStatement x) {
        restoreCurrentTable(x);
    }

    @Override
    public boolean visit(OracleAlterIndexStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement x) {

    }

    @Override
    public boolean visit(OracleForStatement x) {
        x.getRange().setParent(x);

        SQLName index = x.getIndex();
        this.getVariants().put(index.toString(), x);

        x.getRange().accept(this);
        accept(x.getStatements());

        return false;
    }

    @Override
    public void endVisit(OracleForStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement.Rebuild x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement.Rebuild x) {

    }

    @Override
    public boolean visit(Else x) {
        return true;
    }

    @Override
    public void endVisit(Else x) {

    }

    @Override
    public boolean visit(ElseIf x) {
        return true;
    }

    @Override
    public void endVisit(ElseIf x) {

    }

    @Override
    public boolean visit(OracleIfStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleIfStatement x) {

    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleRangeExpr x) {

    }

    @Override
    public boolean visit(OracleAlterTableAddConstaint x) {
        x.getConstraint().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableAddConstaint x) {

    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        accept(x.getColumns());

        return false;
    }

    @Override
    public void endVisit(OraclePrimaryKey x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        this.visit((SQLCreateTableStatement) x);

        if (x.getSelect() != null) {
            x.getSelect().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OracleCreateTableStatement x) {
        this.endVisit((SQLCreateTableStatement) x);
    }

    @Override
    public boolean visit(OracleStorageClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleStorageClause x) {

    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleGotoStatement x) {

    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleLabelStatement x) {

    }

    @Override
    public boolean visit(OracleParameter x) {
        return true;
    }

    @Override
    public void endVisit(OracleParameter x) {

    }

    @Override
    public boolean visit(OracleCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCommitStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTriggerStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterSynonymStatement x) {

    }

    @Override
    public boolean visit(AsOfSnapshotClause x) {
        return false;
    }

    @Override
    public void endVisit(AsOfSnapshotClause x) {

    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterViewStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableMoveTablespace x) {

    }

    @Override
    public boolean visit(OracleSizeExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleSizeExpr x) {

    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        return false;
    }

    @Override
    public void endVisit(OracleFileSpecification x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTablespaceAddDataFile x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(OracleCreateSequenceStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateSequenceStatement x) {

    }

    @Override
    public boolean visit(OracleRangeValuesClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleRangeValuesClause x) {

    }

    @Override
    public boolean visit(OraclePartitionByRangeClause x) {
        return false;
    }

    @Override
    public void endVisit(OraclePartitionByRangeClause x) {

    }

    @Override
    public boolean visit(OracleLoopStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleLoopStatement x) {

    }

    @Override
    public boolean visit(OracleExitStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExitStatement x) {

    }

    @Override
    public boolean visit(OracleFetchStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleFetchStatement x) {

    }

    @Override
    public boolean visit(OracleSavePointStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleSavePointStatement x) {

    }

    @Override
    public boolean visit(OracleCreateProcedureStatement x) {
        String name = x.getName().toString();
        this.variants.put(name, x);
        accept(x.getBlock());
        return false;
    }

    @Override
    public void endVisit(OracleCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateDatabaseDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleDropDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDataTypeTimestamp x) {
        return false;
    }

    @Override
    public void endVisit(OracleDataTypeTimestamp x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        return false;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalYear x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        return false;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalDay x) {

    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleUsingIndexClause x) {

    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleLobStorageClause x) {

    }

    @Override
    public boolean visit(OracleUnique x) {
        return visit((SQLUnique) x);
    }

    @Override
    public void endVisit(OracleUnique x) {

    }

    @Override
    public boolean visit(OracleForeignKey x) {
        return visit((SQLForeignKeyImpl) x);
    }

    @Override
    public void endVisit(OracleForeignKey x) {

    }

    @Override
    public boolean visit(OracleCheck x) {
        return visit((SQLCheck) x);
    }

    @Override
    public void endVisit(OracleCheck x) {

    }
}
