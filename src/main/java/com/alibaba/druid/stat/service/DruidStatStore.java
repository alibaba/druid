package com.alibaba.druid.stat.service;

import java.util.List;

import com.alibaba.druid.stat.service.dto.DataSourceInfo;

public interface DruidStatStore {

    void saveDataSource(long timeMillis, List<DataSourceInfo> dataSourceList);
}
