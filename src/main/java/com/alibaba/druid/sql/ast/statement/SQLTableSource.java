package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObjectImpl;

public abstract class SQLTableSource extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    protected String alias;

    public SQLTableSource() {

    }

    public SQLTableSource(String alias) {

        this.alias = alias;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
