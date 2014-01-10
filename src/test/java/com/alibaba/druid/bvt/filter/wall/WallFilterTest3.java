package com.alibaba.druid.bvt.filter.wall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

public class WallFilterTest3 extends TestCase {

    private DruidDataSource dataSource;
    private WallFilter      wallFilter;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:h2:mem:wall_test;");
        // dataSource.setFilters("wall");
        dataSource.setDbType(JdbcConstants.MARIADB);

        WallConfig config = new WallConfig();
        config.setTenantCallBack(new TenantTestCallBack());

        wallFilter = new WallFilter();
        wallFilter.setConfig(config);
        wallFilter.setDbType(JdbcConstants.MARIADB);
        List<Filter> filters = new LinkedList<Filter>();
        filters.add(wallFilter);
        dataSource.setProxyFilters(filters);

        dataSource.init();

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_wallFilter() throws Exception {
        Assert.assertEquals(JdbcConstants.MARIADB, wallFilter.getDbType());
        Assert.assertFalse(wallFilter.isLogViolation());
        wallFilter.setLogViolation(true);
        Assert.assertTrue(wallFilter.isLogViolation());
        wallFilter.setLogViolation(false);
        Assert.assertFalse(wallFilter.isLogViolation());

        Assert.assertTrue(wallFilter.isThrowException());
        wallFilter.setThrowException(false);
        Assert.assertFalse(wallFilter.isThrowException());
        wallFilter.setThrowException(true);
        Assert.assertTrue(wallFilter.isThrowException());

        wallFilter.clearProviderCache();
        wallFilter.getProviderWhiteList();
        Assert.assertTrue(wallFilter.isInited());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE t (FID INTEGER, FNAME VARCHAR(50), TENANT VARCHAR(32))");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, wallFilter.getProvider().getTableStat("t").getCreateCount());

        {
            Connection conn = dataSource.getConnection();
            String sql = "INSERT INTO t (FID, FNAME) VALUES (?, ?)";

            for (int i = 0; i < 10; ++i) {
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.NO_GENERATED_KEYS);
                stmt.setInt(1, i + 10);
                stmt.setString(2, "a" + (i + 10));
                stmt.execute();
                stmt.close();
            }

            conn.close();
        }
        Assert.assertEquals(10, wallFilter.getProvider().getTableStat("t").getInsertCount());
        Assert.assertEquals(10, wallFilter.getProvider().getTableStat("t").getInsertDataCount());

        {
            Connection conn = dataSource.getConnection();
            String sql = "INSERT INTO t (FID, FNAME) VALUES (?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.NO_GENERATED_KEYS);
            for (int i = 0; i < 10; ++i) {
                stmt.setInt(1, i + 20);
                stmt.setString(2, "a" + (i + 20));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();

            conn.close();
        }
        Assert.assertEquals(11, wallFilter.getProvider().getTableStat("t").getInsertCount());
        Assert.assertEquals(20, wallFilter.getProvider().getTableStat("t").getInsertDataCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            for (int i = 0; i < 10; ++i) {
                stmt.addBatch("INSERT INTO t (FID, FNAME) VALUES (" + i + ", 'a" + i + "')");
            }
            stmt.executeBatch();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(21, wallFilter.getProvider().getTableStat("t").getInsertCount());
        Assert.assertEquals(0, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());
        {
            String sql = "SELECT * FROM T";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(30, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                                                           ResultSet.CONCUR_READ_ONLY,
                                                           ResultSet.HOLD_CURSORS_OVER_COMMIT);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(60, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T LIMIT 10";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, new int[0]);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(70, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T LIMIT 10";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, new String[0]);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(80, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T LIMIT 10";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(90, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T LIMIT 10";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(100, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T";

            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                                                      ResultSet.HOLD_CURSORS_OVER_COMMIT);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(130, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T LIMIT 10";

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stmt.execute(sql, Statement.NO_GENERATED_KEYS);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(140, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T LIMIT 10";

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                                                  ResultSet.HOLD_CURSORS_OVER_COMMIT);
            stmt.execute(sql, new int[0]);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(150, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            String sql = "SELECT * FROM T LIMIT 10";

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                                                  ResultSet.HOLD_CURSORS_OVER_COMMIT);
            stmt.execute(sql, new String[0]);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(160, wallFilter.getProvider().getTableStat("t").getFetchRowCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE from t where FID = 0");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, wallFilter.getProvider().getTableStat("t").getDeleteDataCount());
        Assert.assertEquals(0, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE from t where FID = 1 OR FID = 2", Statement.NO_GENERATED_KEYS);
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(3, wallFilter.getProvider().getTableStat("t").getDeleteDataCount());
        Assert.assertEquals(0, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE from t where FID = 3", new int[0]);
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(4, wallFilter.getProvider().getTableStat("t").getDeleteDataCount());
        Assert.assertEquals(0, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE from t where FID = 4", new String[0]);
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(5, wallFilter.getProvider().getTableStat("t").getDeleteDataCount());
        Assert.assertEquals(0, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE from t where FID = ?");
            stmt.setInt(1, 5);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(6, wallFilter.getProvider().getTableStat("t").getDeleteDataCount());
        Assert.assertEquals(0, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("update t SET fname = 'xx' where FID = 13 OR FID = 14");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(2, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t SET fname = 'xx' where FID = ? OR FID = ?");
            stmt.setInt(1, 13);
            stmt.setInt(2, 14);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(4, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());

        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t SET fname = 'xx' where FID = ? OR FID = ?");
            stmt.setInt(1, 13);
            stmt.setInt(2, 14);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(6, wallFilter.getProvider().getTableStat("t").getUpdateDataCount());
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update t SET fname = 'xx' where FID = ?");

            stmt.setInt(1, 13);
            stmt.addBatch();

            stmt.setInt(1, 14);
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

        Assert.assertEquals(0, wallFilter.getViolationCount());
        wallFilter.resetViolationCount();
        wallFilter.checkValid("select 1");
        Assert.assertEquals(0, wallFilter.getViolationCount());
    }
}
