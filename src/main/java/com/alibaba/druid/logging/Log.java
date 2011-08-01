package com.alibaba.druid.logging;

public interface Log {

    boolean isDebugEnabled();

    void error(String s, Throwable e);

    void error(String s);

    public void debug(String s);

    public void debug(String s, Throwable e);

    public void warn(String s);

    void warn(String s, Throwable e);

}
