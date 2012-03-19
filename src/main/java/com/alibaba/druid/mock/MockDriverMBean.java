package com.alibaba.druid.mock;

import java.sql.SQLException;


public interface MockDriverMBean {
    long getConnectionCloseCount();
    
    int getMajorVersion();
    
    int getMinorVersion();
    
    boolean jdbcCompliant();
    
    boolean acceptsURL(String url) throws SQLException;
    
    boolean isLogExecuteQueryEnable();
    
    void setLogExecuteQueryEnable(boolean logExecuteQueryEnable);
    
    long getIdleTimeCount();
    
    void setIdleTimeCount(long idleTimeCount);
    
    void closeAllConnections() throws SQLException;
    
    int getConnectionsSize();
}
