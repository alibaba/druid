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
package com.alibaba.druid.support.spring.stat;

import com.alibaba.druid.filter.stat.StatFilterContext;
import com.alibaba.druid.filter.stat.StatFilterContextListenerAdapter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;

public class DruidStatInterceptor implements MethodInterceptor, InitializingBean, DisposableBean {

    public static final String          PROP_NAME_PROFILE   = "druid.profile";

    private final static Log            LOG                 = LogFactory.getLog(DruidStatInterceptor.class);

    private static SpringStat           springStat          = new SpringStat();

    private SpringMethodContextListener statContextListener = new SpringMethodContextListener();

    public DruidStatInterceptor(){

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SpringStatManager.getInstance().addSpringStat(springStat);

        StatFilterContext.getInstance().addContextListener(statContextListener);
    }

    @Override
    public void destroy() throws Exception {
        StatFilterContext.getInstance().removeContextListener(statContextListener);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        SpringMethodStat lastMethodStat = SpringMethodStat.current();

        SpringMethodInfo methodInfo = getMethodInfo(invocation);

        SpringMethodStat methodStat = springStat.getMethodStat(methodInfo, true);

        if (methodStat != null) {
            methodStat.beforeInvoke();
        }

        long startNanos = System.nanoTime();

        Throwable error = null;
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long endNanos = System.nanoTime();

            long nanos = endNanos - startNanos;

            if (methodStat != null) {
                methodStat.afterInvoke(error, nanos);
            }

            SpringMethodStat.setCurrent(lastMethodStat);
        }
    }

    public SpringMethodInfo getMethodInfo(MethodInvocation invocation) {
        Object thisObject = invocation.getThis();
        Method method = invocation.getMethod();

        if (thisObject == null) {
            return new SpringMethodInfo(method.getDeclaringClass(), method);
        }

        if (method.getDeclaringClass() == thisObject.getClass()) {
            return new SpringMethodInfo(method.getDeclaringClass(), method);
        }

        {
            Class<?> clazz = thisObject.getClass();
            boolean isCglibProxy = false;
            boolean isJavassistProxy = false;
            for (Class<?> item : clazz.getInterfaces()) {
                if (item.getName().equals("net.sf.cglib.proxy.Factory")) {
                    isCglibProxy = true;
                    break;
                } else if (item.getName().equals("javassist.util.proxy.ProxyObject")) {
                    isJavassistProxy = true;
                    break;
                }
            }

            if (isCglibProxy || isJavassistProxy) {
                Class<?> superClazz = clazz.getSuperclass();

                return new SpringMethodInfo(superClazz, method);
            }
        }

        Class<?> clazz = null;
        try {
            // 最多支持10层代理
            for (int i = 0; i < 10; ++i) {
                if (thisObject instanceof org.springframework.aop.framework.Advised) {
                    TargetSource targetSource = ((org.springframework.aop.framework.Advised) thisObject).getTargetSource();

                    if (targetSource == null) {
                        break;
                    }

                    Object target = targetSource.getTarget();
                    if (target != null) {
                        thisObject = target;
                    } else {
                        clazz = targetSource.getTargetClass();
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            LOG.error("getMethodInfo error", ex);
        }

        if (clazz == null) {
            return new SpringMethodInfo(method.getDeclaringClass(), method);
        }

        return new SpringMethodInfo(clazz, method);
    }

    class SpringMethodContextListener extends StatFilterContextListenerAdapter {

        @Override
        public void addUpdateCount(int updateCount) {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.addJdbcUpdateCount(updateCount);
            }
        }

        @Override
        public void addFetchRowCount(int fetchRowCount) {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.addJdbcFetchRowCount(fetchRowCount);
            }
        }

        @Override
        public void executeBefore(String sql, boolean inTransaction) {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcExecuteCount();
            }
        }

        @Override
        public void executeAfter(String sql, long nanos, Throwable error) {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.addJdbcExecuteTimeNano(nanos);
                if (error != null) {
                    springMethodStat.incrementJdbcExecuteErrorCount();
                }
            }
        }

        @Override
        public void commit() {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcCommitCount();
            }
        }

        @Override
        public void rollback() {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcRollbackCount();
            }
        }

        @Override
        public void pool_connect() {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcPoolConnectionOpenCount();
            }
        }

        @Override
        public void pool_close(long nanos) {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcPoolConnectionCloseCount();
            }
        }

        @Override
        public void resultSet_open() {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcResultSetOpenCount();
            }
        }

        @Override
        public void resultSet_close(long nanos) {
            SpringMethodStat springMethodStat = SpringMethodStat.current();
            if (springMethodStat != null) {
                springMethodStat.incrementJdbcResultSetCloseCount();
            }
        }
    }
}
