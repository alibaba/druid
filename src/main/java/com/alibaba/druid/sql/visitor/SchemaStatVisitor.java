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
package com.alibaba.druid.sql.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsValuesTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitorAdapter;
import com.alibaba.druid.sql.ast.statement.SQLDeclareStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Condition;
import com.alibaba.druid.stat.TableStat.Mode;
import com.alibaba.druid.stat.TableStat.Relationship;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;

public class SchemaStatVisitor extends SQLASTVisitorAdapter {

    protected SchemaRepository repository;

    protected final HashMap<TableStat.Name, TableStat> tableStats     = new LinkedHashMap<TableStat.Name, TableStat>();
    protected final Map<Column, Column>                columns        = new LinkedHashMap<Column, Column>();
    protected final List<Condition>                    conditions     = new ArrayList<Condition>();
    protected final Set<Relationship>                  relationships  = new LinkedHashSet<Relationship>();
    protected final List<Column>                       orderByColumns = new ArrayList<Column>();
    protected final Set<Column>                        groupByColumns = new LinkedHashSet<Column>();
    protected final List<SQLAggregateExpr>             aggregateFunctions = new ArrayList<SQLAggregateExpr>();
    protected final List<SQLMethodInvokeExpr>          functions          = new ArrayList<SQLMethodInvokeExpr>(2);

    protected final Map<String, SQLObject> subQueryMap = new LinkedHashMap<String, SQLObject>();

    protected final Map<String, SQLObject> variants = new LinkedHashMap<String, SQLObject>();

    protected Map<String, String> aliasMap = new LinkedHashMap<String, String>();

    protected String currentTable;

    public final static String ATTR_TABLE  = "_table_";
    public final static String ATTR_COLUMN = "_column_";

    private List<Object> parameters;

    private Mode mode;

    protected String dbType;

    public SchemaStatVisitor(){
        this((String) null);
    }

    public SchemaStatVisitor(String dbType){
        this(new SchemaRepository(dbType), new ArrayList<Object>());
        this.dbType = dbType;
    }

    public SchemaStatVisitor(List<Object> parameters){
        this.parameters = parameters;
    }

    public SchemaStatVisitor(SchemaRepository repository, List<Object> parameters){
        this.repository = repository;
        this.parameters = parameters;
        if (repository != null) {
            String dbType = repository.getDbType();
            if (dbType != null && this.dbType == null) {
                this.dbType = dbType;
            }
        }
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public TableStat getTableStat(String ident) {
        return getTableStat(ident, null);
    }

    protected Column addColumn(String tableName, String columnName) {
        return addColumn(tableName, columnName, 0, 0);
    }

    protected Column addColumn(String tableName, String columnName, long table_hash, long name_hash) {
        tableName = handleName(tableName);
        columnName = handleName(columnName);

        Column column = this.getColumn(tableName, columnName);
        if (column == null && columnName != null) {
            column = new Column(tableName, columnName);
            columns.put(column, column);
        }
        return column;
    }

    public TableStat getTableStat(String tableName, String alias) {
        if (variants.containsKey(tableName)) {
            return null;
        }

        tableName = handleName(tableName);

        TableStat.Name tableNameObj = new TableStat.Name(tableName);
        TableStat stat = tableStats.get(tableNameObj);
        if (stat == null) {
            stat = new TableStat();
            tableStats.put(new TableStat.Name(tableName), stat);
            putAliasMap(aliasMap, alias, tableName);
        }
        return stat;
    }

    private String handleName(String ident) {
        int len = ident.length();
        if (ident.charAt(0) == '[' && ident.charAt(len - 1) == ']') {
            ident = ident.substring(1, len - 1);
        } else {
            boolean flag0 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            for (int i = 0; i < len; ++i) {
                final char ch = ident.charAt(i);
                if (ch == '\"') {
                    flag0 = true;
                } else if (ch == '`') {
                    flag1 = true;
                } else if (ch == ' ') {
                    flag2 = true;
                } else if (ch == '\'') {
                    flag3 = true;
                }
            }
            if (flag0) {
                ident = ident.replaceAll("\"", "");
            }

            if (flag1) {
                ident = ident.replaceAll("`", "");
            }

            if (flag2) {
                ident = ident.replaceAll(" ", "");
            }

            if (flag3) {
                ident = ident.replaceAll("'", "");
            }
        }
        ident = aliasWrap(ident);

        return ident;
    }

    public Map<String, SQLObject> getVariants() {
        return variants;
    }

    public void setAliasMap() {
        this.setAliasMap(new HashMap<String, String>());
    }

    public void clearAliasMap() {
        this.aliasMap = null;
    }

    public void setAliasMap(Map<String, String> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }
    
    public void setCurrentTable(String table) {
        this.currentTable = table;
    }

    public void setCurrentTable(SQLObject x) {
        x.putAttribute("_old_local_", this.currentTable);
    }

    public void restoreCurrentTable(SQLObject x) {
        String table = (String) x.getAttribute("_old_local_");
        this.currentTable = table;
    }

    public void setCurrentTable(SQLObject x, String table) {
        x.putAttribute("_old_local_", this.currentTable);
        this.currentTable = table;
    }

    public String getCurrentTable() {
        return currentTable;
    }

    protected Mode getMode() {
        return mode;
    }

    protected void setModeOrigin(SQLObject x) {
        Mode originalMode = (Mode) x.getAttribute("_original_use_mode");
        mode = originalMode;
    }

    protected Mode setMode(SQLObject x, Mode mode) {
        Mode oldMode = this.mode;
        x.putAttribute("_original_use_mode", oldMode);
        this.mode = mode;
        return oldMode;
    }

    private boolean visitOrderBy(SQLIdentifierExpr x) {
        if (containsSubQuery(currentTable)) {
            return false;
        }

        String identName = x.getName();
        if (aliasMap != null && aliasMap.containsKey(identName) && aliasMap.get(identName) == null) {
            return false;
        }

        if (currentTable != null) {
            orderByAddColumn(currentTable, identName, x);
        } else {
            orderByAddColumn("UNKOWN", identName, x);
        }
        return false;
    }

    private boolean visitOrderBy(SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLIdentifierExpr) {
            String owner = ((SQLIdentifierExpr) x.getOwner()).getName();

            if (containsSubQuery(owner)) {
                return false;
            }

            owner = aliasWrap(owner);

            if (owner != null) {
                orderByAddColumn(owner, x.getName(), x);
            }
        }

        return false;
    }

    private void orderByAddColumn(String table, String columnName, SQLObject expr) {
        Column column = new Column(table, columnName);

        SQLObject parent = expr.getParent();
        if (parent instanceof SQLSelectOrderByItem) {
            SQLOrderingSpecification type = ((SQLSelectOrderByItem) parent).getType();
            column.getAttributes().put("orderBy.type", type);
        }

        orderByColumns.add(column);
    }

    protected class OrderByStatVisitor extends SQLASTVisitorAdapter {

        private final SQLOrderBy orderBy;

        public OrderByStatVisitor(SQLOrderBy orderBy){
            this.orderBy = orderBy;
            for (SQLSelectOrderByItem item : orderBy.getItems()) {
                item.getExpr().setParent(item);
            }
        }

        public SQLOrderBy getOrderBy() {
            return orderBy;
        }

        public boolean visit(SQLIdentifierExpr x) {
            return visitOrderBy(x);
        }

        public boolean visit(SQLPropertyExpr x) {
            return visitOrderBy(x);
        }
    }

    protected class MySqlOrderByStatVisitor extends MySqlASTVisitorAdapter {

        private final SQLOrderBy orderBy;

        public MySqlOrderByStatVisitor(SQLOrderBy orderBy){
            this.orderBy = orderBy;
            for (SQLSelectOrderByItem item : orderBy.getItems()) {
                item.getExpr().setParent(item);
            }
        }

        public SQLOrderBy getOrderBy() {
            return orderBy;
        }

        public boolean visit(SQLIdentifierExpr x) {
            return visitOrderBy(x);
        }

        public boolean visit(SQLPropertyExpr x) {
            return visitOrderBy(x);
        }
    }

    protected class PGOrderByStatVisitor extends PGASTVisitorAdapter {

        private final SQLOrderBy orderBy;

        public PGOrderByStatVisitor(SQLOrderBy orderBy){
            this.orderBy = orderBy;
            for (SQLSelectOrderByItem item : orderBy.getItems()) {
                item.getExpr().setParent(item);
            }
        }

        public SQLOrderBy getOrderBy() {
            return orderBy;
        }

        public boolean visit(SQLIdentifierExpr x) {
            return visitOrderBy(x);
        }

        public boolean visit(SQLPropertyExpr x) {
            return visitOrderBy(x);
        }
    }

    protected class OracleOrderByStatVisitor extends PGASTVisitorAdapter {

        private final SQLOrderBy orderBy;

        public OracleOrderByStatVisitor(SQLOrderBy orderBy){
            this.orderBy = orderBy;
            for (SQLSelectOrderByItem item : orderBy.getItems()) {
                item.getExpr().setParent(item);
            }
        }

        public SQLOrderBy getOrderBy() {
            return orderBy;
        }

        public boolean visit(SQLIdentifierExpr x) {
            return visitOrderBy(x);
        }

        public boolean visit(SQLPropertyExpr x) {
            return visitOrderBy(x);
        }
    }

    public boolean visit(SQLOrderBy x) {
        final SQLASTVisitor orderByVisitor = createOrderByVisitor(x);

        SQLSelectQueryBlock query = null;
        if (x.getParent() instanceof SQLSelectQueryBlock) {
            query = (SQLSelectQueryBlock) x.getParent();
        }
        if (query != null) {
            for (SQLSelectOrderByItem item : x.getItems()) {
                SQLExpr expr = item.getExpr();
                if (expr instanceof SQLIntegerExpr) {
                    int intValue = ((SQLIntegerExpr) expr).getNumber().intValue() - 1;
                    if (intValue < query.getSelectList().size()) {
                        SQLSelectItem selectItem = query.getSelectList().get(intValue);
                        selectItem.getExpr().accept(orderByVisitor);
                    }
                } else if (expr instanceof MySqlExpr || expr instanceof OracleExpr) {
                    continue;
                }
            }
        }
        x.accept(orderByVisitor);
        return true;
    }

    protected SQLASTVisitor createOrderByVisitor(SQLOrderBy x) {
        final SQLASTVisitor orderByVisitor;
        if (JdbcConstants.MYSQL.equals(getDbType())) {
            orderByVisitor = new MySqlOrderByStatVisitor(x);
        } else if (JdbcConstants.POSTGRESQL.equals(getDbType())) {
            orderByVisitor = new PGOrderByStatVisitor(x);
        } else if (JdbcConstants.ORACLE.equals(getDbType())) {
            orderByVisitor = new OracleOrderByStatVisitor(x);
        } else {
            orderByVisitor = new OrderByStatVisitor(x);
        }
        return orderByVisitor;
    }

    public Set<Relationship> getRelationships() {
        return relationships;
    }

    public List<Column> getOrderByColumns() {
        return orderByColumns;
    }

    public Set<Column> getGroupByColumns() {
        return groupByColumns;
    }

    public List<Condition> getConditions() {
        return conditions;
    }
    
    public List<SQLAggregateExpr> getAggregateFunctions() {
        return aggregateFunctions;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        SQLObject parent = x.getParent();

        if (parent instanceof SQLIfStatement) {
            return true;
        }

        final SQLBinaryOperator op = x.getOperator();
        final SQLExpr left = x.getLeft();
        final SQLExpr right = x.getRight();

        switch (op) {
            case Equality:
            case NotEqual:
            case GreaterThan:
            case GreaterThanOrEqual:
            case LessThan:
            case LessThanOrGreater:
            case LessThanOrEqual:
            case LessThanOrEqualOrGreaterThan:
            case Like:
            case NotLike:
            case Is:
            case IsNot:
                handleCondition(left, x.getOperator().name, right);
                handleCondition(right, x.getOperator().name, left);

                handleRelationship(left, x.getOperator().name, right);
                break;
            case BooleanOr:{
                List<SQLExpr> list = SQLBinaryOpExpr.split(x, op);

                for (SQLExpr item : list) {
                    if (item instanceof SQLBinaryOpExpr) {
                        visit((SQLBinaryOpExpr) item);
                    } else {
                        item.accept(this);
                    }
                }

                return false;
            }
            default:
                break;
        }

        left.accept(this);
        right.accept(this);

        return false;
    }

    protected void handleRelationship(SQLExpr left, String operator, SQLExpr right) {
        Column leftColumn = getColumn(left);
        if (leftColumn == null) {
            return;
        }

        Column rightColumn = getColumn(right);
        if (rightColumn == null) {
            return;
        }

        Relationship relationship = new Relationship();
        relationship.setLeft(leftColumn);
        relationship.setRight(rightColumn);
        relationship.setOperator(operator);

        this.relationships.add(relationship);
    }

    protected void handleCondition(SQLExpr expr, String operator, List<SQLExpr> values) {
        handleCondition(expr, operator, values.toArray(new SQLExpr[values.size()]));
    }

    protected void handleCondition(SQLExpr expr, String operator, SQLExpr... valueExprs) {
        if (expr instanceof SQLCastExpr) {
            expr = ((SQLCastExpr) expr).getExpr();
        }
        
        Column column = getColumn(expr);
        if (column == null) {
            return;
        }
        
        Condition condition = null;
        for (Condition item : this.getConditions()) {
            if (item.getColumn().equals(column) && item.getOperator().equals(operator)) {
                condition = item;
                break;
            }
        }

        if (condition == null) {
            condition = new Condition();
            condition.setColumn(column);
            condition.setOperator(operator);
            this.conditions.add(condition);
        }

        for (SQLExpr item : valueExprs) {
            Column valueColumn = getColumn(item);
            if (valueColumn != null) {
                continue;
            }
            Object value = SQLEvalVisitorUtils.eval(getDbType(), item, parameters, false);
            if (value == SQLEvalVisitor.EVAL_VALUE_NULL) {
                value = null;
            }
            condition.getValues().add(value);
        }
    }

    public String getDbType() {
        return dbType;
    }

    protected Column getColumn(SQLExpr expr) {
        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap == null) {
            return null;
        }
        
        if (expr instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodInvokeExp = (SQLMethodInvokeExpr) expr;
            if (methodInvokeExp.getParameters().size() == 1) {
                SQLExpr firstExpr = methodInvokeExp.getParameters().get(0);
                return getColumn(firstExpr);
            }
        }

        if (expr instanceof SQLCastExpr) {
            expr = ((SQLCastExpr) expr).getExpr();
        }

        if (expr instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) expr).getOwner();
            String column = ((SQLPropertyExpr) expr).getName();

            if (owner instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) owner).getName();
                String table = tableName;
                String tableNameLower = tableName.toLowerCase();
                
                if (aliasMap.containsKey(tableNameLower)) {
                    table = aliasMap.get(tableNameLower);
                }
                
                if (containsSubQuery(tableNameLower)) {
                    table = null;
                }

                if (variants.containsKey(table)) {
                    return null;
                }

                if (table != null) {
                    return new Column(table, column);
                }

                return handleSubQueryColumn(tableName, column);
            }

            return null;
        }

        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            if (isParam(identifierExpr)) {
                return null;
            }
            Column attrColumn = (Column) expr.getAttribute(ATTR_COLUMN);
            if (attrColumn != null) {
                return attrColumn;
            }

            String column = identifierExpr.getName();
            String table = getCurrentTable();
            if (table != null && aliasMap.containsKey(table)) {
                table = aliasMap.get(table);
                if (table == null) {
                    return null;
                }
            }

            if (table != null) {
                return new Column(table, column);
            }

            if (variants.containsKey(column)) {
                return null;
            }

            return new Column("UNKNOWN", column);
        }

        return null;
    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        setMode(x, Mode.Delete);

        setAliasMap();

        String originalTable = getCurrentTable();

        for (SQLExprTableSource tableSource : x.getTableSources()) {
            SQLName name = (SQLName) tableSource.getExpr();

            String ident = name.toString();
            setCurrentTable(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = getTableStat(ident);
            stat.incrementDeleteCount();

            Map<String, String> aliasMap = getAliasMap();
            putAliasMap(aliasMap, ident, ident);
        }

        return false;
    }

    @Override
    public boolean visit(SQLDropViewStatement x) {
        setMode(x, Mode.Drop);
        return true;
    }

    @Override
    public boolean visit(SQLDropTableStatement x) {
        setMode(x, Mode.Insert);

        setAliasMap();

        String originalTable = getCurrentTable();

        for (SQLExprTableSource tableSource : x.getTableSources()) {
            SQLName name = (SQLName) tableSource.getExpr();
            String ident = name.toString();
            setCurrentTable(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = getTableStat(ident);
            stat.incrementDropCount();

            Map<String, String> aliasMap = getAliasMap();
            putAliasMap(aliasMap, ident, ident);
        }

        return false;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        if (repository != null) {
            repository.resolve(x);
        }

        setMode(x, Mode.Insert);

        setAliasMap();

        String originalTable = getCurrentTable();

        if (x.getTableName() instanceof SQLName) {
            String ident = ((SQLName) x.getTableName()).toString();
            setCurrentTable(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = getTableStat(ident);
            stat.incrementInsertCount();

            Map<String, String> aliasMap = getAliasMap();
            putAliasMap(aliasMap, x.getAlias(), ident);
            putAliasMap(aliasMap, ident, ident);
        }

        accept(x.getColumns());
        accept(x.getQuery());

        return false;
    }
    
    protected static void putAliasMap(Map<String, String> aliasMap, String name, String value) {
        if (aliasMap == null || name == null) {
            return;
        }
        aliasMap.put(name.toLowerCase(), value);
    }

    protected void accept(SQLObject x) {
        if (x != null) {
            x.accept(this);
        }
    }

    protected void accept(List<? extends SQLObject> nodes) {
        for (int i = 0, size = nodes.size(); i < size; ++i) {
            accept(nodes.get(i));
        }
    }

    public boolean visit(SQLSelectQueryBlock x) {
        if (x.getFrom() == null) {
            return false;
        }

        setMode(x, Mode.Select);

        if (x.getFrom() instanceof SQLSubqueryTableSource) {
            x.getFrom().accept(this);
            return false;
        }

        SQLExprTableSource into = x.getInto();
        if (into != null && into.getExpr() instanceof SQLName) {
            SQLName intoExpr = (SQLName) into.getExpr();

            boolean isParam = intoExpr instanceof SQLIdentifierExpr && isParam((SQLIdentifierExpr) intoExpr);

            if (!isParam) {
                String ident = intoExpr.toString();
                TableStat stat = getTableStat(ident);
                if (stat != null) {
                    stat.incrementInsertCount();
                }
            }
        }

        String originalTable = getCurrentTable();

        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableSource = (SQLExprTableSource) x.getFrom();
            if (tableSource.getExpr() instanceof SQLName) {
                String ident = tableSource.getExpr().toString();

                setCurrentTable(x, ident);
                x.putAttribute(ATTR_TABLE, ident);
                if (x.getParent() instanceof SQLSelect) {
                    x.getParent().putAttribute(ATTR_TABLE, ident);
                }
                x.putAttribute("_old_local_", originalTable);
            }
        }

        if (x.getFrom() != null) {
            x.getFrom().accept(this); // 提前执行，获得aliasMap
            String table = (String) x.getFrom().getAttribute(ATTR_TABLE);
            if (table != null) {
                x.putAttribute(ATTR_TABLE, table);
            }
        }

        // String ident = x.getTable().toString();
        //
        // TableStat stat = getTableStat(ident);
        // stat.incrementInsertCount();
        // return false;

        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
        }

        return true;
    }

    public static boolean isParam(SQLIdentifierExpr x) {
        if (x.isParameter() != null) {
            return x.isParameter();
        }

        for (SQLObject parent = x.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof SQLCreateProcedureStatement) {
                SQLCreateProcedureStatement stmt = (SQLCreateProcedureStatement) parent;
                for (SQLParameter parameter : stmt.getParameters()) {
                    if (x.equals(parameter.getName())) {
                        x.setParameter(true);
                        return true;
                    }
                }
                break;
            }

            if (parent instanceof SQLBlockStatement) {
                SQLBlockStatement stmt = (SQLBlockStatement) parent;
                for (SQLParameter parameter : stmt.getParameters()) {
                    if (x.equals(parameter.getName())) {
                        x.setParameter(true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void endVisit(SQLSelectQueryBlock x) {
        String originalTable = (String) x.getAttribute("_old_local_");
        x.putAttribute("table", getCurrentTable());
        setCurrentTable(originalTable);

        setModeOrigin(x);
    }

    public boolean visit(SQLJoinTableSource x) {
        x.getLeft().accept(this);
        x.getRight().accept(this);
        SQLExpr condition = x.getCondition();
        if (condition != null) {
            condition.accept(this);
        }
        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr ownerExpr = (SQLIdentifierExpr) x.getOwner();
            String owner = ownerExpr.getName();

            if (containsSubQuery(owner)) {
                return false;
            }

            owner = aliasWrap(owner);

            if (owner != null) {
                Column column = addColumn(owner, x.getName(), ownerExpr.name_hash_lower(), x.name_hash_lower());
                x.putAttribute(ATTR_COLUMN, column);
                if (column != null) {
                    if (isParentGroupBy(x)) {
                        this.groupByColumns.add(column);
                    }
                    
                    if (column != null) {
                        setColumn(x, column);
                    }
                }
            }
        }
        return false;
    }

    protected String aliasWrap(String name) {
        Map<String, String> aliasMap = getAliasMap();

        if (aliasMap != null) {
            if (aliasMap.containsKey(name)) {
                return aliasMap.get(name);
            }
            
            String name_lcase = name.toLowerCase();
            if (name_lcase != name && aliasMap.containsKey(name_lcase)) {
                return aliasMap.get(name_lcase);
            }
            
//            for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
//                if (entry.getKey() == null) {
//                    continue;
//                }
//                if (entry.getKey().equalsIgnoreCase(name)) {
//                    return entry.getValue();
//                }
//            }
        }

        return name;
    }

    protected Column handleSubQueryColumn(String owner, String alias) {
        SQLObject query = getSubQuery(owner);

        if (query == null) {
            return null;
        }
        
        return handleSubQueryColumn(alias, query);
    }

    protected Column handleSubQueryColumn(String alias, SQLObject query) {
        if (query instanceof SQLSelect) {
            query = ((SQLSelect) query).getQuery();
        }

        SQLSelectQueryBlock queryBlock = null;
        List<SQLSelectItem> selectList = null;
        if (query instanceof SQLSelectQueryBlock) {
            queryBlock = (SQLSelectQueryBlock) query;

            if (queryBlock.getGroupBy() != null) {
                return null;
            }

            selectList = queryBlock.getSelectList();
        }

        if (selectList != null) {
            boolean allColumn = false;
            String allColumnOwner = null;
            for (SQLSelectItem item : selectList) {
                if (!item.getClass().equals(SQLSelectItem.class)) {
                    continue;
                }

                String itemAlias = item.getAlias();
                SQLExpr itemExpr = item.getExpr();
                
                String ident = itemAlias;
                if (itemAlias == null) {
                    if (itemExpr instanceof SQLIdentifierExpr) {
                        ident = itemAlias = itemExpr.toString();
                    } else if (itemExpr instanceof SQLPropertyExpr) {
                        itemAlias = ((SQLPropertyExpr) itemExpr).getName();
                        ident = itemExpr.toString();
                    }
                }

                if (alias.equalsIgnoreCase(itemAlias)) {
                    Column column = (Column) itemExpr.getAttribute(ATTR_COLUMN);
                    if (column != null) {
                        return column;
                    } else {
                        SQLTableSource from = queryBlock.getFrom();
                        if (from instanceof SQLSubqueryTableSource) {
                            SQLSelect select = ((SQLSubqueryTableSource) from).getSelect();
                            Column subQueryColumn = handleSubQueryColumn(ident, select);
                            return subQueryColumn;
                        }
                    }
                }

                if (itemExpr instanceof SQLAllColumnExpr) {
                    allColumn = true;
                } else if (itemExpr instanceof SQLPropertyExpr) {
                    SQLPropertyExpr propertyExpr = (SQLPropertyExpr) itemExpr;
                    if (propertyExpr.getName().equals("*")) {
                        SQLExpr owner = propertyExpr.getOwner();
                        if (owner instanceof SQLIdentifierExpr) {
                            allColumnOwner = ((SQLIdentifierExpr) owner).getName();
                            allColumn = true;
                        }
                    }
                }
            }

            if (allColumn) {

                SQLTableSource from = queryBlock.getFrom();
                String tableName = getTable(from, allColumnOwner);
                if (tableName != null) {
                    return new Column(tableName, alias);
                }
            }
        }

        return null;
    }

    private String getTable(SQLTableSource from, String tableAlias) {
        if (from instanceof SQLExprTableSource) {
            boolean aliasEq = StringUtils.equals(from.getAlias(), tableAlias);
            SQLExpr tableSourceExpr = ((SQLExprTableSource) from).getExpr();
            if (tableSourceExpr instanceof SQLName) {
                String tableName = ((SQLName) tableSourceExpr).toString();
                if (tableAlias == null || aliasEq) {
                    return tableName;
                }
            }
        } else if (from instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinTableSource = (SQLJoinTableSource) from;
            String leftMatchTableName = getTable(joinTableSource.getLeft(), tableAlias);
            if (leftMatchTableName != null) {
                return leftMatchTableName;
            }

            return getTable(joinTableSource.getRight(), tableAlias);
        }

        return null;
    }

    public boolean visit2(SQLIdentifierExpr x) {
        if (isParam(x)) {
            return false;
        }

        Column column = null;
        String ident = x.getName();

        SQLTableSource tableSource = x.getResolvedTableSource();
        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr table = (SQLIdentifierExpr) expr;
                column = addColumn(table.getName(), ident, table.name_hash_lower(), x.name_hash_lower());

                if (column != null && isParentGroupBy(x)) {
                    this.groupByColumns.add(column);
                }
                x.putAttribute(ATTR_COLUMN, column);
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr table = (SQLPropertyExpr) expr;
                String tableName = table.toString();
                column = addColumn(tableName, ident, 0, x.name_hash_lower());

                if (column != null && isParentGroupBy(x)) {
                    this.groupByColumns.add(column);
                }
                x.putAttribute(ATTR_COLUMN, column);
            }
        } else {
            boolean skip = false;
            for (SQLObject parent = x.getParent();parent != null;parent = parent.getParent()) {
                if (parent instanceof SQLSelectQueryBlock) {
                    SQLTableSource from = ((SQLSelectQueryBlock) parent).getFrom();

                    if (from instanceof OdpsValuesTableSource) {
                        skip = true;
                        break;
                    }
                } else if (parent instanceof SQLSelectQuery) {
                    break;
                }
            }
            if (!skip) {
                column = handleUnkownColumn(ident);
            }
            if (column != null) {
                x.putAttribute(ATTR_COLUMN, column);
            }
        }

        if (column != null) {
            SQLObject parent = x.getParent();
            if (parent instanceof SQLSelectOrderByItem) {
                parent = parent.getParent();
            }
            if (parent instanceof SQLPrimaryKey) {
                column.setPrimaryKey(true);
            } else if (parent instanceof SQLUnique) {
                column.setUnique(true);
            }

            setColumn(x, column);
        }

        return false;
    }

    public boolean visit(SQLIdentifierExpr x) {
        if (isParam(x)) {
            return false;
        }

        String currentTable = getCurrentTable();

        if (containsSubQuery(currentTable)) {
            return false;
        }

        String ident = x.toString();

        if (variants.containsKey(ident)) {
            return false;
        }

        if ("LEVEL".equalsIgnoreCase(ident) || "CONNECT_BY_ISCYCLE".equalsIgnoreCase(ident)) {
            for (SQLObject parent = x.getParent(); parent != null; parent = parent.getParent()) {
                if (parent instanceof SQLSelectQueryBlock) {
                    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;
                    if (queryBlock.getStartWith() != null || queryBlock.getConnectBy() != null) {
                        return false;
                    }
                    break;
                }
            }
        }

        Column column = null;
        if (currentTable != null) {
            column = addColumn(currentTable, ident, 0, x.name_hash_lower());
            
            if (column != null && isParentGroupBy(x)) {
                this.groupByColumns.add(column);
            }
            x.putAttribute(ATTR_COLUMN, column);
        } else {
            boolean skip = false;
            for (SQLObject parent = x.getParent();parent != null;parent = parent.getParent()) {
                if (parent instanceof SQLSelectQueryBlock) {
                    SQLTableSource from = ((SQLSelectQueryBlock) parent).getFrom();

                    if (from instanceof OdpsValuesTableSource) {
                        skip = true;
                        break;
                    }
                } else if (parent instanceof SQLSelectQuery) {
                    break;
                }
            }
            if (!skip) {
                column = handleUnkownColumn(ident);
            }
            if (column != null) {
                x.putAttribute(ATTR_COLUMN, column);
            }
        }
        if (column != null) {
            SQLObject parent = x.getParent();
            if (parent instanceof SQLSelectOrderByItem) {
                parent = parent.getParent();
            }
            if (parent instanceof SQLPrimaryKey) {
                column.setPrimaryKey(true);
            } else if (parent instanceof SQLUnique) {
                column.setUnique(true);
            }

            setColumn(x, column);
        }
        return false;
    }
    
    private boolean isParentSelectItem(SQLObject parent) {
        if (parent == null) {
            return false;
        }
        
        if (parent instanceof SQLSelectItem) {
            return true;
        }
        
        if (parent instanceof SQLSelectQueryBlock) {
            return false;
        }
        
        return isParentSelectItem(parent.getParent());
    }
    
    private boolean isParentGroupBy(SQLObject parent) {
        if (parent == null) {
            return false;
        }
        
        if (parent instanceof SQLSelectGroupByClause) {
            return true;
        }
        
        return isParentGroupBy(parent.getParent());
    }

    private void setColumn(SQLExpr x, Column column) {
        SQLObject current = x;
        for (;;) {
            SQLObject parent = current.getParent();

            if (parent == null) {
                break;
            }

            if (parent instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock query = (SQLSelectQueryBlock) parent;
                if (query.getWhere() == current) {
                    column.setWhere(true);
                }
                break;
            }

            if (parent instanceof SQLSelectGroupByClause) {
                SQLSelectGroupByClause groupBy = (SQLSelectGroupByClause) parent;
                if (current == groupBy.getHaving()) {
                    column.setHaving(true);
                } else if (groupBy.getItems().contains(current)) {
                    column.setGroupBy(true);
                }
                break;
            }

            if (isParentSelectItem(parent)) {
                column.setSelec(true);
                break;
            }

            if (parent instanceof SQLJoinTableSource) {
                SQLJoinTableSource join = (SQLJoinTableSource) parent;
                if (join.getCondition() == current) {
                    column.setJoin(true);
                }
                break;
            }

            current = parent;
        }
    }

    protected Column handleUnkownColumn(String columnName) {
        return addColumn("UNKNOWN", columnName);
    }

    public boolean visit(SQLAllColumnExpr x) {
        String currentTable = getCurrentTable();

        if (containsSubQuery(currentTable)) {
            return false;
        }
        
        if (x.getParent() instanceof SQLAggregateExpr) {
            SQLAggregateExpr aggregateExpr = (SQLAggregateExpr) x.getParent();
            if ("count".equalsIgnoreCase(aggregateExpr.getMethodName())) {
                return false;
            }
        }

        if (currentTable != null) {
            Column column = addColumn(currentTable, "*");
            if (isParentSelectItem(x.getParent())) {
                column.setSelec(true);
            }
        }
        return false;
    }

    public Map<TableStat.Name, TableStat> getTables() {
        return tableStats;
    }

    public boolean containsTable(String tableName) {
        return tableStats.containsKey(new TableStat.Name(tableName));
    }

    public boolean containsColumn(String tableName, String columnName) {
        return columns.containsKey(new Column(tableName, columnName));
    }

    public Collection<Column> getColumns() {
        return columns.values();
    }

    public Column getColumn(String tableName, String columnName) {
        if (aliasMap != null && aliasMap.containsKey(columnName) && aliasMap.get(columnName) == null) {
            return null;
        }
        
        Column column = new Column(tableName, columnName);
        
        return this.columns.get(column);
    }

    public boolean visit(SQLSelectStatement x) {
        if (repository != null) {
            repository.resolve(x);
        }

        setAliasMap();
        return true;
    }

    public void endVisit(SQLSelectStatement x) {
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        String alias = x.getAlias();
        Map<String, String> aliasMap = getAliasMap();
        SQLWithSubqueryClause with = (SQLWithSubqueryClause) x.getParent();

        if (Boolean.TRUE == with.getRecursive()) {

            if (aliasMap != null && alias != null) {
                putAliasMap(aliasMap, alias, null);
                addSubQuery(alias, x);
            }

            SQLSelect select = x.getSubQuery();
            if (select != null) {
                select.accept(this);
            } else {
                x.getReturningStatement().accept(this);
            }
        } else {
            SQLSelect select = x.getSubQuery();
            if (select != null) {
                select.accept(this);
            } else {
                x.getReturningStatement().accept(this);
            }

            if (aliasMap != null && alias != null) {
                putAliasMap(aliasMap, alias, null);
                addSubQuery(alias, x);
            }
        }

        return false;
    }

    public boolean visit(SQLSubqueryTableSource x) {
        x.getSelect().accept(this);

        SQLSelectQuery query = x.getSelect().getQuery();

        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap != null && x.getAlias() != null) {
            putAliasMap(aliasMap, x.getAlias(), null);
            addSubQuery(x.getAlias(), query);
        }
        return false;
    }
    
    protected void addSubQuery(String alias, SQLObject query) {
        String alias_lcase = alias.toLowerCase();
        subQueryMap.put(alias_lcase, query);
    }
    
    protected SQLObject getSubQuery(String alias) {
        String alias_lcase = alias.toLowerCase();
        return subQueryMap.get(alias_lcase);
    }
    
    protected boolean containsSubQuery(String alias) {
        if (alias == null) {
            return false;
        }
        
        String alias_lcase = alias.toLowerCase();
        return subQueryMap.containsKey(alias_lcase);
    }

    protected boolean isSimpleExprTableSource(SQLExprTableSource x) {
        return x.getExpr() instanceof SQLName;
    }

    public boolean visit(SQLExprTableSource x) {
        if (isSimpleExprTableSource(x)) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr && isParam((SQLIdentifierExpr) expr)) {
                return false;
            }

            String ident = expr.toString();

            if (variants.containsKey(ident)) {
                return false;
            }

            if (containsSubQuery(ident)) {
                return false;
            }

            Map<String, String> aliasMap = getAliasMap();

            TableStat stat = getTableStat(ident);

            Mode mode = getMode();
            if (mode != null) {
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
                    case Drop:
                        stat.incrementDropCount();
                        break;
                    default:
                        break;
                }
            }

            if (aliasMap != null) {
                String alias = x.getAlias();
                if (alias != null) {
                    String table = aliasMap.get(alias);
                    if (!(ident.equals(table) || alias.equals(table))) {
                        putAliasMap(aliasMap, alias, ident);
                    }
                }
                if (!aliasMap.containsKey(ident)) {
                    putAliasMap(aliasMap, ident, ident);
                }
            }
        } else {
            accept(x.getExpr());
        }

        return false;
    }

    public boolean visit(SQLSelectItem x) {
        x.getExpr().accept(this);
        
        String alias = x.getAlias();
        
        Map<String, String> aliasMap = this.getAliasMap();
        if (alias != null && (!alias.isEmpty()) && aliasMap != null) {
            if (x.getExpr() instanceof SQLName) {
                putAliasMap(aliasMap, alias, x.getExpr().toString());
            } else {
                putAliasMap(aliasMap, alias, null);
            }
        }
        
        return false;
    }

    public void endVisit(SQLSelect x) {
        restoreCurrentTable(x);
    }

    public boolean visit(SQLSelect x) {
        setCurrentTable(x);

        if (x.getOrderBy() != null) {
            x.getOrderBy().setParent(x);
        }

        accept(x.getWithSubQuery());
        accept(x.getQuery());

        String originalTable = getCurrentTable();

        setCurrentTable((String) x.getQuery().getAttribute("table"));
        x.putAttribute("_old_local_", originalTable);

        accept(x.getOrderBy());

        setCurrentTable(originalTable);

        return false;
    }

    public boolean visit(SQLAggregateExpr x) {
        this.aggregateFunctions.add(x);
        
        accept(x.getArguments());
        accept(x.getWithinGroup());
        accept(x.getOver());
        return false;
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        this.functions.add(x);

        accept(x.getParameters());
        return false;
    }

    public boolean visit(SQLUpdateStatement x) {
        if (repository != null) {
            repository.resolve(x);
        }
        setAliasMap();

        setMode(x, Mode.Update);

        SQLTableSource tableSource = x.getTableSource();
        if (tableSource instanceof SQLExprTableSource) {
            SQLName identName = ((SQLExprTableSource) tableSource).getName();
            String ident = identName.toString();
            setCurrentTable(ident);

            TableStat stat = getTableStat(ident);
            stat.incrementUpdateCount();

            Map<String, String> aliasMap = getAliasMap();
            putAliasMap(aliasMap, ident, ident);
        } else {
            tableSource.accept(this);
        }

        accept(x.getFrom());

        accept(x.getItems());
        accept(x.getWhere());

        return false;
    }

    public boolean visit(SQLDeleteStatement x) {
        if (repository != null) {
            repository.resolve(x);
        }

        setAliasMap();

        setMode(x, Mode.Delete);

        String tableName = x.getTableName().toString();
        setCurrentTable(tableName);

        if (x.getAlias() != null) {
            putAliasMap(this.aliasMap, x.getAlias(), tableName);
        }

        if (x.getTableSource() instanceof SQLSubqueryTableSource) {
            SQLSelectQuery selectQuery = ((SQLSubqueryTableSource) x.getTableSource()).getSelect().getQuery();
            if (selectQuery instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock subQueryBlock = ((SQLSelectQueryBlock) selectQuery);
                subQueryBlock.getWhere().accept(this);
            }
        }

        TableStat stat = getTableStat(tableName);
        stat.incrementDeleteCount();

        accept(x.getWhere());

        return false;
    }

    public boolean visit(SQLInListExpr x) {
        if (x.isNot()) {
            handleCondition(x.getExpr(), "NOT IN", x.getTargetList());
        } else {
            handleCondition(x.getExpr(), "IN", x.getTargetList());
        }

        return true;
    }

    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        if (x.isNot()) {
            handleCondition(x.getExpr(), "NOT IN");
        } else {
            handleCondition(x.getExpr(), "IN");
        }
        return true;
    }

    public void endVisit(SQLDeleteStatement x) {

    }

    public void endVisit(SQLUpdateStatement x) {
    }

    public boolean visit(SQLCreateTableStatement x) {
        for (SQLTableElement e : x.getTableElementList()) {
            e.setParent(x);
        }

        String tableName = x.getName().toString();

        TableStat stat = getTableStat(tableName);
        stat.incrementCreateCount();
        setCurrentTable(x, tableName);

        accept(x.getTableElementList());

        restoreCurrentTable(x);

        if (x.getInherits() != null) {
            x.getInherits().accept(this);
        }

        if (x.getSelect() != null) {
            x.getSelect().accept(this);
        }

        return false;
    }

    public boolean visit(SQLColumnDefinition x) {
        String tableName = null;
        {
            SQLObject parent = x.getParent();
            if (parent instanceof SQLCreateTableStatement) {
                tableName = ((SQLCreateTableStatement) parent).getName().toString();
            }
        }

        if (tableName == null) {
            return true;
        }

        String columnName = x.getName().toString();
        Column column = addColumn(tableName, columnName);
        if (x.getDataType() != null) {
            column.setDataType(x.getDataType().getName());
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            if (item instanceof SQLPrimaryKey) {
                column.setPrimaryKey(true);
            } else if (item instanceof SQLUnique) {
                column.setUnique(true);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLCallStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLCommentStatement x) {

    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        return false;
    }

    public boolean visit(SQLCurrentOfCursorExpr x) {
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
        String table = stmt.getName().toString();

        for (SQLColumnDefinition column : x.getColumns()) {
            String columnName = column.getName().toString();
            addColumn(table, columnName);
        }
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableAddColumn x) {

    }

    @Override
    public boolean visit(SQLRollbackStatement x) {
        return false;
    }

    public boolean visit(SQLCreateViewStatement x) {
        x.getSubQuery().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropForeignKey x) {
        return false;
    }

    @Override
    public boolean visit(SQLUseStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDisableConstraint x) {
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableEnableConstraint x) {
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        String tableName = x.getName().toString();
        TableStat stat = getTableStat(tableName);
        stat.incrementAlterCount();

        setCurrentTable(x, tableName);

        for (SQLAlterTableItem item : x.getItems()) {
            item.setParent(x);
            item.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropConstraint x) {
        return false;
    }

    @Override
    public boolean visit(SQLDropIndexStatement x) {
        setMode(x, Mode.DropIndex);
        SQLExprTableSource table = x.getTableName();
        if (table != null) {
            SQLName name = (SQLName) table.getExpr();

            String ident = name.toString();
            setCurrentTable(ident);

            TableStat stat = getTableStat(ident);
            stat.incrementDropIndexCount();

            Map<String, String> aliasMap = getAliasMap();
            putAliasMap(aliasMap, ident, ident);
        }
        return false;
    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        setMode(x, Mode.CreateIndex);

        SQLName name = (SQLName) ((SQLExprTableSource) x.getTable()).getExpr();

        String table = name.toString();
        setCurrentTable(table);

        TableStat stat = getTableStat(table);
        stat.incrementCreateIndexCount();

        Map<String, String> aliasMap = getAliasMap();
        putAliasMap(aliasMap, table, table);

        for (SQLSelectOrderByItem item : x.getItems()) {
            SQLExpr expr = item.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                String columnName = identExpr.getName();
                addColumn(table, columnName);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLForeignKeyImpl x) {

        for (SQLName column : x.getReferencingColumns()) {
            column.accept(this);
        }

        String table = x.getReferencedTableName().getSimpleName();
        setCurrentTable(table);

        TableStat stat = getTableStat(table);
        stat.incrementReferencedCount();
        for (SQLName column : x.getReferencedColumns()) {
            String columnName = column.getSimpleName();
            addColumn(table, columnName);
        }

        return false;
    }

    @Override
    public boolean visit(SQLDropSequenceStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLDropTriggerStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLDropUserStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLGrantStatement x) {
        if (x.getOn() != null && (x.getObjectType() == null || x.getObjectType() == SQLObjectType.TABLE)) {
            x.getOn().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLRevokeStatement x) {
        if (x.getOn() != null) {
            x.getOn().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddIndex x) {
        for (SQLSelectOrderByItem item : x.getItems()) {
            item.accept(this);
        }

        String table = ((SQLAlterTableStatement) x.getParent()).getName().toString();
        TableStat tableStat = this.getTableStat(table);
        tableStat.incrementCreateIndexCount();
        return false;
    }

    public boolean visit(SQLCheck x) {
        x.getExpr().accept(this);
        return false;
    }

    public boolean visit(SQLCreateTriggerStatement x) {
        return false;
    }

    public boolean visit(SQLDropFunctionStatement x) {
        return false;
    }

    public boolean visit(SQLDropTableSpaceStatement x) {
        return false;
    }

    public boolean visit(SQLDropProcedureStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableRename x) {
        return false;
    }

    @Override
    public boolean visit(SQLArrayExpr x) {
        accept(x.getValues());

        SQLExpr exp = x.getExpr();
        if (exp instanceof SQLIdentifierExpr) {
            if (((SQLIdentifierExpr) exp).getName().equals("ARRAY")) {
                return false;
            }
        }
        exp.accept(this);
        return false;
    }
    
    @Override
    public boolean visit(SQLOpenStatement x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLFetchStatement x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLCloseStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLCreateProcedureStatement x) {
        String name = x.getName().toString();
        this.variants.put(name, x);
        accept(x.getBlock());
        return false;
    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
        String name = x.getName().toString();
        this.variants.put(name, x);
        accept(x.getBlock());
        return false;
    }
    
    @Override
    public boolean visit(SQLBlockStatement x) {
        for (SQLParameter param : x.getParameters()) {
            param.setParent(x);

            SQLExpr name = param.getName();
            this.variants.put(name.toString(), name);

            param.accept(this);
        }

        for (SQLStatement stmt : x.getStatementList()) {
            stmt.accept(this);
        }

        SQLStatement exception = x.getException();
        if (exception != null) {
            exception.accept(this);
        }

        return false;
    }
    
    @Override
    public boolean visit(SQLShowTablesStatement x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLDeclareItem x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLPartitionByHash x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLPartitionByRange x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLPartitionByList x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLPartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLSubPartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLSubPartitionByHash x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLPartitionValue x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterDatabaseStatement x) {
        return true;
    }
    
    @Override
    public boolean visit(SQLAlterTableConvertCharSet x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableDropPartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableReOrganizePartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableCoalescePartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableTruncatePartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableDiscardPartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableImportPartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableAnalyzePartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableCheckPartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableOptimizePartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableRebuildPartition x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableRepairPartition x) {
        return false;
    }
    
    public boolean visit(SQLSequenceExpr x) {
        return false;
    }
    
    @Override
    public boolean visit(SQLMergeStatement x) {
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
                putAliasMap(aliasMap, x.getAlias(), ident);
            }
            putAliasMap(aliasMap, ident, ident);
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
    public boolean visit(SQLSetStatement x) {
        return false;
    }

    public List<SQLMethodInvokeExpr> getFunctions() {
        return this.functions;
    }

    public boolean visit(SQLCreateSequenceStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddConstraint x) {
        SQLConstraint constraint = x.getConstraint();
        if (constraint instanceof SQLUniqueConstraint) {
            SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
            String tableName = stmt.getName().toString();

            TableStat tableStat = this.getTableStat(tableName);
            tableStat.incrementCreateIndexCount();
        }
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableDropIndex x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
        String tableName = stmt.getName().toString();

        TableStat tableStat = this.getTableStat(tableName);
        tableStat.incrementDropIndexCount();
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
        String tableName = stmt.getName().toString();

        TableStat tableStat = this.getTableStat(tableName);
        tableStat.incrementDropIndexCount();
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropKey x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
        String tableName = stmt.getName().toString();

        TableStat tableStat = this.getTableStat(tableName);
        tableStat.incrementDropIndexCount();
        return false;
    }

    @Override
    public boolean visit(SQLDescribeStatement x) {
        String tableName = x.getObject().toString();

        TableStat tableStat = this.getTableStat(tableName);
        tableStat.incrementDropIndexCount();

        SQLName column = x.getColumn();
        if (column != null) {
            String columnName = column.toString();
            this.addColumn(tableName, columnName);
        }
        return false;
    }

    public boolean visit(SQLExplainStatement x) {
        if (x.getStatement() != null) {
            accept(x.getStatement());
        }

        return false;
    }

    @Override
    public boolean visit(SQLDeclareStatement x) {
        for (SQLDeclareItem item : x.getItems()) {
            item.setParent(x);

            SQLExpr name = item.getName();
            this.variants.put(name.toString(), name);
        }
        return true;
    }
}
