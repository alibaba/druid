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
package com.alibaba.druid.util;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class DruidWebUtils {

    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && !isValidAddress(ip)) {
            ip = null;
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (ip != null && !isValidAddress(ip)) {
                ip = null;
            }
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (ip != null && !isValidAddress(ip)) {
                ip = null;
            }
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip != null && !isValidAddress(ip)) {
                ip = null;
            }
        }

        return ip;
    }

    private static boolean isValidAddress(String ip) {
        if (ip == null) {
            return false;
        }

        for (int i = 0; i < ip.length(); ++i) {
            char ch = ip.charAt(i);
            if (ch >= '0' && ch <= '9') {
            } else if (ch >= 'A' && ch <= 'F') {
            } else if (ch >= 'a' && ch <= 'f') {
            } else if (ch == '.' || ch == ':') {
                //
            } else {
                return false;
            }
        }

        return true;
    }

    private static String getContextPath_2_5(ServletContext context) {
        String contextPath = context.getContextPath();

        if (contextPath == null || contextPath.length() == 0) {
            contextPath = "/";
        }

        return contextPath;
    }

    public static String getContextPath(ServletContext context) {
        if (context.getMajorVersion() == 2 && context.getMinorVersion() < 5) {
            return null;
        }

        try {
            return getContextPath_2_5(context);
        } catch (NoSuchMethodError error) {
            return null;
        }
    }

    public static Boolean getBoolean(GenericServlet servlet, String key) {
        String property = servlet.getInitParameter(key);
        if ("true".equals(property)) {
            return Boolean.TRUE;
        } else if ("false".equals(property)) {
            return Boolean.FALSE;
        }
        return null;
    }
}
