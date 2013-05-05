/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
        // TODO add slf4j logging

        // 优先选择log4j,而非Apache Common Logging. 因为后者无法设置真实Log调用者的信息
		tryImplementation("org.slf4j.Logger", "com.alibaba.druid.support.logging.SLF4JImpl");
        tryImplementation("org.apache.log4j.Logger", "com.alibaba.druid.support.logging.Log4jImpl");
        tryImplementation("java.util.logging.Logger", "com.alibaba.druid.support.logging.Jdk14LoggingImpl");
        tryImplementation("org.apache.commons.logging.LogFactory",
                          "com.alibaba.druid.support.logging.JakartaCommonsLoggingImpl");

        if (logConstructor == null) {
            try {
                logConstructor = NoLoggingImpl.class.getConstructor(Class.class);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        tryImplementation("java.lang.Object", "com.alibaba.druid.support.logging.NoLoggingImpl");
    }

    @SuppressWarnings("unchecked")
    private static void tryImplementation(String testClassName, String implClassName) {
        if (logConstructor != null) {
            return;
        }

        try {
            Resources.classForName(testClassName);
            Class implClass = Resources.classForName(implClassName);
            logConstructor = implClass.getConstructor(new Class[] { Class.class });

            Class<?> declareClass = logConstructor.getDeclaringClass();
            if (!Log.class.isAssignableFrom(declareClass)) {
                logConstructor = null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static Log getLog(Class aClass) {
        try {
            return (Log) logConstructor.newInstance(new Object[] { aClass });
        } catch (Throwable t) {
            throw new RuntimeException("Error creating logger for class " + aClass + ".  Cause: " + t, t);
        }
    }

    /**
     * This method will switch the logging implementation to Log4J if Log4J is available on the classpath. This is
     * useful in situations where you want to use Log4J to log iBATIS activity but commons logging is on the classpath.
     * Note that this method is only effective for log classes obtained after calling this method. If you intend to use
     * this method you should call it before calling any other iBATIS method.
     */
    @SuppressWarnings("unchecked")
    public static synchronized void selectLog4JLogging() {
        try {
            Resources.classForName("org.apache.log4j.Logger");
            Class implClass = Resources.classForName("com.alibaba.druid.support.logging.Log4jImpl");
            logConstructor = implClass.getConstructor(new Class[] { Class.class });
        } catch (Throwable t) {
        }
    }

    /**
     * This method will switch the logging implementation to Java native logging if you are running in JRE 1.4 or above.
     * This is useful in situations where you want to use Java native logging to log iBATIS activity but commons logging
     * or Log4J is on the classpath. Note that this method is only effective for log classes obtained after calling this
     * method. If you intend to use this method you should call it before calling any other iBATIS method.
     */
    @SuppressWarnings("unchecked")
    public static synchronized void selectJavaLogging() {
        try {
            Resources.classForName("java.util.logging.Logger");
            Class implClass = Resources.classForName("com.alibaba.druid.support.logging.Jdk14LoggingImpl");
            logConstructor = implClass.getConstructor(new Class[] { Class.class });
        } catch (Throwable t) {
        }
    }
}
