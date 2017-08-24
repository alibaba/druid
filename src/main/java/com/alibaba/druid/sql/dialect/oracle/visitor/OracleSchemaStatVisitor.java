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
package com.alibaba.druid.sql.dialect.oracle.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeUpdateClause;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignment;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignmentItem;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.MainModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumnClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelRulesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.QueryPartitionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ReturnRowsClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
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

            if (containsSubQuery(ident)) {
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

            putAliasMap(aliasMap, x.getAlias(), ident);
            putAliasMap(aliasMap, ident, ident);
            return false;
        }

        accept(x.getExpr());

        return false;
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

    public boolean visit(SQLIdentifierExpr x) {
        String name = x.getName();
        if ("ROWNUM".equalsIgnoreCase(name)) {
            return false;
        }

        if ("SYSDATE".equalsIgnoreCase(name)) {
            return false;
        }

        if ("+".equalsIgnoreCase(name)) {
            return false;
        }

        if ("LEVEL".equals(name)) {
            return false;
        }

        if ("SQLCODE".equalsIgnoreCase(name)) {
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
    public void endVisit(OracleDbLinkExpr x) {

    }

    @Override
    public void endVisit(OracleIntervalExpr x) {

    }

    @Override
    public void endVisit(OracleOuterExpr x) {

    }

    @Override
    public void endVisit(OracleSelectJoin x) {

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
    public boolean visit(OracleDbLinkExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleIntervalExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleOuterExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {
        super.visit(x);

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
            addSubQuery(x.getAlias(), x.getSelect());
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
    public boolean visit(OracleWithSubqueryEntry x) {
        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap != null) {
            String alias = null;
            if (x.getName() != null) {
                alias = x.getName().toString();
            }

            if (alias != null) {
                putAliasMap(aliasMap, alias, null);
                addSubQuery(alias, x.getSubQuery());
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
                    putAliasMap(aliasMap, x.getAlias(), ident);
                }
                putAliasMap(aliasMap, ident, ident);
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
        accept(x.getEntries());

        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {

    }

    @Override
    public boolean visit(ConditionalInsertClause x) {
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
        SQLExpr when = x.getWhen();
        if (when instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr ident = (SQLIdentifierExpr) when;
            if (ident.getName().equalsIgnoreCase("OTHERS")) {
                // skip
            } else {
                this.visit(ident);
            }
        } else if (when != null) {
            when.accept(this);
        }

        for (SQLStatement stmt : x.getStatements()) {
            stmt.accept(this);
        }

        return false;
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
    public void endVisit(SQLAlterTableStatement x) {
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
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
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
    public boolean visit(OracleRangeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleRangeExpr x) {

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
    public boolean visit(OracleExitStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleExitStatement x) {

    }

    @Override
    public boolean visit(OracleContinueStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleContinueStatement x) {

    }

    @Override
    public boolean visit(OracleRaiseStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleRaiseStatement x) {

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

    @Override
    public boolean visit(OracleSupplementalIdKey x) {
        return true;
    }

    @Override
    public void endVisit(OracleSupplementalIdKey x) {

    }

    @Override
    public boolean visit(OracleSupplementalLogGrp x) {
        return true;
    }

    @Override
    public void endVisit(OracleSupplementalLogGrp x) {

    }

    public boolean visit(OracleCreateTableStatement.Organization x) {
        return false;
    }

    public void endVisit(OracleCreateTableStatement.Organization x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement.OracleExternalRecordFormat x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateTableStatement.OracleExternalRecordFormat x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement.OIDIndex x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateTableStatement.OIDIndex x) {

    }

    @Override
    public boolean visit(OracleCreatePackageStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreatePackageStatement x) {

    }

    @Override
    public boolean visit(OracleExecuteImmediateStatement x) {
        SQLExpr dynamicSql = x.getDynamicSql();

        String sql = null;

        if (dynamicSql instanceof SQLIdentifierExpr) {
            String varName = ((SQLIdentifierExpr) dynamicSql).getName();

            SQLExpr valueExpr = null;
            if (x.getParent() instanceof SQLBlockStatement) {
                List<SQLStatement> statementList = ((SQLBlockStatement) x.getParent()).getStatementList();
                for (int i = 0, size = statementList.size(); i < size; ++i) {
                    SQLStatement stmt = statementList.get(i);
                    if (stmt == x) {
                        break;
                    }

                    if (stmt instanceof SQLSetStatement) {
                        List<SQLAssignItem> items = ((SQLSetStatement) stmt).getItems();
                        for (SQLAssignItem item : items) {
                            if (item.getTarget().equals(dynamicSql)) {
                                valueExpr = item.getValue();
                                break;
                            }
                        }
                    }
                }
            }

            if (valueExpr != null) {
                dynamicSql = valueExpr;
            }
        }

        if (dynamicSql instanceof SQLCharExpr) {
            sql = ((SQLCharExpr) dynamicSql).getText();
        }

        if (sql != null) {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, getDbType());
            for (SQLStatement stmt : stmtList) {
                stmt.accept(this);
            }
        }
        return false;
    }

    @Override
    public void endVisit(OracleExecuteImmediateStatement x) {

    }

    @Override
    public boolean visit(OracleTreatExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleTreatExpr x) {

    }

    @Override
    public boolean visit(OracleCreateSynonymStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateSynonymStatement x) {

    }
}
