package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;

import java.util.List;

public interface SQLIndex extends SQLObject {
    List<SQLName> getCovering();

    List<SQLSelectOrderByItem> getColumns();
}
