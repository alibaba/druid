package com.alibaba.druid.support.logging;

public class NoLoggingImpl implements Log {

    private int infoCount;
    private int errorCount;
    private int warnCount;

    public NoLoggingImpl(Class<?> clazz){
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void error(String s, Throwable e) {
        errorCount++;
    }

    public void error(String s) {
        errorCount++;
    }

    public void debug(String s) {
    }

    public void debug(String s, Throwable e) {
    }

    public void warn(String s) {
        warnCount++;
    }

    @Override
    public void warn(String s, Throwable e) {
        warnCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public int getWarnCount() {
        return warnCount;
    }

    @Override
    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String s) {
        infoCount++;
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    public int getInfoCount() {
        return infoCount;
    }
}
