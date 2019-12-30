package com.alibaba.druid.sql.ast;

public enum AutoIncrementType
{
    GROUP("GROUP"), SIMPLE("SIMPLE"),
    SIMPLE_CACHE("SIMPLE WITH CACHE"), TIME("TIME");

    private final String keyword;

    public String getKeyword() {
        return this.keyword;
    }

    private AutoIncrementType(String keyword){
        this.keyword = keyword;
    }


}
