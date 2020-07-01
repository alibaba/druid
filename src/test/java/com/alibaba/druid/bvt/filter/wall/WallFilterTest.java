package com.alibaba.druid.bvt.filter.wall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallFilter;

public class WallFilterTest extends TestCase {

    private DruidDataSource dataSource;
    private WallFilter      wallFilter;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:h2:mem:wall_test;");
        dataSource.setFilters("wall");
        dataSource.init();

        wallFilter = (WallFilter) dataSource.getProxyFilters().get(0);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_wallFilter() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE t (FID INTEGER, FNAME VARCHAR(50))");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, wallFilter.getProvider().getTableStat("t").getCreateCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            for (int i = 0; i < 10; ++i) {
                stmt.execute("INSERT INTO t (FID, FNAME) VALUES (" + i + ", 'a" + i + "')");
            }
            stmt.close();
            conn.close();
        }
        {
            String sql = "SELECT * FROM T";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(10, wallFilter.getProvider().getTableStat("t").getFetchRowCount());
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE from t where FID = 0");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, wallFilter.getProvider().getTableStat("t").getDeleteDataCount());
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE from t where FID = 1 OR FID = 2");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(3, wallFilter.getProvider().getTableStat("t").getDeleteDataCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("update t SET fname = 'xx' where FID = 3 OR FID = 4");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(2, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t SET fname = 'xx' where FID = ? OR FID = ?");
            stmt.setInt(1, 3);
            stmt.setInt(2, 4);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(4, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());
        
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t SET fname = 'xx' where FID = ? OR FID = ?");
            stmt.setInt(1, 3);
            stmt.setInt(2, 4);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(6, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t SET fname = 'xx' where FID = ?");
            
            stmt.setInt(1, 3);
            stmt.addBatch();
            
            stmt.setInt(1, 4);
            stmt.addBatch();
            
            stmt.executeBatch();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(8, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("truncate table t");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, wallFilter.getProvider().getTableStat("t").getTruncateCount());
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("drop table t");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, wallFilter.getProvider().getTableStat("t").getDropCount());
    }
}
