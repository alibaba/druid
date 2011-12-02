package com.alibaba.druid.pool.ha.config;

import java.util.ArrayList;
import java.util.List;

public class MultiDataSourceConfig {

    private List<DruidDataSourceConfig> dataSources = new ArrayList<DruidDataSourceConfig>();

    public List<DruidDataSourceConfig> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DruidDataSourceConfig> dataSources) {
        this.dataSources = dataSources;
    }

}
