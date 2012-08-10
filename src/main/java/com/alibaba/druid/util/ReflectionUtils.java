package com.alibaba.druid.util;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class ReflectionUtils {

    private final static Log LOG = LogFactory.getLog(ReflectionUtils.class);

    public static Class<?> getClassFromWebContainerOrCurrentClassLoader(String className) {
        Class<?> result = null;
        try {
            result = HttpServletRequest.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled()) LOG.debug("can'r find class in web container classLoader ", e);
        }
        if (result == null) {
            try {
                result = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                LOG.error("can'r find class in current thread context classLoader ", e);
            }
        }
        return result;
    }

    public static Object callStaticMethod(Class<?> classObject, String methodName) {

        try {
            Method m = classObject.getMethod(methodName);
            return m.invoke(classObject);
        } catch (Exception e) {
            LOG.warn("callStaticMethod fail:class=" + classObject.getName() + " method=" + methodName, e);
            return null;
        }
    }

    public static Object callObjectMethod(Object obj, String methodName, Object... methodParams) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            return m.invoke(obj, methodParams);
        } catch (Exception e) {
            LOG.warn("callObjectMethod fail:class=" + obj.getClass().getName() + " method=" + methodName, e);
            return null;
        }
    }

    public void getTest() {
        System.out.println("dd");
        // return "test";
    }

    public static void main(String args[]) {
        System.out.println(callObjectMethod(new ReflectionUtils(), "getTest"));

    }
}
