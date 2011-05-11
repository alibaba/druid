package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;

@SuppressWarnings("serial")
public class MySqlCreateTableStatement extends SQLCreateTableStatement {

    private boolean ifNotExiists = false;

    private Map<String, String> tableOptions = new HashMap<String, String>();

    protected SQLSelect query;

    public MySqlCreateTableStatement() {

    }

    public Map<String, String> getTableOptions() {
        return tableOptions;
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect query) {
        this.query = query;
    }

    @Override
    public void output(StringBuffer buf) {
        if (Type.GLOBAL_TEMPORARY.equals(this.type)) {
            buf.append("CREATE TEMPORARY TABLE ");
        } else {
            buf.append("CREATE TABLE ");
        }

        if (ifNotExiists) {
            buf.append("IF NOT EXISTS ");
        }

        this.name.output(buf);
        buf.append(" ");
        buf.append("(");
        for (int i = 0, size = tableElementList.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            tableElementList.get(i).output(buf);
        }
        buf.append(")");

        if (query != null) {
            buf.append(" ");
            query.output(buf);
        }
    }

    public boolean isIfNotExiists() {
        return ifNotExiists;
    }

    public void setIfNotExiists(boolean ifNotExiists) {
        this.ifNotExiists = ifNotExiists;
    }

}
