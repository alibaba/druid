package com.alibaba.druid.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;

import org.h2.jdbc.JdbcConnection;
import org.h2.jdbcx.JdbcDataSourceFactory;
import org.h2.jdbcx.JdbcXAConnection;
import org.h2.message.TraceObject;

public class H2Utils {

    private static volatile Constructor<JdbcXAConnection> constructor;

    private static volatile Method                        method;

    public static final int                               XA_DATA_SOURCE = 13;

    public static Object createJdbcDataSourceFactory() {
        return new JdbcDataSourceFactory();
    }

    public static XAConnection createXAConnection(Object factory, Connection physicalConn) throws SQLException {

        try {
            if (constructor == null) {
                constructor = JdbcXAConnection.class.getConstructor(JdbcDataSourceFactory.class, int.class,
                                                                    JdbcConnection.class);
                constructor.setAccessible(true);
            }

            int id = getNextId(XA_DATA_SOURCE);

            return constructor.newInstance(factory, id, physicalConn);
        } catch (Exception e) {
            throw new SQLException("createXAConnection error", e);
        }
    }

    public static int getNextId(int type) throws Exception {
        if (method == null) {
            method = TraceObject.class.getMethod("getNextId", int.class);
            method.setAccessible(true);
        }

        return (Integer) method.invoke(null, type);
    }
}
