package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.SQLException;


public class MySqlUtils {
    public static com.mysql.jdbc.Connection unwrap(Connection conn) throws SQLException {
        if (conn instanceof com.mysql.jdbc.Connection) {
            return (com.mysql.jdbc.Connection) conn;
        }

        return conn.unwrap(com.mysql.jdbc.Connection.class);
    }
    
    public static void ping(Connection conn) throws SQLException {
        com.mysql.jdbc.Connection mysqlConn = unwrap(conn);
        mysqlConn.ping();
    }
}
