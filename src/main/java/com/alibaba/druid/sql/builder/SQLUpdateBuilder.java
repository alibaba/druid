package com.alibaba.druid.sql.builder;


public interface SQLUpdateBuilder {
    SQLUpdateBuilder from(String table);

    SQLUpdateBuilder from(String table, String alias);
    
    SQLUpdateBuilder limit(int rowCount);

    SQLUpdateBuilder limit(int rowCount, int offset);

    SQLUpdateBuilder where(String sql);

    SQLUpdateBuilder whereAnd(String sql);

    SQLUpdateBuilder whereOr(String sql);
    
    SQLUpdateBuilder set(String... items);
}
