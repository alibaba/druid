package com.alibaba.druid.bvt.statService;

import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.service.DruidStatServiceUtils;

public class SqlInfoTest extends TestCase {
    public void test_0 () throws Exception {
        JdbcSqlStat sqlStat = new JdbcSqlStat("select 1");
        
        DruidStatServiceUtils.createSqlInfo(sqlStat);
    }
}
