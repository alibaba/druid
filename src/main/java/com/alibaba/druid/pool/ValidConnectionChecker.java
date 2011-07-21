package com.alibaba.druid.pool;

import java.sql.Connection;

public interface ValidConnectionChecker {
    boolean isValidConnection(Connection c, String query, int validationQueryTimeout) ;
}
