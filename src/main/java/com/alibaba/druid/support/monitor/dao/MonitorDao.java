package com.alibaba.druid.support.monitor.dao;

import java.util.List;

import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.http.stat.WebURIStatValue;
import com.alibaba.druid.support.monitor.MonitorContext;
import com.alibaba.druid.support.spring.stat.SpringMethodStatValue;

public interface MonitorDao {

    void saveSql(MonitorContext ctx, List<JdbcSqlStatValue> sqlList);

    void saveSpringMethod(MonitorContext ctx, List<SpringMethodStatValue> methodList);

    void saveWebURI(MonitorContext ctx, List<WebURIStatValue> uriList);
}
