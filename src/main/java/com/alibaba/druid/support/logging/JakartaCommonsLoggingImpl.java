package com.alibaba.druid.support.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JakartaCommonsLoggingImpl implements com.alibaba.druid.support.logging.Log {

    private Log log;

    private int errorCount;
    private int warnCount;
    private int infoCount;

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
        warnCount++;
    }

    @Override
    public void warn(String s, Throwable e) {
        log.warn(s, e);
        warnCount++;
    }

    @Override
    public int getWarnCount() {
        return warnCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        log.info(msg);
        infoCount++;
    }
    
    @Override
    public int getInfoCount() {
        return infoCount;
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }
}
