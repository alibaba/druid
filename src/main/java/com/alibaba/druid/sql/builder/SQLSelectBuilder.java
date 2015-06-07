package com.alibaba.druid.sql.builder;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;

public interface SQLSelectBuilder {

    SQLSelectStatement getSQLSelectStatement();

    SQLSelectBuilder select(String... column);

    SQLSelectBuilder selectWithAlias(String column, String alias);

    SQLSelectBuilder from(String table);

    SQLSelectBuilder from(String table, String alias);

    SQLSelectBuilder orderBy(String... columns);

    SQLSelectBuilder groupBy(String expr);

    SQLSelectBuilder having(String expr);

    SQLSelectBuilder into(String expr);

    SQLSelectBuilder limit(int rowCount);

    SQLSelectBuilder limit(int rowCount, int offset);

    SQLSelectBuilder where(String sql);

    SQLSelectBuilder whereAnd(String sql);

    SQLSelectBuilder whereOr(String sql);

    String toString();
}
