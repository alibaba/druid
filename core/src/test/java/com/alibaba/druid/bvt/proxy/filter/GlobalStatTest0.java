package com.alibaba.druid.bvt.proxy.filter;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalStatTest0 {
    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;

    @BeforeEach
    protected void setUp() throws Exception {
        JdbcStatManager.getInstance().reset();

        dataSourceA = new DruidDataSource();
        dataSourceA.setUrl("jdbc:mock:xx_A");
        dataSourceA.setFilters("stat");
        dataSourceA.setUseGlobalDataSourceStat(true);

        dataSourceB = new DruidDataSource();
        dataSourceB.setUrl("jdbc:mock:xx_A");
        dataSourceB.setFilters("stat");
        dataSourceB.setUseGlobalDataSourceStat(true);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSourceA);
        JdbcUtils.close(dataSourceB);

        JdbcDataSourceStat.setGlobal(null);
        JdbcStatManager.getInstance().reset();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_execute() throws Exception {
        {
            Connection conn = dataSourceA.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT 1");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Empty loop
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
                // Empty loop
            }
            rs.close();
            stmt.close();
            conn.close();
        }

        assertSame(JdbcDataSourceStat.getGlobal(), dataSourceA.getDataSourceStat());
        assertSame(JdbcDataSourceStat.getGlobal(), dataSourceB.getDataSourceStat());

        assertEquals(1, JdbcStatManager.getInstance().getSqlList().size());
        String json = JSONUtils.toJSONString(JdbcStatManager.getInstance().getSqlList());
        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) JSONUtils.parse(json);
        Map<String, Object> sqlInfo = sqlList.get(0);
        assertNotNull(sqlInfo);
//        assertEquals(JdbcConstants.MOCK, sqlInfo.get("DbType"));
        assertEquals(2, sqlInfo.get("ExecuteCount"));
        assertEquals(2, sqlInfo.get("FetchRowCount"));
    }
}
