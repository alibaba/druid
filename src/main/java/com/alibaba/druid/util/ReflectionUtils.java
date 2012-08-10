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

    public static Class<?> getClassFromWebContainer(String className) {
        Class<?> result = null;
        try {
            result = HttpServletRequest.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled()) LOG.debug("can'r find class in web container classLoader ", e);
        }
        return result;
    }

    public static Class<?> getClassFromCurrentClassLoader(String className) {
        Class<?> result = null;
        try {
            result = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            LOG.error("can'r find class in current thread context classLoader ", e);
        }
        return result;
    }

    public static Class<?> getClassFromWebContainerOrCurrentClassLoader(String className) {
        Class<?> result = getClassFromWebContainer(className);
        if (result == null) {
            result = getClassFromCurrentClassLoader(className);
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

    public static Object callObjectMethod(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            return m.invoke(obj);
        } catch (Exception e) {
            LOG.warn("callObjectMethod fail:class=" + obj.getClass().getName() + " method=" + methodName, e);
            return null;
        }
    }

    public static Method getObjectMethod(Object obj, String methodName) {
        try {
            return obj.getClass().getMethod(methodName);
        } catch (Exception e) {
            LOG.warn("getObjectMethod fail:class=" + obj.getClass().getName() + " method=" + methodName, e);
            return null;
        }
    }

    public static Method getObjectMethod(Object obj, String methodName, Integer id) {
        try {
            return obj.getClass().getMethod(methodName, Integer.class);
        } catch (Exception e) {
            LOG.warn("getObjectMethod fail:class=" + obj.getClass().getName() + " method=" + methodName, e);
            return null;
        }
    }

    public static Object callObjectMethod(Object obj, String methodName, Integer id) {
        try {
            Method m = obj.getClass().getMethod(methodName, Integer.class);
            return m.invoke(obj, id);
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
