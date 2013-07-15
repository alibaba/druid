package com.alibaba.druid.pool.vendor;

import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.ExceptionSorter;

public class DB2ExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        int errorCode = e.getErrorCode();
        switch (errorCode) { // THE CURSOR IS NOT IN A PREPARED STATE
            case -514:
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }

}
