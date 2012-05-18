package com.alibaba.druid.hbase.exec;

import java.util.List;

import com.alibaba.druid.hbase.HBaseConnection;


public class SingleTableQueryExecutePlan implements ExecutePlan {

    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void execute(HBaseConnection connection, List<Object> paramerers) {
        
    }

}
