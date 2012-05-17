package com.alibaba.druid.sql.ast.statement;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLObject;

public interface SQLTableSource extends SQLObject {

    String getAlias();

    void setAlias(String alias);
    
    List<SQLHint> getHints();
}
