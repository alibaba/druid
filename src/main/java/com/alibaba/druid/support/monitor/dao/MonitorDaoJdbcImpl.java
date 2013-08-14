package com.alibaba.druid.support.monitor.dao;

import java.util.List;

import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.http.stat.WebURIStatValue;
import com.alibaba.druid.support.monitor.MonitorContext;
import com.alibaba.druid.support.spring.stat.SpringMethodStatValue;

public class MonitorDaoJdbcImpl implements MonitorDao {

    @Override
    public void saveSql(MonitorContext ctx, List<JdbcSqlStatValue> sqlList) {

    }

    @Override
    public void saveSpringMethod(MonitorContext ctx, List<SpringMethodStatValue> methodList) {

    }

    @Override
    public void saveWebURI(MonitorContext ctx, List<WebURIStatValue> uriList) {

    }

}
