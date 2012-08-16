package com.alibaba.druid.support.logging;

import org.apache.log4j.Logger;

public class Log4jImpl implements Log {

    private Logger log;

    private int    errorCount;

    public Log4jImpl(Class<?> clazz){
        log = Logger.getLogger(clazz);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    public void error(String s) {
        log.error(s);
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
