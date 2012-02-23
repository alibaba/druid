package com.alibaba.druid.sql.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Condition;
import com.alibaba.druid.stat.TableStat.Mode;

public class SchemaStatVisitor extends SQLASTVisitorAdapter {

    protected final HashMap<TableStat.Name, TableStat>      tableStats        = new HashMap<TableStat.Name, TableStat>();
    protected final Set<Column>                             fields            = new HashSet<Column>();
    protected final Set<Condition>                          conditions        = new HashSet<Condition>();

    protected final static ThreadLocal<Map<String, String>> aliasLocal        = new ThreadLocal<Map<String, String>>();
    protected final static ThreadLocal<String>              currentTableLocal = new ThreadLocal<String>();
    protected final static ThreadLocal<Mode>                modeLocal         = new ThreadLocal<Mode>();

    public Set<Condition> getConditions() {
        return conditions;
    }

    public boolean visit(SQLBinaryOpExpr x) {
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
                break;
            default:
                break;
        }
        return true;
    }

    protected void handleCondition(SQLExpr expr, String operator) {
        Map<String, String> aliasMap = aliasLocal.get();
        if (aliasMap == null) {
            return;
        }

        if (expr instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) expr).getOwner();
            String column = ((SQLPropertyExpr) expr).getName();

            if (owner instanceof SQLIdentifierExpr) {
                String table = ((SQLIdentifierExpr) owner).getName();
                if (aliasMap.containsKey(table)) {
                    table = aliasMap.get(table);
                }

                if (table != null) {
                    Condition condition = new Condition();
                    condition.setColumn(new Column(table, column));
                    condition.setOperator(operator);
                    this.conditions.add(condition);
                }
            }

            return;
        }

        if (expr instanceof SQLIdentifierExpr) {
            String column = ((SQLIdentifierExpr) expr).getName();
            String table = currentTableLocal.get();
            if (table != null) {
                if (aliasMap.containsKey(table)) {
                    table = aliasMap.get(table);
                }
            }

            if (table != null) {
                Condition condition = new Condition();
                condition.setColumn(new Column(table, column));
                condition.setOperator(operator);
                this.conditions.add(condition);
            }
        }
    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        x.putAttribute("_original_use_mode", modeLocal.get());
        modeLocal.set(Mode.Insert);

        aliasLocal.set(new HashMap<String, String>());

        String originalTable = currentTableLocal.get();

        for (SQLName name : x.getTableNames()) {
            String ident = name.toString();
            currentTableLocal.set(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
            stat.incrementInsertCount();

            Map<String, String> aliasMap = aliasLocal.get();
            if (aliasMap != null) {
                aliasMap.put(ident, ident);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLDropTableStatement x) {
        x.putAttribute("_original_use_mode", modeLocal.get());
        modeLocal.set(Mode.Insert);

        aliasLocal.set(new HashMap<String, String>());

        String originalTable = currentTableLocal.get();

        for (SQLName name : x.getTableNames()) {
            String ident = name.toString();
            currentTableLocal.set(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
            stat.incrementDropCount();

            Map<String, String> aliasMap = aliasLocal.get();
            if (aliasMap != null) {
                aliasMap.put(ident, ident);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        x.putAttribute("_original_use_mode", modeLocal.get());
        modeLocal.set(Mode.Insert);

        aliasLocal.set(new HashMap<String, String>());

        String originalTable = currentTableLocal.get();

        if (x.getTableName() instanceof SQLName) {
            String ident = ((SQLName) x.getTableName()).toString();
            currentTableLocal.set(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
            stat.incrementInsertCount();

            Map<String, String> aliasMap = aliasLocal.get();
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

        if (x.getFrom() instanceof SQLSubqueryTableSource) {
            x.getFrom().accept(this);
            return false;
        }

        x.putAttribute("_original_use_mode", modeLocal.get());
        modeLocal.set(Mode.Select);

        String originalTable = currentTableLocal.get();

        if (x.getFrom() instanceof SQLExprTableSource) {
            SQLExprTableSource tableSource = (SQLExprTableSource) x.getFrom();
            if (tableSource.getExpr() instanceof SQLName) {
                String ident = tableSource.getExpr().toString();

                Map<String, String> aliasMap = aliasLocal.get();
                if (aliasMap.containsKey(ident) && aliasMap.get(ident) == null) {
                    currentTableLocal.set(null);
                } else {
                    currentTableLocal.set(ident);
                }
                x.putAttribute("_old_local_", originalTable);
            }
        }

        x.getFrom().accept(this); // 提前执行，获得aliasMap

        return true;
    }

    public void endVisit(SQLSelectQueryBlock x) {
        String originalTable = (String) x.getAttribute("_old_local_");
        x.putAttribute("table", currentTableLocal.get());
        currentTableLocal.set(originalTable);

        Mode originalMode = (Mode) x.getAttribute("_original_use_mode");
        modeLocal.set(originalMode);
    }

    public boolean visit(SQLJoinTableSource x) {
        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLIdentifierExpr) {
            String owner = ((SQLIdentifierExpr) x.getOwner()).getName();

            if (owner != null) {
                Map<String, String> aliasMap = aliasLocal.get();
                if (aliasMap != null) {
                    String table = aliasMap.get(owner);

                    // table == null时是SubQuery
                    if (table != null) {
                        fields.add(new Column(table, x.getName()));
                    }
                }
            }
        }
        return false;
    }

    public boolean visit(SQLIdentifierExpr x) {
        String currentTable = currentTableLocal.get();

        if (currentTable != null) {
            fields.add(new Column(currentTable, x.getName()));
        }
        return false;
    }

    public boolean visit(SQLAllColumnExpr x) {
        String currentTable = currentTableLocal.get();

        if (currentTable != null) {
            fields.add(new Column(currentTable, "*"));
        }
        return false;
    }

    public Map<TableStat.Name, TableStat> getTables() {
        return tableStats;
    }

    public boolean containsTable(String tableName) {
        return tableStats.containsKey(new TableStat.Name(tableName));
    }

    public Set<Column> getFields() {
        return fields;
    }

    public boolean visit(SQLSelectStatement x) {
        aliasLocal.set(new HashMap<String, String>());
        return true;
    }

    public void endVisit(SQLSelectStatement x) {
        aliasLocal.set(null);
    }

    public boolean visit(SQLSubqueryTableSource x) {
        Map<String, String> aliasMap = aliasLocal.get();
        if (aliasMap != null) {
            if (x.getAlias() != null) {
                aliasMap.put(x.getAlias(), null);
            }
        }
        return true;
    }

    protected boolean isSimpleExprTableSource(SQLExprTableSource x) {
        return x.getExpr() instanceof SQLName;
    }

    public boolean visit(SQLExprTableSource x) {
        if (isSimpleExprTableSource(x)) {
            String ident = x.getExpr().toString();

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
        } else {
            accept(x.getExpr());
        }

        return false;
    }

    public boolean visit(SQLSelect x) {
        accept(x.getQuery());

        String originalTable = currentTableLocal.get();

        currentTableLocal.set((String) x.getQuery().getAttribute("table"));
        x.putAttribute("_old_local_", originalTable);

        accept(x.getOrderBy());

        currentTableLocal.set(originalTable);

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
        aliasLocal.set(new HashMap<String, String>());

        String ident = x.getTableName().toString();
        currentTableLocal.set(ident);

        TableStat stat = tableStats.get(ident);
        if (stat == null) {
            stat = new TableStat();
            tableStats.put(new TableStat.Name(ident), stat);
        }
        stat.incrementUpdateCount();

        Map<String, String> aliasMap = aliasLocal.get();
        aliasMap.put(ident, ident);

        accept(x.getItems());
        accept(x.getWhere());

        return false;
    }

    public boolean visit(SQLDeleteStatement x) {
        aliasLocal.set(new HashMap<String, String>());

        x.putAttribute("_original_use_mode", modeLocal.get());
        modeLocal.set(Mode.Delete);

        String ident = ((SQLIdentifierExpr) x.getTableName()).getName();
        currentTableLocal.set(ident);

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
        aliasLocal.set(null);
    }

    public void endVisit(SQLUpdateStatement x) {
        aliasLocal.set(null);
    }
}
