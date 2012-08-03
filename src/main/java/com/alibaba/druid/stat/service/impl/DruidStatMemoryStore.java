package com.alibaba.druid.stat.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.alibaba.druid.stat.service.DruidStatStore;
import com.alibaba.druid.stat.service.dto.DataSourceInfo;

public class DruidStatMemoryStore implements DruidStatStore {

    private ConcurrentHashMap<Long, DataSourceData> dataSourceDataMap = new ConcurrentHashMap<Long, DataSourceData>();

    @Override
    public void saveDataSource(long timeMillis, List<DataSourceInfo> dataSourceList) {
        for (DataSourceInfo dataSourceInfo : dataSourceList) {
            DataSourceData data = dataSourceDataMap.get(dataSourceInfo.getId());

            if (data == null) {
                dataSourceDataMap.putIfAbsent(dataSourceInfo.getId(), new DataSourceData());
                data = dataSourceDataMap.get(dataSourceInfo.getId());
            }
            
            data.add(timeMillis, dataSourceInfo);
        }
    }

    public static class DataSourceData {

        private ConcurrentSkipListMap<Long, DataSourceInfo> data = new ConcurrentSkipListMap<Long, DataSourceInfo>();

        public void add(long timeMillis, DataSourceInfo dataSourceInfo) {
            data.put(timeMillis, dataSourceInfo);
        }
    }

}
