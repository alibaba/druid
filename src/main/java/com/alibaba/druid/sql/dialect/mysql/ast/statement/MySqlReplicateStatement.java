package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlReplicateStatement extends MySqlStatementImpl {
    private static final long serialVersionUID = 1L;

    private boolean lowPriority = false;
    private boolean delayed = false;

    private SQLName tableName;
    private final List<SQLExpr> columns = new ArrayList<SQLExpr>();
    private List<ValuesClause> valuesList = new ArrayList<ValuesClause>();
    private List<SQLUpdateSetItem> setItems = new ArrayList<SQLUpdateSetItem>();
    private SQLQueryExpr query;

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public SQLQueryExpr getQuery() {
        return query;
    }

    public void setQuery(SQLQueryExpr query) {
        query.setParent(this);
        this.query = query;
    }

    public List<ValuesClause> getValuesList() {
        return valuesList;
    }

    public List<SQLUpdateSetItem> getSetItems() {
        return setItems;
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableName);
            acceptChild(visitor, columns);
            acceptChild(visitor, valuesList);
            acceptChild(visitor, setItems);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }
}
