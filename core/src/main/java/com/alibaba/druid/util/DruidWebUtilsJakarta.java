package com.alibaba.druid.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public class DruidWebUtilsJakarta {
    private DruidWebUtilsJakarta() {
    }

  public static String getRemoteAddr(final HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.contains(",")) { //截取逗号前第一个ip视为源头ip
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        if (ip != null && !DruidWebUtils.isValidAddress(ip)) {
            ip = null;
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (ip != null && !DruidWebUtils.isValidAddress(ip)) {
                ip = null;
            }
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (ip != null && !DruidWebUtils.isValidAddress(ip)) {
                ip = null;
            }
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip != null && !DruidWebUtils.isValidAddress(ip)) {
                ip = null;
            }
        }

        return ip;
    }

    private static String getContextPath_2_5(final ServletContext context) {
        String contextPath = context.getContextPath();

        if (contextPath == null || contextPath.length() == 0) {
            contextPath = "/";
        }

        return contextPath;
    }

    public static String getContextPath(final ServletContext context) {
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
