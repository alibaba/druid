package com.alibaba.druid.bvt.support.monitor;

import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.BeanInfo;

public class MonitorDaoJdbcImplTest extends TestCase {

    public void testBuildSql() throws Exception {
        MonitorDaoJdbcImpl dao = new MonitorDaoJdbcImpl();

        String sql = dao.buildInsertSql(new BeanInfo(JdbcSqlStatValue.class));

        System.out.println(sql);
    }
}
