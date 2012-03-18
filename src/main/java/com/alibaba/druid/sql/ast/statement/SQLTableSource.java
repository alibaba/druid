package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObject;

public interface SQLTableSource extends SQLObject {

    String getAlias();

    void setAlias(String alias);
}
