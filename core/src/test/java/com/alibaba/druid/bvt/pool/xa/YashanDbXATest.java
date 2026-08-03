package com.alibaba.druid.bvt.pool.xa;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.util.YashanDbUtils;
import com.yashandb.jdbc.YasConnection;
import com.yashandb.xa.YasXAConnection;
import org.junit.jupiter.api.Test;

import javax.sql.XAConnection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbXATest {
    private static YasConnection createMockYasConnection() {
        return (YasConnection) Proxy.newProxyInstance(
                YasConnection.class.getClassLoader(),
                new Class[]{YasConnection.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("getSession".equals(method.getName())) {
                            return null;
                        }
                        if ("isClosed".equals(method.getName())) {
                            return false;
                        }
                        if ("close".equals(method.getName())) {
                            return null;
                        }
                        if ("getAutoCommit".equals(method.getName())) {
                            return true;
                        }
                        if ("setAutoCommit".equals(method.getName())) {
                            return null;
                        }
                        if ("toString".equals(method.getName())) {
                            return "MockYasConnection";
                        }
                        if ("hashCode".equals(method.getName())) {
                            return System.identityHashCode(proxy);
                        }
                        if ("equals".equals(method.getName())) {
                            return proxy == args[0];
                        }
                        return null;
                    }
                }
        );
    }

    @Test
    public void test_createXAConnection() throws Exception {
        YasConnection mockConn = createMockYasConnection();
        XAConnection xaConnection = YashanDbUtils.createXAConnection(mockConn);
        assertNotNull(xaConnection);
        assertTrue(xaConnection instanceof YasXAConnection);
        xaConnection.close();
    }

    @Test
    public void test_xaConnection_getConnection() throws Exception {
        YasConnection mockConn = createMockYasConnection();
        XAConnection xaConnection = YashanDbUtils.createXAConnection(mockConn);
        assertNotNull(xaConnection);
        Connection conn = xaConnection.getConnection();
        assertNotNull(conn);
        xaConnection.close();
    }

    @Test
    public void test_xaConnection_getXAResource() throws Exception {
        YasConnection mockConn = createMockYasConnection();
        XAConnection xaConnection = YashanDbUtils.createXAConnection(mockConn);
        assertNotNull(xaConnection);
        assertNotNull(xaConnection.getXAResource());
        xaConnection.close();
    }

    @Test
    public void test_druidXADataSource_yashandb_type_recognized() throws Exception {
        DruidXADataSource dataSource = new DruidXADataSource();
        dataSource.setUrl("jdbc:yasdb://127.0.0.1:1688/REGRESS");
        dataSource.setTestOnBorrow(false);

        assertEquals(DbType.yashandb, DbType.of("yashandb"));

        dataSource.close();
    }

    @Test
    public void test_druidXADataSource_yashandb_init() throws Exception {
        DruidXADataSource dataSource = new DruidXADataSource();
        dataSource.setUrl("jdbc:yasdb://127.0.0.1:1688/REGRESS");
        dataSource.setTestOnBorrow(false);
        dataSource.setDriverClassName("com.yashandb.jdbc.Driver");
        dataSource.init();

        assertTrue(dataSource.isInited());
        assertEquals("yashandb", dataSource.getDbType());

        dataSource.close();
    }
}
