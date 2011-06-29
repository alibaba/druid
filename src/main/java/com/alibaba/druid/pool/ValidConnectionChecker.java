package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface ValidConnectionChecker {
    boolean isValidConnection(Connection c);
}
