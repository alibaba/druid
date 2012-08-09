package com.alibaba.druid.util;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class ReflectionUtils
{

    public static Class<?> getClassFromWebContainerOrCurrentClassLoader(String className)
    {
        Class<?> result = null;
        try
        {
            result = HttpServletRequest.class.getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e)
        {
        }
        if (result == null)
        {
            try
            {
                result = Thread.currentThread().getContextClassLoader().loadClass(className);
            }
            catch (ClassNotFoundException e)
            {
            }
        }
        return result;
    }


    public static Object callStaticMethod(Class<?> classObject, String methodName)
    {

        try
        {
            Method m = classObject.getMethod(methodName);
            return m.invoke(classObject);
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public static Object callObjectMethod(Object obj, String methodName)
    {

        try
        {
            Method m = obj.getClass().getMethod(methodName);
            return m.invoke(obj);
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public static Object callObjectMethod(Object obj, String methodName, Object methodParam)
    {
        try
        {
            Method m = obj.getClass().getMethod(methodName);
            return m.invoke(obj,methodParam);
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public void getTest()
    {
        System.out.println("dd");
        // return "test";
    }


    public static void main(String args[])
    {
        System.out.println(callObjectMethod(new ReflectionUtils(), "getTest"));

    }
}
