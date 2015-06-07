package com.alibaba.druid.sql.builder;


public interface UpdateBuilder {
    UpdateBuilder from(String table);

    UpdateBuilder from(String table, String alias);
    
    UpdateBuilder limit(int rowCount);

    UpdateBuilder limit(int rowCount, int offset);

    UpdateBuilder where(String sql);

    UpdateBuilder whereAnd(String sql);

    UpdateBuilder whereOr(String sql);
}
