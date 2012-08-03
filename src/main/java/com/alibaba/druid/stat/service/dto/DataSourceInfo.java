package com.alibaba.druid.stat.service.dto;

import java.util.List;

public class DataSourceInfo {

    private long          id;
    private String        url;
    private String        dbType;

    private int           activeCount;
    private int           activePeak;

    private int           poolingCount;

    private int           connectCount;
    private int           connectErrorCount;

    private int           createCount;
    private int           createErrorCount;

    private int           executeCount;

    private int           closeCount;
    private int           destoryCount;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getActivePeak() {
        return activePeak;
    }

    public void setActivePeak(int activePeak) {
        this.activePeak = activePeak;
    }

    public int getPoolingCount() {
        return poolingCount;
    }

    public void setPoolingCount(int poolingCount) {
        this.poolingCount = poolingCount;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public int getConnectErrorCount() {
        return connectErrorCount;
    }

    public void setConnectErrorCount(int connectErrorCount) {
        this.connectErrorCount = connectErrorCount;
    }

    public int getCreateCount() {
        return createCount;
    }

    public void setCreateCount(int createCount) {
        this.createCount = createCount;
    }

    public int getCreateErrorCount() {
        return createErrorCount;
    }

    public void setCreateErrorCount(int createErrorCount) {
        this.createErrorCount = createErrorCount;
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(int executeCount) {
        this.executeCount = executeCount;
    }

    public int getCloseCount() {
        return closeCount;
    }

    public void setCloseCount(int closeCount) {
        this.closeCount = closeCount;
    }

    public int getDestoryCount() {
        return destoryCount;
    }

    public void setDestoryCount(int destoryCount) {
        this.destoryCount = destoryCount;
    }

}
