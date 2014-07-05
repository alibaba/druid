package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

public class GlobalStatTest0 extends TestCase {

    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
        
        dataSourceA = new DruidDataSource();
        dataSourceA.setUrl("jdbc:mock:xx_A");
        dataSourceA.setFilters("stat");
        dataSourceA.setUseGlobalDataSourceStat(true);

        dataSourceB = new DruidDataSource();
        dataSourceB.setUrl("jdbc:mock:xx_A");
        dataSourceB.setFilters("stat");
        dataSourceB.setUseGlobalDataSourceStat(true);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSourceA);
        JdbcUtils.close(dataSourceB);
        
        JdbcDataSourceStat.setGlobal(null);
        
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    @SuppressWarnings("unchecked")
    public void test_execute() throws Exception {
        {
            Connection conn = dataSourceA.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT 1");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSourceB.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT 1");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }

        Assert.assertSame(JdbcDataSourceStat.getGlobal(), dataSourceA.getDataSourceStat());
        Assert.assertSame(JdbcDataSourceStat.getGlobal(), dataSourceB.getDataSourceStat());

        Assert.assertEquals(1, JdbcStatManager.getInstance().getSqlList().size());
        String json = JSONUtils.toJSONString(JdbcStatManager.getInstance().getSqlList());
        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) JSONUtils.parse(json);
        Map<String, Object> sqlInfo = sqlList.get(0);
        Assert.assertNotNull(sqlInfo);
        Assert.assertEquals(JdbcConstants.MOCK, sqlInfo.get("DbType"));
        Assert.assertEquals(2, sqlInfo.get("ExecuteCount"));
        Assert.assertEquals(2, sqlInfo.get("FetchRowCount"));
    }

}
