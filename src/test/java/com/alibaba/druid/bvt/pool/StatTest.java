package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.json.JSONUtils;

public class StatTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_0() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from INFORMATION_SCHEMA.SETTINGS");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            conn.close();
        }

        DruidDataSourceStatValue dataSourceStatValue = dataSource.getStatValueAndReset();
        Assert.assertEquals(1, dataSourceStatValue.getSqlList().size());
        JdbcSqlStatValue sqlStat = dataSourceStatValue.getSqlList().get(0);
        Assert.assertNotNull(sqlStat.getExecuteLastStartTime());
        Assert.assertNotNull(sqlStat.getExecuteNanoSpanMaxOccurTime());
        Assert.assertTrue(sqlStat.getExecuteMillisMax() > 0);

        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from INFORMATION_SCHEMA.SETTINGS");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            conn.close();
        }
        String json = DruidStatService.getInstance().service("/sql-" + sqlStat.getId() + ".json");
        Map map = (Map) JSONUtils.parse(json);
        System.out.println(json);
    }
}
