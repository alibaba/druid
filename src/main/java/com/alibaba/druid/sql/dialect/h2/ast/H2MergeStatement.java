package com.alibaba.druid.sql.dialect.h2.ast;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;

import java.util.ArrayList;
import java.util.List;

public class H2MergeStatement extends SQLStatementImpl {
    private SQLExprTableSource table;

    private final List<SQLName> keyColumns = new ArrayList<SQLName>();

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }

    public void setTable(SQLExprTableSource table) {
        if (table != null) {
            table.setParent(this);
        }
        this.table = table;
    }

    public List<SQLName> getKeyColumns() {
        return keyColumns;
    }
}
