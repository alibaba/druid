/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import javax.sql.XAConnection;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class MySqlUtils {
    static Class<?> utilClass;
    static boolean utilClassError = false;
    static boolean utilClass_isJdbc4 = false;

    static Class<?> connectionClass = null;
    static Method getPinGlobalTxToPhysicalConnectionMethod = null;

    static Class<?> suspendableXAConnectionClass = null;
    static Constructor<?> suspendableXAConnectionConstructor = null;

    static Class<?> JDBC4SuspendableXAConnectionClass = null;
    static Constructor<?> JDBC4SuspendableXAConnectionConstructor = null;

    static Class<?> MysqlXAConnectionClass = null;
    static Constructor<?> MysqlXAConnectionConstructor = null;

    public static XAConnection createXAConnection(Driver driver, Connection physicalConn) throws SQLException {
        if (driver.getMajorVersion() == 5) {
            if (utilClass == null && !utilClassError) {
                try {
                    utilClass = Class.forName("com.mysql.jdbc.Util");

                    Method method = utilClass.getMethod("isJdbc4");
                    utilClass_isJdbc4 = (Boolean) method.invoke(null);

                    connectionClass = Class.forName("com.mysql.jdbc.Connection");
                    getPinGlobalTxToPhysicalConnectionMethod = connectionClass.getMethod("getPinGlobalTxToPhysicalConnection");

                    suspendableXAConnectionClass = Class.forName("com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection");
                    suspendableXAConnectionConstructor = suspendableXAConnectionClass.getConstructor(connectionClass);

                    JDBC4SuspendableXAConnectionClass = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection");
                    JDBC4SuspendableXAConnectionConstructor = JDBC4SuspendableXAConnectionClass.getConstructor(connectionClass);

                    MysqlXAConnectionClass = Class.forName("com.mysql.jdbc.jdbc2.optional.MysqlXAConnection");
                    MysqlXAConnectionConstructor = MysqlXAConnectionClass.getConstructor(connectionClass, boolean.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    utilClassError = true;
                }
            }

            try {
                boolean pinGlobTx = (Boolean) getPinGlobalTxToPhysicalConnectionMethod.invoke(physicalConn);
                if (pinGlobTx) {
                    if (!utilClass_isJdbc4) {
                        return (XAConnection) suspendableXAConnectionConstructor.newInstance(physicalConn);
                    }

                    return (XAConnection) JDBC4SuspendableXAConnectionConstructor.newInstance(physicalConn);
                }

                return (XAConnection) MysqlXAConnectionConstructor.newInstance(physicalConn, Boolean.FALSE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        throw new SQLFeatureNotSupportedException();
    }
}
