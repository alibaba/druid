package com.alibaba.druid.wall;

public class WallContext {

    private final static ThreadLocal<WallContext> contextLocal = new ThreadLocal<WallContext>();

    private WallSqlStat                           sqlState;

    public static WallContext createIfNotExists() {
        WallContext context = contextLocal.get();
        if (context == null) {
            context = new WallContext();
            contextLocal.set(context);
        }
        return context;
    }

    public static WallContext current() {
        return contextLocal.get();
    }

    public static void clearContext() {
        contextLocal.remove();
    }

    public WallSqlStat getSqlState() {
        return sqlState;
    }

    public void setSqlState(WallSqlStat sqlState) {
        this.sqlState = sqlState;
    }

}
