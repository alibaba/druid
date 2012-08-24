package com.alibaba.druid.bvt.stat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;

public class DruidStatServiceTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_statService() throws Exception {
        String sql = "select 1";
        Connection conn = dataSource.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();
        
        conn.close();
        
        String result = DruidStatService.getInstance().service("/sql.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        
        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) resultMap.get("Content");
        
        Assert.assertEquals(1, sqlList.size());
    }
}
