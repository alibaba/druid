package com.alibaba.druid.stat.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.alibaba.druid.stat.service.DruidStatStore;
import com.alibaba.druid.stat.service.dto.DataSourceInfo;

public class DruidStatMemoryStore implements DruidStatStore {

    private ConcurrentHashMap<Long, DataSourceData> dataSourceDataMap = new ConcurrentHashMap<Long, DataSourceData>();

    @Override
    public void saveDataSource(List<DataSourceInfo> dataSourceList) {
        for (DataSourceInfo dataSourceInfo : dataSourceList) {
            DataSourceData data = dataSourceDataMap.get(dataSourceInfo.getId());

            if (data == null) {
                dataSourceDataMap.putIfAbsent(dataSourceInfo.getId(), new DataSourceData(dataSourceInfo.getId(),
                                                                                         dataSourceInfo.getUrl(),
                                                                                         dataSourceInfo.getDbType()));
                data = dataSourceDataMap.get(dataSourceInfo.getId());
            }

            data.add(dataSourceInfo);
        }
    }

    public Collection<DataSourceInfo> scan(long dataSourceId, Long startTimeMillis, Long endTimeMillis) {
        DataSourceData data = dataSourceDataMap.get(dataSourceId);

        if (data == null) {
            return Collections.emptyList();
        }

        return data.scan(startTimeMillis, endTimeMillis);
    }

    public static class DataSourceData {

        private final Long                                  id;
        private final String                                url;
        private final String                                dbType;

        private ConcurrentSkipListMap<Long, DataSourceInfo> data = new ConcurrentSkipListMap<Long, DataSourceInfo>();

        public DataSourceData(Long id, String url, String dbType){
            super();
            this.id = id;
            this.url = url;
            this.dbType = dbType;
        }

        public Long getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public String getDbType() {
            return dbType;
        }

        public void add(DataSourceInfo dataSourceInfo) {
            data.put(dataSourceInfo.getCollectTimeMillis(), dataSourceInfo);
        }

        public Collection<DataSourceInfo> scan(Long startTimeMillis, Long endTimeMillis) {
            if (data.size() == 0) {
                return Collections.emptyList();
            }

            if (startTimeMillis == null) {
                startTimeMillis = data.keySet().iterator().next();
            }

            if (endTimeMillis == null) {
                endTimeMillis = data.descendingKeySet().iterator().next();
            }

            return data.subMap(startTimeMillis, endTimeMillis).values();
        }
    }

}
