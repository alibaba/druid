package com.alibaba.druid.bvt.proxy;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.config.AbstractDruidFilterConfig;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.util.JdbcUtils;

public class DruidDriverTest extends TestCase {

    public static class PublicJdbcFilterAdapter extends FilterAdapter {

        public PublicJdbcFilterAdapter(){

        }

        @Override
        public void loadConfig(AbstractDruidFilterConfig druidFilterConfig) {

        }
    }

    static class PrivateJdbcFilterAdapter extends FilterAdapter {

        private PrivateJdbcFilterAdapter(){

        }

        @Override
        public void loadConfig(AbstractDruidFilterConfig druidFilterConfig) {

        }
    }

    public static class InitErrorJdbcFilterAdapter extends FilterAdapter {

        public InitErrorJdbcFilterAdapter() throws InstantiationException{
            throw new InstantiationException("init error");
        }

        @Override
        public void loadConfig(AbstractDruidFilterConfig druidFilterConfig) {
        }
    }

    static class PrivateDriver extends MockDriver {

        private PrivateDriver(){

        }
    }

    public static class InitErrorDriver extends MockDriver {

        public InitErrorDriver() throws InstantiationException{
            throw new InstantiationException("init error");
        }
    }

    public void test_registerDriver() throws Exception {
        Assert.assertFalse(DruidDriver.registerDriver(null));
    }

    public void test_getRawDriverClassName() throws Exception {
        Assert.assertEquals("com.mysql.jdbc.Driver", JdbcUtils.getDriverClassName("jdbc:mysql:"));
        Assert.assertEquals("oracle.jdbc.driver.OracleDriver", JdbcUtils.getDriverClassName("jdbc:oracle:"));
        Assert.assertEquals("com.microsoft.jdbc.sqlserver.SQLServerDriver", JdbcUtils.getDriverClassName("jdbc:microsoft:"));
        Assert.assertEquals("org.postgresql.Driver", JdbcUtils.getDriverClassName("jdbc:postgresql:xx"));
        Assert.assertEquals("net.sourceforge.jtds.jdbc.Driver", JdbcUtils.getDriverClassName("jdbc:jtds:"));
        {
            Exception error = null;
            try {
            	JdbcUtils.getDriverClassName("jdbc:xxx:");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getRawDriver() throws Exception {
        DruidDriver driver = new DruidDriver();
        Assert.assertNotNull(driver.createDriver(MockDriver.class.getName()));

        {
            Exception error = null;
            try {
                driver.createDriver(null);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                driver.createDriver(PrivateDriver.class.getName());
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            Exception error = null;
            try {
                driver.createDriver(InitErrorDriver.class.getName());
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_driver_wrap() throws Exception {
        DruidDriver driver = new DruidDriver();

        {
            ConnectionProxyImpl conn = (ConnectionProxyImpl) driver.connect("jdbc:wrap-jdbc:filters=:name=driverWrapperTest:jdbc:derby:memory:driverWrapperTestDB;create=true",
                                                                            new Properties());
            Assert.assertEquals(0, conn.getDirectDataSource().getFilters().size());
            conn.close();
        }
        {
            ConnectionProxyImpl conn = (ConnectionProxyImpl) driver.connect("jdbc:wrap-jdbc:filters=,:name=driverWrapperTest:jdbc:derby:memory:driverWrapperTestDB;create=true",
                                                                            new Properties());
            Assert.assertEquals(0, conn.getDirectDataSource().getFilters().size());
            conn.close();
        }
        {
            ConnectionProxyImpl conn = (ConnectionProxyImpl) driver.connect("jdbc:wrap-jdbc:filters=,:jdbc:derby:memory:driverWrapperTestDB;create=true",
                                                                            new Properties());
            Assert.assertEquals(0, conn.getDirectDataSource().getFilters().size());
            conn.close();
        }
        {
            ConnectionProxyImpl conn = (ConnectionProxyImpl) driver.connect("jdbc:wrap-jdbc:filters=,:name=:jdbc:derby:memory:driverWrapperTestDB;create=true",
                                                                            new Properties());
            Assert.assertEquals(0, conn.getDirectDataSource().getFilters().size());
            conn.close();
        }
        {
            ConnectionProxyImpl conn = (ConnectionProxyImpl) driver.connect("jdbc:wrap-jdbc:driver=:filters=,:name=driverWrapperTest:jdbc:derby:memory:driverWrapperTestDB;create=true",
                                                                            new Properties());
            Assert.assertEquals(0, conn.getDirectDataSource().getFilters().size());
            conn.close();
        }
        {
            ConnectionProxyImpl conn = (ConnectionProxyImpl) driver.connect("jdbc:wrap-jdbc:name=driverWrapperTest:jdbc:derby:memory:driverWrapperTestDB;create=true",
                                                                            new Properties());
            Assert.assertEquals(0, conn.getDirectDataSource().getFilters().size());
            conn.close();
        }
        {
            ConnectionProxyImpl conn = (ConnectionProxyImpl) driver.connect("jdbc:wrap-jdbc:filters="
                                                                                    + PublicJdbcFilterAdapter.class.getName()
                                                                                    + ":name=driverWrapperTest:jdbc:derby:memory:driverWrapperTestDB;create=true",
                                                                            new Properties());
            Assert.assertEquals(1, conn.getDirectDataSource().getFilters().size());
            conn.close();
        }
        {
            Exception error = null;
            try {
                driver.connect("jdbc:wrap-jdbc:filters=" + PrivateJdbcFilterAdapter.class.getName()
                                       + ":name=driverWrapperTest:jdbc:derby:memory:driverWrapperTestDB;create=true",
                               new Properties()).close();
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                driver.connect("jdbc:wrap-jdbc:filters=" + InitErrorJdbcFilterAdapter.class.getName()
                                       + ":name=driverWrapperTest:jdbc:derby:memory:driverWrapperTestDB;create=true",
                               new Properties()).close();
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

}
