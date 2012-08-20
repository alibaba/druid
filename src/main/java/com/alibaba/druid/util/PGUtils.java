package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.core.BaseConnection;
import org.postgresql.xa.PGXAConnection;

public class PGUtils {

    public static PGXAConnection createXAConnection(Connection physicalConn) throws SQLException {
        return new PGXAConnection((BaseConnection) physicalConn);
    }
}
