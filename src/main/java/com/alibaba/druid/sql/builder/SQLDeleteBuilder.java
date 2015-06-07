package com.alibaba.druid.sql.builder;


public interface SQLDeleteBuilder {
    SQLDeleteBuilder from(String table);

    SQLDeleteBuilder from(String table, String alias);
    
    SQLDeleteBuilder limit(int rowCount);

    SQLDeleteBuilder limit(int rowCount, int offset);

    SQLDeleteBuilder where(String sql);

    SQLDeleteBuilder whereAnd(String sql);

    SQLDeleteBuilder whereOr(String sql);
}
