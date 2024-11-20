package com.alibaba.druid.oceanbase;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.oceanbase.jdbc.OceanBaseXid;
import org.junit.Assert;
import org.junit.Ignore;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Ignore("OceanBase Oracle Mode is not open source")
public class OceanBaseOracleXATest extends PoolTestCase {

    private DruidXADataSource dataSource;
    private XAConnection xaConnection;
    private XAResource xaResource;
    private Connection connection;
    public static String tableName = "tablename";

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidXADataSource();
        // When using the oracle mode of oceanbase, must I add the following URL to the end of the XA connection URL. useServerPrepStmts=true parameter
        dataSource.setUrl("jdbc:oceanbase://host:port/datasource?useServerPrepStmts=true");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        dataSource.setDriverClassName("com.oceanbase.jdbc.Driver");
        xaConnection = dataSource.getXAConnection();
        xaResource = xaConnection.getXAResource();
        connection = xaConnection.getConnection();
        JdbcUtils.execute(dataSource,
                "CREATE TABLE tableName (\n" +
                "    c1 NUMBER(10) NOT NULL,\n" +
                "    c2 NUMBER(10) NOT NULL,\n" +
                "    PRIMARY KEY (c1)\n" +
                ");");

    }

    protected void tearDown() throws Exception {
        JdbcUtils.execute(dataSource, "DROP TABLE tableName");
        JdbcUtils.close(dataSource);

        super.tearDown();
    }

    public void test_0() throws Exception {
        XAConnection conn = dataSource.getXAConnection();
        conn.close();
    }

    public void testObOracleXAOne() throws Exception {
        String gtridStr = "gtrid_test_wgs_ob_oracle_xa_one";
        String bqualStr = "bqual_test_wgs_ob_oracle_xa_one";

        Xid xid = new OceanBaseXid(gtridStr.getBytes(), bqualStr.getBytes(), 123);
        try {
            xaResource.start(xid, XAResource.TMNOFLAGS);
            // ps test
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            pstmt = connection.prepareStatement("select c1 from " + tableName);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
            pstmt.close();
            pstmt = connection.prepareStatement("insert into " + tableName + " (c1, c2) values(?, ?)");
            pstmt.setInt(1, 12);
            pstmt.setInt(2, 12);
            pstmt.executeUpdate();
            xaResource.end(xid, XAResource.TMSUCCESS);
            xaResource.prepare(xid);
            xaResource.commit(xid, false);
        } catch (Exception e) {
            e.printStackTrace();
            xaResource.rollback(xid);
            throw e;
        }
    }

    public void testObOracleXAOnePhase() throws Exception {
        connection.createStatement().execute(" insert into " + tableName + "  values(1,2)");

        String gtridStr = "gtrid_test_wgs_ob_oracle_xa_one_phase";
        String bqualStr = "bqual_test_wgs_ob_oracle_xa_one_phase";

        Xid xid = new OceanBaseXid(gtridStr.getBytes(), bqualStr.getBytes(), 123);
        try {
            xaResource.start(xid, XAResource.TMNOFLAGS);
            // ps test
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            pstmt = connection.prepareStatement("select c1 from " + tableName + "");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }

            pstmt.close();

            pstmt = connection.prepareStatement("insert into " + tableName + " (c1, c2) values(?, ?)");
            pstmt.setInt(1, 12);
            pstmt.setInt(2, 12);
            pstmt.executeUpdate();

            xaResource.end(xid, XAResource.TMSUCCESS);
            xaResource.commit(xid, true);
        } catch (Exception e) {
            xaResource.rollback(xid);
            throw e;
        }
    }

    public void testObOracleXAWithError() throws Exception {
        connection.setAutoCommit(false);
        
        String gtridStr = "gtrid_test_wgs_ob_oracle_xa_with_error";
        String bqualStr = "bqual_test_wgs_ob_oracle_xa_with_error";

        Xid xid = new OceanBaseXid(gtridStr.getBytes(), bqualStr.getBytes(), 123);
        // This flag will cause an exception
        try {
            xaResource.start(xid, 123);
            Assert.fail();
        } catch (XAException e) {
            Assert.assertEquals(XAException.XAER_INVAL, e.errorCode);
        }

        try {
            xaResource.prepare(xid);
            Assert.fail();
        } catch (XAException e) {
            Assert.assertEquals(XAException.XAER_NOTA, e.errorCode);
        }

        try {
            xaResource.commit(xid, true);
            Assert.fail();
        } catch (XAException e) {
            Assert.assertEquals(XAException.XAER_NOTA, e.errorCode);
        }
        xaResource.rollback(xid);
    }

    public void testObOracleXACheckAcAndError() throws Exception {

        String gtridStr = "gtrid_test_wgs_ob_oracle_xa_check_ac_and_error";
        String bqualStr = "bqual_test_wgs_ob_oracle_xa_check_ac_and_error";

        Xid xid = new OceanBaseXid(gtridStr.getBytes(), bqualStr.getBytes(), 123);
        XAResource xaResource = xaConnection.getXAResource();
        try {
            Assert.assertTrue(connection.getAutoCommit());
            // This flag will cause an exception
            try {
                xaResource.start(xid, 123);
                Assert.fail();
            } catch (XAException e) {
                Assert.assertEquals(XAException.XAER_INVAL, e.errorCode);
            }
            Assert.assertTrue(connection.getAutoCommit());
        } catch (Exception e) {
            xaResource.rollback(xid);
            throw e;
        }
    }

    public void testObOracleXACheckAcAndEndError() throws Exception {
        
        String gtridStr = "gtrid_test_wgs_ob_oracle_xa_check_ac_and_end_error_3";
        String bqualStr = "bqual_test_wgs_ob_oracle_xa_check_ac_and_end_error_3";

        Xid xid = new OceanBaseXid(gtridStr.getBytes(), bqualStr.getBytes(), 123);
        try {
            Assert.assertTrue(connection.getAutoCommit());
            xaResource.start(xid, XAResource.TMNOFLAGS);
            Assert.assertFalse(connection.getAutoCommit());

            // ps test
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            pstmt = connection.prepareStatement("select c1 from " + tableName + "");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString(1));
            }

            pstmt.close();

            pstmt = connection.prepareStatement("insert into " + tableName + " (c1,c2) values(1,2)");
            pstmt.executeUpdate();

            try {
                xaResource.end(xid, 123);
                Assert.fail();
            } catch (XAException e) {
                Assert.assertEquals(XAException.XAER_INVAL, e.errorCode);
            }
            Assert.assertFalse(connection.getAutoCommit());

            xaResource.end(xid, XAResource.TMSUCCESS);
            Assert.assertTrue(connection.getAutoCommit());

            xaResource.prepare(xid);
            xaResource.commit(xid, false);
        } catch (XAException e) {
            if (e.errorCode == XAException.XAER_DUPID) {
                xaResource.start(xid, XAResource.TMJOIN);
                xaResource.end(xid, XAResource.TMSUCCESS);
            }
            xaResource.rollback(xid);
            throw e;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
