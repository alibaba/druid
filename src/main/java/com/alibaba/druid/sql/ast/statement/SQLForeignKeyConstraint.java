package com.alibaba.druid.sql.ast.statement;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;

public interface SQLForeignKeyConstraint extends SQLConstaint {
    List<SQLName> getReferencingColumns();

    SQLName getReferencedTableName();

    void setReferencedTableName(SQLName value);

    List<SQLName> getReferencedColumns();
}
