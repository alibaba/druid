package com.alibaba.druid.stat.service;

import java.util.List;

import com.alibaba.druid.stat.service.dto.DataSourceInfo;
import com.alibaba.druid.stat.service.dto.SqlInfo;

public interface DruidStatStore {

    void saveSql(long timeMillis, List<SqlInfo> sqlList);

    void saveDataSource(long timeMillis, List<DataSourceInfo> dataSourceList);
}
