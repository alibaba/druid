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
package com.alibaba.druid.support.ibatis;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;
import com.ibatis.sqlmap.engine.scope.SessionScope;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IbatisUtils {
    private static Log     LOG               = LogFactory.getLog(IbatisUtils.class);

    private static boolean VERSION_2_3_4     = false;

    private static Method  methodGetId       = null;
    private static Method  methodGetResource = null;
    private static Field sessionField;

    static {
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("com.ibatis.sqlmap.engine.mapping.result.AutoResultMap");
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals("setResultObjectValues")) { // ibatis 2.3.4 add method 'setResultObjectValues'
                    VERSION_2_3_4 = true;
                    break;
                }
            }
        } catch (Throwable e) {
            LOG.error("Error while initializing", e);
        }
    }

    public static boolean isVersion2_3_4() {
        return VERSION_2_3_4;
    }

    public static SqlMapExecutor setClientImpl(SqlMapExecutor session, SqlMapClientImplWrapper clientImplWrapper) {
        if (session == null || clientImplWrapper == null) {
            return session;
        }

        if (session.getClass() == SqlMapSessionImpl.class) {
            SqlMapSessionImpl sessionImpl = (SqlMapSessionImpl) session;
            set(sessionImpl, clientImplWrapper);
        }

        return session;
    }

    /**
     * 通过反射的方式得到id，能够兼容2.3.0和2.3.4
     * 
     * @return
     */
    protected static String getId(Object statement) {
        try {
            if (methodGetId == null) {
                Class<?> clazz = statement.getClass();
                methodGetId = clazz.getMethod("getId");
            }

            Object returnValue = methodGetId.invoke(statement);

            if (returnValue == null) {
                return null;
            }

            return returnValue.toString();
        } catch (Exception ex) {
            LOG.error("createIdError", ex);
            return null;
        }
    }

    /**
     * 通过反射的方式得到resource，能够兼容2.3.0和2.3.4
     * 
     * @return
     */
    protected static String getResource(Object statement) {
        try {
            if (methodGetResource == null) {
                methodGetResource = statement.getClass().getMethod("getResource");
            }

            return (String) methodGetResource.invoke(statement);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void set(SqlMapSessionImpl session, SqlMapClientImpl client) {
        if (sessionField == null) {
            for (Field field : SqlMapSessionImpl.class.getDeclaredFields()) {
                if (field.getName().equals("session") || field.getName().equals("sessionScope")) {
                    sessionField = field;
                    sessionField.setAccessible(true);
                    break;
                }
            }
        }
        
        if (sessionField != null) {
            SessionScope sessionScope;
            try {
                sessionScope = (SessionScope) sessionField.get(session);
                
                if (sessionScope != null) {
                    if (sessionScope.getSqlMapClient() != null && sessionScope.getSqlMapClient().getClass() == SqlMapClientImpl.class) {
                        sessionScope.setSqlMapClient(client);
                    }
                    if (sessionScope.getSqlMapExecutor() != null && sessionScope.getSqlMapExecutor().getClass() == SqlMapClientImpl.class) {
                        sessionScope.setSqlMapExecutor(client);
                    }
                    if (sessionScope.getSqlMapTxMgr() != null && sessionScope.getSqlMapTxMgr().getClass() == SqlMapClientImpl.class) {
                        sessionScope.setSqlMapTxMgr(client);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
