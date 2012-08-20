package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;

public class MySqlUtils {

    public static MysqlXAConnection createXAConnection(Connection physicalConn) throws SQLException {
        return new MysqlXAConnection((com.mysql.jdbc.ConnectionImpl) physicalConn, false);
    }
}
