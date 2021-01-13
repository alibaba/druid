package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.CounterValidConnectionChecker;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomDataSourceSelectorWithFilterTest {
    private final static Log LOG = LogFactory.getLog(RandomDataSourceSelectorWithFilterTest.class);
    private HighAvailableDataSource highAvailableDataSource;
    private CounterValidConnectionChecker checker;

    @Before
    public void setUp() throws Exception {
        LOG.info("setUp()");
        checker = new CounterValidConnectionChecker();
        highAvailableDataSource = new HighAvailableDataSource();
        String file = "/com/alibaba/druid/pool/ha/ha-datasource.properties";
        highAvailableDataSource.setDataSourceFile(file);
        highAvailableDataSource.setFilters("haRandomValidator");

        RandomDataSourceSelector selector = new RandomDataSourceSelector(highAvailableDataSource);
        RandomDataSourceValidateThread thread = new RandomDataSourceValidateThread(selector);
        thread.setCheckingIntervalSeconds(2);
        selector.setValidateThread(thread);
        selector.init();

        highAvailableDataSource.setDataSourceSelector(selector);
        highAvailableDataSource.init();
        initChecker();
    }

    @After
    public void tearDown() {
        LOG.info("tearDown()");
        highAvailableDataSource.destroy();
        highAvailableDataSource = null;
        checker = null;
    }

    @Test
    public void testSkipValidation() throws Exception {
        Connection conn = highAvailableDataSource.getConnection();
        createTable(conn);
        String url = conn.getMetaData().getURL();
        Thread.sleep(5000);
        int initValue = checker.getCountValue(url);

        executeSQL(conn, "select * from foo");
        Thread.sleep(1000);
        executeSQL(conn, "select * from foo");
        Thread.sleep(1000);
        assertEquals(initValue, checker.getCountValue(url));

        Thread.sleep(3000);
        int value = checker.getCountValue(url);
        LOG.info("URL: " + url + " Value: " + value + " Init: " + initValue);
        assertEquals(initValue + 1, value);

        Thread.sleep(2000);
        value = checker.getCountValue(url);
        LOG.info("URL: " + url + " Value: " + value + " Init: " + initValue);
        assertEquals(initValue + 2, value);

        conn.close();
    }

    @Test
    public void testForceValidation() throws Exception {
        Connection conn = highAvailableDataSource.getConnection();
        createTable(conn);
        String url = conn.getMetaData().getURL();
        Thread.sleep(2000);
        LOG.info("Start execute SQL");
        int initValue = 0;
        int value = 0;
        for (int i = 0; i < 10; i++) {
            executeSQL(conn, "select * from foo");
            if (initValue == 0) {
                initValue = checker.getCountValue(url);
            }
            Thread.sleep(600);
            value = checker.getCountValue(url);
            LOG.info("URL: " + url + " Value: " + value + " Init: " + initValue);
            assertEquals(initValue, value);
        }
        LOG.info("URL: " + url + " Value: " + value + " Init: " + initValue);
        Thread.sleep(5000);
        value = checker.getCountValue(url);
        assertTrue(value > initValue + 1);
    }

    private void createTable(Connection connection) {
        try {
            executeSQL(connection, "create table foo (bar varchar(1))");
        } catch(SQLException e) {
            try {
                executeSQL(connection, "drop table foo");
                executeSQL(connection, "create table foo (bar varchar(1))");
            } catch (SQLException ex) {
                // ignore
            }
        }
    }

    private void executeSQL(Connection connection, String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
        LOG.info("Executing " + sql);
        stmt.close();
    }

    private void initChecker() {
        Map<String, DataSource> map = highAvailableDataSource.getAvailableDataSourceMap();
        for (DataSource ds : map.values()) {
            DruidDataSource dds = (DruidDataSource) ds;
            dds.setValidConnectionChecker(checker);
        }
    }
}
