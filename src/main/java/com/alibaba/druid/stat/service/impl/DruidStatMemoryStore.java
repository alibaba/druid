package com.alibaba.druid.stat.service.impl;

import java.util.List;

import com.alibaba.druid.stat.service.DruidStatStore;
import com.alibaba.druid.stat.service.dto.DataSourceInfo;
import com.alibaba.druid.stat.service.dto.SqlInfo;


public class DruidStatMemoryStore implements DruidStatStore {

    @Override
    public void saveSql(long timeMillis, List<SqlInfo> sqlList) {
        
    }

    @Override
    public void saveDataSource(long timeMillis, List<DataSourceInfo> dataSourceList) {
        
    }

}
