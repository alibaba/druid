package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;

public interface SQLConstaint extends SQLObject {
    SQLName getName();

    void setName(SQLName value);
}
