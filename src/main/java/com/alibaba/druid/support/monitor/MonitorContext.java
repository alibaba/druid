package com.alibaba.druid.support.monitor;

import java.util.Date;

public class MonitorContext {

    private String appName;
    private Date   collectTime;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

}
