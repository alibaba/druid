package com.alibaba.druid.hdriver.impl.execute;

public class SingleTableExecutePlan extends ExecutePlanAdapter {

    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
