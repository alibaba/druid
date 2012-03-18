package com.alibaba.druid.logging;

public class NoLoggingImpl implements Log {

    public NoLoggingImpl(Class<?> clazz){
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void error(String s, Throwable e) {
    }

    public void error(String s) {
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

}
