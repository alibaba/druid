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
package com.alibaba.druid.support.logging;

import java.lang.reflect.Constructor;

@SuppressWarnings("rawtypes")
public class LogFactory {

    private static Constructor logConstructor;

    static {
        String logType= System.getProperty("druid.logType");
        if(logType != null){
            if(logType.equalsIgnoreCase("slf4j")){
                tryImplementation("org.slf4j.Logger", "com.alibaba.druid.support.logging.SLF4JImpl");
            }else if(logType.equalsIgnoreCase("log4j")){
                tryImplementation("org.apache.log4j.Logger", "com.alibaba.druid.support.logging.Log4jImpl");
            }else if(logType.equalsIgnoreCase("log4j2")){
                tryImplementation("org.apache.logging.log4j.Logger", "com.alibaba.druid.support.logging.Log4j2Impl");
            }else if(logType.equalsIgnoreCase("commonsLog")){
                tryImplementation("org.apache.commons.logging.LogFactory",
                        "com.alibaba.druid.support.logging.JakartaCommonsLoggingImpl");
            }else if(logType.equalsIgnoreCase("jdkLog")){
                tryImplementation("java.util.logging.Logger", "com.alibaba.druid.support.logging.Jdk14LoggingImpl");
            }
        }
        // 优先选择log4j,而非Apache Common Logging. 因为后者无法设置真实Log调用者的信息
        tryImplementation("org.slf4j.Logger", "com.alibaba.druid.support.logging.SLF4JImpl");
        tryImplementation("org.apache.log4j.Logger", "com.alibaba.druid.support.logging.Log4jImpl");
        tryImplementation("org.apache.logging.log4j.Logger", "com.alibaba.druid.support.logging.Log4j2Impl");
        tryImplementation("org.apache.commons.logging.LogFactory",
                          "com.alibaba.druid.support.logging.JakartaCommonsLoggingImpl");
        tryImplementation("java.util.logging.Logger", "com.alibaba.druid.support.logging.Jdk14LoggingImpl");

        if (logConstructor == null) {
            try {
                logConstructor = NoLoggingImpl.class.getConstructor(String.class);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void tryImplementation(String testClassName, String implClassName) {
        if (logConstructor != null) {
            return;
        }

        try {
            Resources.classForName(testClassName);
            Class implClass = Resources.classForName(implClassName);
            logConstructor = implClass.getConstructor(new Class[] { String.class });

            Class<?> declareClass = logConstructor.getDeclaringClass();
            if (!Log.class.isAssignableFrom(declareClass)) {
                logConstructor = null;
            }

            try {
                if (null != logConstructor) {
                    logConstructor.newInstance(LogFactory.class.getName());
                }
            } catch (Throwable t) {
                logConstructor = null;
            }

        } catch (Throwable t) {
            // skip
        }
    }

    public static Log getLog(Class clazz) {
        return getLog(clazz.getName());
    }

    public static Log getLog(String loggerName) {
        try {
            return (Log) logConstructor.newInstance(loggerName);
        } catch (Throwable t) {
            throw new RuntimeException("Error creating logger for logger '" + loggerName + "'.  Cause: " + t, t);
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void selectLog4JLogging() {
        try {
            Resources.classForName("org.apache.log4j.Logger");
            Class implClass = Resources.classForName("com.alibaba.druid.support.logging.Log4jImpl");
            logConstructor = implClass.getConstructor(new Class[] { String.class });
        } catch (Throwable t) {
            //ignore
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void selectJavaLogging() {
        try {
            Resources.classForName("java.util.logging.Logger");
            Class implClass = Resources.classForName("com.alibaba.druid.support.logging.Jdk14LoggingImpl");
            logConstructor = implClass.getConstructor(new Class[] { String.class });
        } catch (Throwable t) {
            //ignore
        }
    }
}
