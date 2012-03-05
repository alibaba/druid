package com.alibaba.druid.sql.visitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Condition;
import com.alibaba.druid.stat.TableStat.Mode;
import com.alibaba.druid.stat.TableStat.Relationship;

public class SchemaStatVisitor extends SQLASTVisitorAdapter {

    protected final HashMap<TableStat.Name, TableStat> tableStats     = new LinkedHashMap<TableStat.Name, TableStat>();
    protected final Set<Column>                        columns        = new LinkedHashSet<Column>();
    protected final Set<Condition>                     conditions     = new LinkedHashSet<Condition>();
    protected final Set<Relationship>                  relationships  = new LinkedHashSet<Relationship>();
    protected final Set<Column>                        orderByColumns = new LinkedHashSet<Column>();
    protected final Set<Column>                        groupByColumns = new LinkedHashSet<Column>();

    protected final Map<String, SQLObject>             subQueryMap    = new LinkedHashMap<String, SQLObject>();

    protected Map<String, String>                      aliasLocal     = new HashMap<String, String>();

    protected String                                   currentTable;

    public final static String                         ATTR_TABLE     = "_table_";
    public final static String                         ATTR_COLUMN    = "_column_";

    private Mode                                       mode;

    public void setAliasMap() {
        this.setAliasMap(new HashMap<String, String>());
    }

    public void clearAliasMap() {
        this.aliasLocal = null;
    }

    public void setAliasMap(Map<String, String> aliasMap) {
        this.aliasLocal = aliasMap;
    }

    public Map<String, String> getAliasMap() {
        return aliasLocal;
    }

    public void setCurrentTable(String table) {
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

    public class OrderByStatVisitor extends SQLASTVisitorAdapter {

        private final SQLOrderBy orderBy;

        public OrderByStatVisitor(SQLOrderBy orderBy){
            this.orderBy = orderBy;
        }

        public SQLOrderBy getOrderBy() {
            return orderBy;
        }

        public boolean visit(SQLIdentifierExpr x) {
            String currentTable = getCurrentTable();

            if (subQueryMap.containsKey(currentTable)) {
                return false;
            }

            if (currentTable != null) {
                orderByColumns.add(new Column(currentTable, x.getName()));
            } else {
                orderByColumns.add(new Column("UNKOWN", x.getName()));
            }
            return false;
        }

        public boolean visit(SQLPropertyExpr x) {
            if (x.getOwner() instanceof SQLIdentifierExpr) {
                String owner = ((SQLIdentifierExpr) x.getOwner()).getName();

                if (subQueryMap.containsKey(owner)) {
                    return false;
                }

                owner = aliasWrap(owner);

                if (owner != null) {
                    orderByColumns.add(new Column(owner, x.getName()));
                }
            }

            return false;
        }
    }

    public boolean visit(SQLOrderBy x) {
        OrderByStatVisitor orderByVisitor = new OrderByStatVisitor(x);
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
                }
            }
        }
        x.accept(orderByVisitor);
        return true;
    }

    public Set<Relationship> getRelationships() {
        return relationships;
    }

    public Set<Column> getOrderByColumns() {
        return orderByColumns;
    }

    public Set<Column> getGroupByColumns() {
        return groupByColumns;
    }

    public Set<Condition> getConditions() {
        return conditions;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        x.getLeft().setParent(x);
        x.getRight().setParent(x);

        switch (x.getOperator()) {
            case Equality:
            case NotEqual:
            case GreaterThan:
            case GreaterThanOrEqual:
            case LessThan:
            case LessThanOrEqual:
            case LessThanOrEqualOrGreaterThan:
            case Like:
            case NotLike:
            case Is:
            case IsNot:
                handleCondition(x.getLeft(), x.getOperator().name);
                handleCondition(x.getRight(), x.getOperator().name);

                handleRelationship(x.getLeft(), x.getOperator().name, x.getRight());
                break;
            default:
                break;
        }
        return true;
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

    protected void handleCondition(SQLExpr expr, String operator) {
        Column column = getColumn(expr);
        if (column == null) {
            return;
        }

        Condition condition = new Condition();
        condition.setColumn(column);
        condition.setOperator(operator);
        this.conditions.add(condition);
    }

    protected Column getColumn(SQLExpr expr) {
        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap == null) {
            return null;
        }

        if (expr instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) expr).getOwner();
            String column = ((SQLPropertyExpr) expr).getName();

            if (owner instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) owner).getName();
                String table = tableName;
                if (aliasMap.containsKey(table)) {
                    table = aliasMap.get(table);
                }

                if (table != null) {
                    return new Column(table, column);
                }

                return handleSubQueryColumn(tableName, column);
            }

            return null;
        }

        if (expr instanceof SQLIdentifierExpr) {
            Column attrColumn = (Column) expr.getAttribute(ATTR_COLUMN);
            if (attrColumn != null) {
                return attrColumn;
            }

            String column = ((SQLIdentifierExpr) expr).getName();
            String table = getCurrentTable();
            if (table != null) {
                if (aliasMap.containsKey(table)) {
                    table = aliasMap.get(table);
                    if (table == null) {
                        return null;
                    }
                }
            }

            if (table != null) {
                return new Column(table, column);
            }

            return new Column("UNKNOWN", column);
        }

        return null;
    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        setMode(x, Mode.Insert);

        setAliasMap();

        String originalTable = getCurrentTable();

        for (SQLName name : x.getTableNames()) {
            String ident = name.toString();
            setCurrentTable(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
            stat.incrementInsertCount();

            Map<String, String> aliasMap = getAliasMap();
            if (aliasMap != null) {
                aliasMap.put(ident, ident);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLDropTableStatement x) {
        setMode(x, Mode.Insert);

        setAliasMap();

        String originalTable = getCurrentTable();

        for (SQLName name : x.getTableNames()) {
            String ident = name.toString();
            setCurrentTable(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
            stat.incrementDropCount();

            Map<String, String> aliasMap = getAliasMap();
            if (aliasMap != null) {
                aliasMap.put(ident, ident);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        setMode(x, Mode.Insert);

        setAliasMap();

        String originalTable = getCurrentTable();

        if (x.getTableName() instanceof SQLName) {
            String ident = ((SQLName) x.getTableName()).toString();
            setCurrentTable(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
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

        return false;
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
        setMode(x, Mode.Select);

        if (x.getFrom() instanceof SQLSubqueryTableSource) {
            x.getFrom().accept(this);
            return false;
        }

        String originalTable = getCurrentTable();

        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableSource = (SQLExprTableSource) x.getFrom();
            if (tableSource.getExpr() instanceof SQLName) {
                String ident = tableSource.getExpr().toString();

                setCurrentTable(ident);
                x.putAttribute(ATTR_TABLE, ident);
                if (x.getParent() instanceof SQLSelect) {
                    x.getParent().putAttribute(ATTR_TABLE, ident);
                }
                x.putAttribute("_old_local_", originalTable);
            }
        }

        if (x.getFrom() != null) {
            x.getFrom().accept(this); // 提前执行，获得aliasMap
        }

        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
        }

        return true;
    }

    public void endVisit(SQLSelectQueryBlock x) {
        String originalTable = (String) x.getAttribute("_old_local_");
        x.putAttribute("table", getCurrentTable());
        setCurrentTable(originalTable);

        setModeOrigin(x);
    }

    public boolean visit(SQLJoinTableSource x) {
        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLIdentifierExpr) {
            String owner = ((SQLIdentifierExpr) x.getOwner()).getName();

            if (subQueryMap.containsKey(owner)) {
                return false;
            }

            owner = aliasWrap(owner);

            if (owner != null) {
                Column column = new Column(owner, x.getName());
                columns.add(column);
                x.putAttribute(ATTR_COLUMN, column);
            }
        }
        return false;
    }

    protected String aliasWrap(String name) {
        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap.containsKey(name)) {
            return aliasMap.get(name);
        }

        return name;
    }

    protected Column handleSubQueryColumn(String owner, String alias) {
        SQLObject query = subQueryMap.get(owner);

        if (query == null) {
            return null;
        }

        List<SQLSelectItem> selectList = null;
        if (query instanceof SQLSelectQueryBlock) {
            selectList = ((SQLSelectQueryBlock) query).getSelectList();
        }

        if (selectList != null) {
            for (SQLSelectItem item : selectList) {
                String itemAlias = item.getAlias();
                SQLExpr itemExpr = item.getExpr();
                if (itemAlias == null) {
                    if (itemExpr instanceof SQLIdentifierExpr) {
                        itemAlias = itemExpr.toString();
                    } else if (itemExpr instanceof SQLPropertyExpr) {
                        itemAlias = ((SQLPropertyExpr) itemExpr).getName();
                    }
                }

                if (alias.equalsIgnoreCase(itemAlias)) {
                    Column column = (Column) itemExpr.getAttribute(ATTR_COLUMN);
                    return column;
                }

            }
        }

        return null;
    }

    public boolean visit(SQLIdentifierExpr x) {
        String currentTable = getCurrentTable();

        if (subQueryMap.containsKey(currentTable)) {
            return false;
        }

        if (currentTable != null) {
            Column column = new Column(currentTable, x.getName());
            columns.add(column);
            x.putAttribute(ATTR_COLUMN, column);
        } else {
            Column column = handleUnkownColumn(x.getName());
            if (column != null) {
                x.putAttribute(ATTR_COLUMN, column);
            }
        }
        return false;
    }

    protected Column handleUnkownColumn(String columnName) {
        Column column = new Column("UNKNOWN", columnName);
        columns.add(column);
        return column;
    }

    public boolean visit(SQLAllColumnExpr x) {
        String currentTable = getCurrentTable();

        if (subQueryMap.containsKey(currentTable)) {
            return false;
        }

        if (currentTable != null) {
            columns.add(new Column(currentTable, "*"));
        }
        return false;
    }

    public Map<TableStat.Name, TableStat> getTables() {
        return tableStats;
    }

    public boolean containsTable(String tableName) {
        return tableStats.containsKey(new TableStat.Name(tableName));
    }

    public Set<Column> getColumns() {
        return columns;
    }

    public boolean visit(SQLSelectStatement x) {
        setAliasMap();
        return true;
    }

    public void endVisit(SQLSelectStatement x) {
        clearAliasMap();
    }

    public boolean visit(SQLSubqueryTableSource x) {
        x.getSelect().accept(this);

        SQLSelectQuery query = x.getSelect().getQuery();

        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap != null) {
            if (x.getAlias() != null) {
                aliasMap.put(x.getAlias(), null);
                subQueryMap.put(x.getAlias(), query);
            }
        }
        return false;
    }

    protected boolean isSimpleExprTableSource(SQLExprTableSource x) {
        return x.getExpr() instanceof SQLName;
    }

    public boolean visit(SQLExprTableSource x) {
        if (isSimpleExprTableSource(x)) {
            String ident = x.getExpr().toString();

            if (subQueryMap.containsKey(ident)) {
                return false;
            }

            Map<String, String> aliasMap = getAliasMap();

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
                x.putAttribute(ATTR_TABLE, ident);
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

            if (aliasMap != null) {
                if (x.getAlias() != null) {
                    aliasMap.put(x.getAlias(), ident);
                }
                aliasMap.put(ident, ident);
            }
        } else {
            accept(x.getExpr());
        }

        return false;
    }

    public boolean visit(SQLSelectItem x) {
        x.getExpr().setParent(x);
        return true;
    }

    public boolean visit(SQLSelect x) {
        if (x.getOrderBy() != null) {
            x.getOrderBy().setParent(x);
        }

        accept(x.getQuery());

        String originalTable = getCurrentTable();

        setCurrentTable((String) x.getQuery().getAttribute("table"));
        x.putAttribute("_old_local_", originalTable);

        accept(x.getOrderBy());

        setCurrentTable(originalTable);

        return false;
    }

    public boolean visit(SQLAggregateExpr x) {
        accept(x.getArguments());
        return false;
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        accept(x.getParameters());
        return false;
    }

    public boolean visit(SQLUpdateStatement x) {
        setAliasMap();

        String ident = x.getTableName().toString();
        setCurrentTable(ident);

        TableStat stat = tableStats.get(ident);
        if (stat == null) {
            stat = new TableStat();
            tableStats.put(new TableStat.Name(ident), stat);
        }
        stat.incrementUpdateCount();

        Map<String, String> aliasMap = getAliasMap();
        aliasMap.put(ident, ident);

        accept(x.getItems());
        accept(x.getWhere());

        return false;
    }

    public boolean visit(SQLDeleteStatement x) {
        setAliasMap();

        setMode(x, Mode.Delete);

        String ident = x.getTableName().toString();
        setCurrentTable(ident);

        TableStat stat = tableStats.get(ident);
        if (stat == null) {
            stat = new TableStat();
            tableStats.put(new TableStat.Name(ident), stat);
        }
        stat.incrementDeleteCount();

        accept(x.getWhere());

        return false;
    }

    public boolean visit(SQLInListExpr x) {
        if (x.isNot()) {
            handleCondition(x.getExpr(), "NOT IN");
        } else {
            handleCondition(x.getExpr(), "IN");
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
        clearAliasMap();
    }

    public void endVisit(SQLUpdateStatement x) {
        clearAliasMap();
    }

    public boolean visit(SQLCreateTableStatement x) {
        for (SQLTableElement e : x.getTableElementList()) {
            e.setParent(x);
        }

        String tableName = x.getName().toString();
        TableStat stat = new TableStat();
        stat.incrementCreateCount();
        tableStats.put(new TableStat.Name(tableName), stat);

        accept(x.getTableElementList());

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
        columns.add(new Column(tableName, columnName));

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
}
