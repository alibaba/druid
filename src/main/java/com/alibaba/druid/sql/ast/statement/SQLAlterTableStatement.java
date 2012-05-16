package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatementImpl;

public class SQLAlterTableStatement extends SQLStatementImpl implements SQLDDLStatement {

    private static final long       serialVersionUID = 1L;

    private SQLExprTableSource      tableSource;
    private List<SQLAlterTableItem> items            = new ArrayList<SQLAlterTableItem>();

    public List<SQLAlterTableItem> getItems() {
        return items;
    }

    public void setItems(List<SQLAlterTableItem> items) {
        this.items = items;
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        this.tableSource = tableSource;
    }

}
