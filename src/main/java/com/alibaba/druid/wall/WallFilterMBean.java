package com.alibaba.druid.wall;

import java.sql.SQLException;
import java.util.Set;

public interface WallFilterMBean {

    String getDbType();

    boolean isLogViolation();

    void setLogViolation(boolean logViolation);

    boolean isThrowException();
    
    void setThrowException(boolean throwException);
    
    boolean isInited();
    
    void clearProviderCache();
    
    Set<String> getProviderWhiteList();
    
    void check(String sql) throws SQLException;
}
