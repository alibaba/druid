package com.alibaba.druid.pool;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleStatement;


public class OracleUtils {
    public static void clearDefines(PoolablePreparedStatement stmt) throws SQLException {
        PreparedStatement raw = stmt.getRawPreparedStatement();
        if (raw instanceof OracleStatement) {
            OracleStatement oracleStmt = (OracleStatement) raw;
            oracleStmt.clearDefines();
        }
    }
}
