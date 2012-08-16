package com.alibaba.druid.support.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JakartaCommonsLoggingImpl implements com.alibaba.druid.support.logging.Log {

    private Log log;

    private int errorCount;

    public JakartaCommonsLoggingImpl(Class<?> clazz){
        log = LogFactory.getLog(clazz);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void error(String s, Throwable e) {
        log.error(s, e);
        errorCount++;
    }

    public void error(String s) {
        log.error(s);
        errorCount++;
    }

    public void debug(String s) {
        log.debug(s);
    }

    public void debug(String s, Throwable e) {
        log.debug(s, e);
    }

    public void warn(String s) {
        log.warn(s);
    }

    @Override
    public void warn(String s, Throwable e) {
        log.warn(s, e);
    }

    public int getErrorCount() {
        return errorCount;
    }


    @Override
    public void resetStat() {
        errorCount = 0;
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        log.info(msg);
    }
}
