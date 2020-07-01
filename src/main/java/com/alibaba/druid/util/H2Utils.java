/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
                constructor = JdbcXAConnection.class.getDeclaredConstructor(JdbcDataSourceFactory.class, int.class,
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
            method = TraceObject.class.getDeclaredMethod("getNextId", int.class);
            method.setAccessible(true);
        }

        return (Integer) method.invoke(null, type);
    }
}
