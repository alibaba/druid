package com.alibaba.druid.hdriver.impl.execute;

import com.alibaba.druid.hdriver.impl.mapping.HMapping;

public class SingleTableExecutePlan extends ExecutePlanAdapter {

    private String   tableName;

    private HMapping mapping;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public HMapping getMapping() {
        return mapping;
    }

    public void setMapping(HMapping mapping) {
        this.mapping = mapping;
    }

}
