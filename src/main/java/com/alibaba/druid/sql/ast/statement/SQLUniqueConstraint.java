package com.alibaba.druid.sql.ast.statement;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;

public interface SQLUniqueConstraint extends SQLConstaint {
    List<SQLExpr> getColumns();

}
