package com.alibaba.druid.support.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Jdk14LoggingImpl implements Log {

    private Logger log;

    private int    errorCount;
    private int    warnCount;
    private int    infoCount;

    public Jdk14LoggingImpl(Class<?> clazz){
        log = Logger.getLogger(clazz.toString());
    }

    public boolean isDebugEnabled() {
        return log.isLoggable(Level.FINE);
    }

    public void error(String s, Throwable e) {
        log.log(Level.SEVERE, s, e);
        errorCount++;
    }

    public void error(String s) {
        log.log(Level.SEVERE, s);
        errorCount++;
    }

    public void debug(String s) {
        log.log(Level.FINE, s);
    }

    public void debug(String s, Throwable e) {
        log.log(Level.FINE, s, e);
    }

    public void warn(String s) {
        log.log(Level.WARNING, s);
        warnCount++;
    }

    @Override
    public void warn(String s, Throwable e) {
        log.log(Level.WARNING, s, e);
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
        return log.isLoggable(Level.INFO);
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
        return log.isLoggable(Level.WARNING);
    }
}
