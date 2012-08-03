package com.alibaba.druid.stat.service.impl;

import java.util.List;

import com.alibaba.druid.stat.service.DruidStatStore;
import com.alibaba.druid.stat.service.dto.DataSourceInfo;


public class DruidStatMemoryStore implements DruidStatStore {

    @Override
    public void saveDataSource(long timeMillis, List<DataSourceInfo> dataSourceList) {
        
    }

}
