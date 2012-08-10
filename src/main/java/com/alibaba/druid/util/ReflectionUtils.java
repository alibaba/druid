package com.alibaba.druid.util;

import java.lang.reflect.Constructor;
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
            LOG.warn(e.getMessage(), e);
        }
        if (result == null) {
            try {
                result = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        return result;
    }

    public static Object callStaticMethod(Class<?> classObject, String methodName) {

        try {
            Method m = classObject.getMethod(methodName);
            return m.invoke(classObject);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static Object callObjectMethod(Object obj, String methodName) {
        try {
            Constructor<?> ctor = ((Class<?>) obj).getConstructor();
            Object instance = ctor.newInstance();
            Method m = ((Class<?>) obj).getMethod(methodName); // 获取方法
            return m.invoke(instance);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static Object callObjectMethod(Object obj, String methodName, Object methodParam) {
        try {
            Constructor<?> ctor = ((Class<?>) obj).getConstructor();
            Object instance = ctor.newInstance();
            Method m = ((Class<?>) obj).getMethod(methodName, methodParam.getClass()); // 获取方法
            return m.invoke(instance);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public void getTest() {
        System.out.println("dd");
        // return "test";
    }

    public static void main(String args[]) {
        System.out.println(callObjectMethod(new ReflectionUtils(), "getTest"));

    }
}
