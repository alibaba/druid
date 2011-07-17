/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.mock;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.mock.handler.MockExecuteHandler;
import com.alibaba.druid.mock.handler.MySqlMockExecuteHandlerImpl;

public class MockDriver implements Driver {
    private final static Log LOG = LogFactory.getLog(MockDriver.class);

    public final static MockExecuteHandler DEFAULT_HANDLER      = new MySqlMockExecuteHandlerImpl();

    private String                         prefix               = "jdbc:fake:";
    private String                         mockPrefix           = "jdbc:mock:";

    private MockExecuteHandler             executeHandler       = DEFAULT_HANDLER;

    private final static MockDriver        instance             = new MockDriver();

    private final AtomicLong               connectCount         = new AtomicLong();
    private final AtomicLong               connectionCloseCount = new AtomicLong();

    static {
        registerDriver(instance);
    }

    protected void incrementConnectionCloseCount() {
        connectionCloseCount.incrementAndGet();
    }

    public long getConnectionCloseCount() {
        return connectionCloseCount.get();
    }

    protected void afterConnectionClose(MockConnection conn) {
        connectionCloseCount.incrementAndGet();
    }

    public static boolean registerDriver(Driver driver) {
        try {
            DriverManager.registerDriver(driver);

            return true;
        } catch (Exception e) {
            LOG.error("registerDriver error", e);
        }

        return false;
    }

    public MockExecuteHandler getExecuteHandler() {
        return executeHandler;
    }

    public void setExecuteHandler(MockExecuteHandler executeHandler) {
        this.executeHandler = executeHandler;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        if (info != null) {
            Object val = info.get("connectSleep");
            if (val != null) {
                long millis = Long.parseLong(val.toString());
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    // skip
                }
            }
        }

        MockConnection conn = new MockConnection(this, info);

        if (url == null) {
            connectCount.incrementAndGet();
            return conn;
        }

        if (url.startsWith(prefix)) {
            String catalog = url.substring(prefix.length());
            conn.setCatalog(catalog);

            connectCount.incrementAndGet();
            return conn;
        }

        if (url.startsWith(mockPrefix)) {
            String catalog = url.substring(mockPrefix.length());
            conn.setCatalog(catalog);

            connectCount.incrementAndGet();
            return conn;
        }

        return null;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }

        return url.startsWith(prefix) || url.startsWith(mockPrefix);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return null;
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    protected ResultSet executeQuery(MockStatement stmt, String sql) throws SQLException {
        MockConnection conn = stmt.getMockConnection();

        if (conn != null) {
            if (conn.getConnectProperties() != null) {
                Object propertyValue = conn.getConnectProperties().get("executeSleep");

                if (propertyValue != null) {
                    long millis = Long.parseLong(propertyValue.toString());
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        // skip
                    }
                }
            }
        }

        if ("SELECT value FROM _int_1000_".equalsIgnoreCase(sql)) {
            MockResultSet rs = new MockResultSet(stmt);

            for (int i = 0; i < 1000; ++i) {
                rs.getRows().add(new Object[] { i });
            }

            return rs;
        }

        return this.executeHandler.executeQuery(stmt, sql);
    }

    protected ResultSet createResultSet(MockPreparedStatement stmt) {
        MockResultSet rs = new MockResultSet(stmt);

        String sql = stmt.getSql();

        if ("SELECT 1".equalsIgnoreCase(sql)) {
            rs.getRows().add(new Object[] { 1 });
        } else if ("SELECT NOW()".equalsIgnoreCase(sql)) {
            rs.getRows().add(new Object[] { new java.sql.Timestamp(System.currentTimeMillis()) });
        } else if ("SELECT ?".equalsIgnoreCase(sql)) {
            rs.getRows().add(new Object[] { stmt.getParameters().get(0) });
        }

        return rs;
    }

    protected Clob createClob(MockConnection conn) throws SQLException {
        return new MockClob();
    }

    protected Blob createBlob(MockConnection conn) throws SQLException {
        return new MockBlob();
    }

    protected NClob createNClob(MockConnection conn) throws SQLException {
        return new MockNClob();
    }

    protected SQLXML createSQLXML(MockConnection conn) throws SQLException {
        return new MockSQLXML();
    }
}
