package com.alibaba.druid.util;

import java.lang.reflect.Method;
import java.util.Map;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;


public class JdbcSqlStatUtils {
    private final static Log LOG = LogFactory.getLog(JdbcSqlStatUtils.class);
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getData(Object jdbcSqlStat) {
        try {
            Method method = jdbcSqlStat.getClass().getMethod("getData");
            Object obj = method.invoke(jdbcSqlStat);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getData error", e);
            return null;
        }
    }
}
