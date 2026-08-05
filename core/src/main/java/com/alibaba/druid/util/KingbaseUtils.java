/*
 * Utility for Kingbase XA support via reflection, to avoid hard dependency.
 */
package com.alibaba.druid.util;

import javax.sql.XAConnection;

import java.lang.reflect.Method;
import java.sql.SQLException;

public class KingbaseUtils {
    private static final String[] CANDIDATE_XA_DS_CLASSNAMES = new String[] {
            "com.kingbase8.xa.KBXADataSource",
            "com.kingbase8.xa.KbXADataSource",
            "com.kingbase.xa.KbXADataSource",
            "kingbase8.xa.KbXADataSource",
            "kingbase.xa.KbXADataSource"
    };

    public static XAConnection createXAConnection(String url, String user, String password) throws SQLException {
        Exception lastError = null;
        for (String className : CANDIDATE_XA_DS_CLASSNAMES) {
            try {
                Class<?> clazz = Class.forName(className);
                Object xaDs = clazz.getDeclaredConstructor().newInstance();

                // try common setter variants
                safeInvokeSetter(xaDs, "setUrl", String.class, url);
                safeInvokeSetter(xaDs, "setURL", String.class, url);
                safeInvokeSetter(xaDs, "setUser", String.class, user);
                safeInvokeSetter(xaDs, "setUsername", String.class, user);
                safeInvokeSetter(xaDs, "setPassword", String.class, password);

                // obtain XAConnection: prefer no-arg if properties are set on XADataSource
                try {
                    Method m = clazz.getMethod("getXAConnection");
                    Object xaConn = m.invoke(xaDs);
                    return (XAConnection) xaConn;
                } catch (NoSuchMethodException ignore) {
                    // fallback: getXAConnection(user, password)
                    Method m = clazz.getMethod("getXAConnection", String.class, String.class);
                    Object xaConn = m.invoke(xaDs, user, password);
                    return (XAConnection) xaConn;
                }
            } catch (Exception ex) {
                lastError = ex;
            }
        }
        SQLException sqlException = new SQLException("kingbase xa not supported: XADataSource class not found or incompatible");
        if (lastError != null) {
            sqlException.initCause(lastError);
        }
        throw sqlException;
    }

    private static void safeInvokeSetter(Object target, String methodName, Class<?> argType, Object argValue) {
        try {
            Method m = target.getClass().getMethod(methodName, argType);
            m.invoke(target, argValue);
        } catch (Exception ignore) {
        }
    }
}
