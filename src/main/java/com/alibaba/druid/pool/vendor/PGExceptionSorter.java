package com.alibaba.druid.pool.vendor;

import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.ExceptionSorter;

public class PGExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        String sqlState = e.getSQLState();
        if (sqlState == null) {
            return false;
        }
        
        // org.postgresql.util.PSQLState
        if (sqlState.startsWith("08")) {
            return true;
        }
        
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }

}
