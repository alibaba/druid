package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface ValidConnectionChecker {
    SQLException isValidConnection(Connection c);
}
