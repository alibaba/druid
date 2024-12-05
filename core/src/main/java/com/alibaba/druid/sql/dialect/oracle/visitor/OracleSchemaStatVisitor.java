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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIsOfTypeExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Mode;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OracleSchemaStatVisitor extends SchemaStatVisitor implements OracleASTVisitor {
    private static final Set<Long> PSEUDO_COLUMNS;
    private static final Set<Long> FUNCTIONS_IDENT;
    private static final Set<Long> IMPLICIT_CURSOR_ATTRIBUTES;

    static {
        PSEUDO_COLUMNS = new HashSet<>();
        PSEUDO_COLUMNS.add(FnvHash.Constants.ROWID);
        PSEUDO_COLUMNS.add(FnvHash.Constants.ROWNUM);
        PSEUDO_COLUMNS.add(FnvHash.Constants.LEVEL);

        FUNCTIONS_IDENT = new HashSet<>();
        FUNCTIONS_IDENT.add(FnvHash.Constants.SYSDATE);
        FUNCTIONS_IDENT.add(FnvHash.Constants.CURRENT_DATE);
        FUNCTIONS_IDENT.add(FnvHash.Constants.CURRENT_TIMESTAMP);
        FUNCTIONS_IDENT.add(FnvHash.Constants.SYSTIMESTAMP);
        FUNCTIONS_IDENT.add(FnvHash.Constants.SQLCODE);
        FUNCTIONS_IDENT.add(FnvHash.Constants.SQLERRM);

        IMPLICIT_CURSOR_ATTRIBUTES = new HashSet<>();
        IMPLICIT_CURSOR_ATTRIBUTES.add(FnvHash.Constants.FOUND);
        IMPLICIT_CURSOR_ATTRIBUTES.add(FnvHash.Constants.NOTFOUND);
        IMPLICIT_CURSOR_ATTRIBUTES.add(FnvHash.Constants.ROWCOUNT);
        IMPLICIT_CURSOR_ATTRIBUTES.add(FnvHash.Constants.BULK_ROWCOUNT);
        IMPLICIT_CURSOR_ATTRIBUTES.add(FnvHash.Constants.BULK_EXCEPTIONS);
    }

    public OracleSchemaStatVisitor() {
        this(new ArrayList<Object>());
    }

    public OracleSchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }

    public OracleSchemaStatVisitor(List<Object> parameters) {
        super(DbType.oracle, parameters);
    }

    protected Column getColumn(SQLExpr expr) {
        if (expr instanceof OracleOuterExpr) {
            expr = ((OracleOuterExpr) expr).getExpr();
        }

        return super.getColumn(expr);
    }

    public boolean visit(OracleSelectTableReference x) {
        SQLExpr expr = x.getExpr();
        TableStat stat = getTableStat(x);

        if (expr instanceof SQLName) {
            if (((SQLName) expr).nameHashCode64() == FnvHash.Constants.DUAL) {
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                if (isSubQueryOrParamOrVariant(propertyExpr)) {
                    return false;
                }
            } else if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
                if (isSubQueryOrParamOrVariant(identifierExpr)) {
                    return false;
                }
            }

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

            return false;
        }

        // accept(x.getExpr());

        return false;
    }

    public boolean visit(OracleUpdateStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        setMode(x, Mode.Update);

        SQLTableSource tableSource = x.getTableSource();
        SQLExpr tableExpr = null;

        if (tableSource instanceof SQLExprTableSource) {
            tableExpr = ((SQLExprTableSource) tableSource).getExpr();
        }

        if (tableExpr instanceof SQLName) {
            TableStat stat = getTableStat((SQLName) tableExpr);
            stat.incrementUpdateCount();
        } else {
            tableSource.accept(this);
        }

        accept(x.getItems());
        accept(x.getWhere());

        return false;
    }

    public boolean visit(OracleSelectQueryBlock x) {
        SQLExprTableSource into = x.getInto();
//        if (into != null && into.getExpr() instanceof SQLName) {
//            TableStat stat = getTableStat((SQLName) into.getExpr());
//            if (stat != null) {
//                stat.incrementInsertCount();
//            }
//        }

        return visit((SQLSelectQueryBlock) x);
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

        if ("+".equalsIgnoreCase(name)) {
            return false;
        }

        long hashCode64 = x.hashCode64();
        if (isPseudoColumn(hashCode64) || isFunctionIdentifier(hashCode64) ||
                isImplicitCursorBinaryExpr(x.getParent())) {
            return false;
        }

        return super.visit(x);
    }

    // This is to override the default behavior of the {@link SchemaStatVisitor} to ignore pseudo columns.
    @Override
    protected boolean isPseudoColumn(long hash) {
        // Pseudo columns which are not covered by {@link SchemaStatVisitor} to ignore. Not all pseudo
        // columns are ignored, for example, wildcard * is not ignored.
        return PSEUDO_COLUMNS.contains(hash);
    }

    protected boolean isFunctionIdentifier(long hash) {
        return FUNCTIONS_IDENT.contains(hash);
    }

    private static boolean isImplicitCursorBinaryExpr(SQLObject sqlObject) {
        if (!(sqlObject instanceof SQLBinaryOpExpr)) {
            return false;
        }

        SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlObject;
        if (sqlBinaryOpExpr.getOperator() == SQLBinaryOperator.Modulus) {
            SQLExpr left = sqlBinaryOpExpr.getLeft();
            SQLExpr right = sqlBinaryOpExpr.getRight();
            return isImplicitCursorBinaryExpr(left, right);
        }

        return false;
    }

    private static boolean isImplicitCursorBinaryExpr(SQLExpr left, SQLExpr right) {
        // Change: if it is an implicit cursor, skip it
        if (left instanceof SQLIdentifierExpr && right instanceof SQLIdentifierExpr) {
            long leftHashCode64 = ((SQLIdentifierExpr) left).hashCode64();
            long rightHashCode64 = ((SQLIdentifierExpr) right).hashCode64();
            return leftHashCode64 == FnvHash.Constants.SQL && IMPLICIT_CURSOR_ATTRIBUTES.contains(rightHashCode64);
        }

        return false;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {
        super.visit(x);
//
//        for (SQLExpr item : x.getUsing()) {
//            if (item instanceof SQLIdentifierExpr) {
//                String columnName = ((SQLIdentifierExpr) item).getName();
//                String leftTable = (String) x.getLeft().getAttribute(ATTR_TABLE);
//                String rightTable = (String) x.getRight().getAttribute(ATTR_TABLE);
//                if (leftTable != null && rightTable != null) {
//                    Relationship relationship = new Relationship();
//                    relationship.setLeft(new Column(leftTable, columnName));
//                    relationship.setRight(new Column(rightTable, columnName));
//                    relationship.setOperator("USING");
//                    relationships.add(relationship);
//                }
//
//                if (leftTable != null) {
//                    addColumn(leftTable, columnName);
//                }
//
//                if (rightTable != null) {
//                    addColumn(rightTable, columnName);
//                }
//            }
//        }

        return false;
    }

    @Override
    public boolean visit(OracleSelectSubqueryTableSource x) {
        accept(x.getSelect());
        accept(x.getPivot());
        accept(x.getUnpivot());
        accept(x.getFlashback());
        return false;
    }

    @Override
    public boolean visit(OracleWithSubqueryEntry x) {
        x.getSubQuery().accept(this);
        return false;
    }

    @Override
    public boolean visit(InsertIntoClause x) {
        if (x.getTableName() instanceof SQLName) {
            TableStat stat = getTableStat(x.getTableName());
            stat.incrementInsertCount();
        }

        accept(x.getColumns());
        accept(x.getQuery());
        accept(x.getReturning());
        accept(x.getErrorLogging());

        return false;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        x.putAttribute("_original_use_mode", getMode());
        setMode(x, Mode.Insert);

        accept(x.getSubQuery());
        accept(x.getEntries());

        return false;
    }

    @Override
    public boolean visit(ConditionalInsertClauseItem x) {
        SQLObject parent = x.getParent();
        if (parent instanceof ConditionalInsertClause) {
            parent = parent.getParent();
        }
        if (parent instanceof OracleMultiInsertStatement) {
            SQLSelect subQuery = ((OracleMultiInsertStatement) parent).getSubQuery();
        }
        x.getWhen().accept(this);
        x.getThen().accept(this);
        return false;
    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        getTableStat(x.getTable());
        return false;
    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        return false;
    }

    @Override
    public boolean visit(SQLExceptionStatement.Item x) {
        SQLExpr when = x.getWhen();
        if (when instanceof SQLIdentifierExpr) {
            return false;
        } else if (when != null) {
            when.accept(this);
        }

        for (SQLStatement stmt : x.getStatements()) {
            stmt.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableModify x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
        SQLName tableName = stmt.getName();

        for (SQLColumnDefinition column : x.getColumns()) {
            SQLName columnName = column.getName();
            addColumn(tableName, columnName.toString());

        }

        return false;
    }

    @Override
    public boolean visit(OracleForStatement x) {
        x.getRange().accept(this);
        accept(x.getStatements());
        return false;
    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        accept(x.getColumns());

        return false;
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
    public boolean visit(OracleStorageClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        return false;
    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        return false;
    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleExitStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleContinueStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleRaiseStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        return false;
    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        return false;
    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        return false;
    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        return false;
    }

    public boolean visit(OracleCreateTableStatement.Organization x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateTableStatement.OIDIndex x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreatePackageStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        for (SQLStatement stmt : x.getStatements()) {
            stmt.accept(this);
        }
        return false;
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
                for (SQLStatement stmt : statementList) {
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
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
            for (SQLStatement stmt : stmtList) {
                stmt.accept(this);
            }
        }
        return false;
    }

    @Override
    public boolean visit(OracleCreateSynonymStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleCreateTypeStatement x) {
        return false;
    }

    @Override
    public boolean visit(OraclePipeRowStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleIsOfTypeExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleXmlColumnProperties x) {
        return false;
    }

    @Override
    public boolean visit(OracleXmlColumnProperties.OracleXMLTypeStorage x) {
        return false;
    }

}
