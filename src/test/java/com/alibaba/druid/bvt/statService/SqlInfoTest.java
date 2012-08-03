package com.alibaba.druid.bvt.statService;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.service.DruidStatServiceUtils;
import com.alibaba.druid.stat.service.dto.SqlInfo;

public class SqlInfoTest extends TestCase {

    public void test_0() throws Exception {
        JdbcSqlStat sqlStat = new JdbcSqlStat("select 1");
        sqlStat.incrementRunningCount();
        sqlStat.incrementExecuteSuccessCount();

        SqlInfo info = DruidStatServiceUtils.createSqlInfo(sqlStat);

        Assert.assertEquals(1, info.getRunningCount());
        Assert.assertEquals(1, info.getExecuteCount());

        JdbcSqlStat sqlStat1 = new JdbcSqlStat("select 2");
        sqlStat1.incrementRunningCount();
        sqlStat1.incrementExecuteSuccessCount();
        
        SqlInfo info1 = DruidStatServiceUtils.createSqlInfo(sqlStat);
        
        info.merge(info1);
        
        Assert.assertEquals(2, info.getRunningCount());
        Assert.assertEquals(2, info.getExecuteCount());
    }
}
