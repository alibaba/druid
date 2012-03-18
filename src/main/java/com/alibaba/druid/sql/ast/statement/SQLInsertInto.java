package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;

public abstract class SQLInsertInto extends SQLObjectImpl {

    private static final long     serialVersionUID = 1L;
    protected SQLExprTableSource  tableSource;

    protected final List<SQLExpr> columns          = new ArrayList<SQLExpr>();
    protected ValuesClause        values;
    protected SQLSelect           query;

    public SQLInsertInto(){

    }

    public String getAlias() {
        return tableSource.getAlias();
    }

    public void setAlias(String alias) {
        this.tableSource.setAlias(alias);
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        this.tableSource = tableSource;
    }

    public SQLName getTableName() {
        return (SQLName) tableSource.getExpr();
    }

    public void setTableName(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public void setTableSource(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect query) {
        this.query = query;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public ValuesClause getValues() {
        return values;
    }

    public void setValues(ValuesClause values) {
        this.values = values;
    }
}
