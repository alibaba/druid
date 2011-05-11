package com.alibaba.druid.sql.ast;

import java.util.List;

public interface SQLDataType extends SQLObject {

    String getName();

    void setName(String name);

    List<SQLExpr> getArguments();
}
