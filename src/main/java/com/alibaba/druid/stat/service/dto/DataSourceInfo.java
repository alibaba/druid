package com.alibaba.druid.stat.service.dto;

import java.util.List;

public class DataSourceInfo {

    private String        url;
    private String        dbType;

    private List<SqlInfo> sqlList;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<SqlInfo> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<SqlInfo> sqlList) {
        this.sqlList = sqlList;
    }

}
