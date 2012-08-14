package com.alibaba.druid.support.spring.stat;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class DruidStatInterceptor implements MethodInterceptor, InitializingBean {

    private final static Log  LOG        = LogFactory.getLog(DruidStatInterceptor.class);

    private static SpringStat springStat = new SpringStat();
    
    public DruidStatInterceptor() {
        
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SpringStatManager.getInstance().addSpringStat(springStat);
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
            Object returnObject = invocation.proceed();

            return returnObject;
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
}
