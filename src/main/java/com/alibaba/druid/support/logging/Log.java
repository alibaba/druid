package com.alibaba.druid.support.logging;

public interface Log {

    boolean isDebugEnabled();

    void error(String msg, Throwable e);

    void error(String msg);

    boolean isInfoEnabled();

    void info(String msg);

    void debug(String msg);

    void debug(String msg, Throwable e);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String msg, Throwable e);

    int getErrorCount();

    int getWarnCount();

    int getInfoCount();

    void resetStat();
}
