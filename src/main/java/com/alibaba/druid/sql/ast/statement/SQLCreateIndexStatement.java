package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;

public class SQLCreateIndexStatement extends SQLStatementImpl implements SQLDDLStatement {

    /**
     * 
     */
    private static final long          serialVersionUID = 1L;

    private SQLName                    name;

    private SQLName                    table;

    private List<SQLSelectOrderByItem> items            = new ArrayList<SQLSelectOrderByItem>();

    private String                     type;

    public SQLName getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        this.table = table;
    }

    public List<SQLSelectOrderByItem> getItems() {
        return items;
    }

    public void setItems(List<SQLSelectOrderByItem> items) {
        this.items = items;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
