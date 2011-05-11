package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;

public class MySqlSQLColumnDefinition extends SQLColumnDefinition {
    private static final long serialVersionUID = 1L;

    private boolean autoIncrement = false;

    public MySqlSQLColumnDefinition() {

    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

}
