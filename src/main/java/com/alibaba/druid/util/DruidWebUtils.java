package com.alibaba.druid.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class DruidWebUtils {

    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
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
}
