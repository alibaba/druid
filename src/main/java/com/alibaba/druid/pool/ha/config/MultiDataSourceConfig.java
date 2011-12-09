package com.alibaba.druid.pool.ha.config;

import java.util.ArrayList;
import java.util.List;

public class MultiDataSourceConfig {

    private String                      name;

    private List<DruidDataSourceConfig> dataSources = new ArrayList<DruidDataSourceConfig>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DruidDataSourceConfig> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DruidDataSourceConfig> dataSources) {
        this.dataSources = dataSources;
    }

}
