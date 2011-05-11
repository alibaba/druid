package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;

@SuppressWarnings("serial")
public abstract class SQLConstaintImpl extends SQLObjectImpl implements SQLConstaint {
    private SQLName name;

    public SQLConstaintImpl() {

    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

}
