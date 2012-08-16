package com.alibaba.druid.support.logging;

public class NoLoggingImpl implements Log {

    private int errorCount;

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
    }

    @Override
    public void warn(String s, Throwable e) {

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
        return false;
    }

    @Override
    public void info(String s) {
        
    }
}
