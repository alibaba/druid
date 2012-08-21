package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;

import org.postgresql.core.BaseConnection;
import org.postgresql.xa.PGXAConnection;

public class PGUtils {

    public static XAConnection createXAConnection(Connection physicalConn) throws SQLException {
        return new PGXAConnection((BaseConnection) physicalConn);
    }
}
