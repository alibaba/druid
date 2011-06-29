package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
import java.sql.SQLException;

import com.alibaba.druid.pool.ExceptionSorter;

public class SybaseExceptionSorter implements ExceptionSorter, Serializable {

    private static final long serialVersionUID = 2742592563671255116L;

    public boolean isExceptionFatal(SQLException e) {
        boolean result = false;

        String errorText = (e.getMessage()).toUpperCase();

        if ((errorText.indexOf("JZ0C0") > -1) || // ERR_CONNECTION_DEAD
            (errorText.indexOf("JZ0C1") > -1) // ERR_IOE_KILLED_CONNECTION
        ) {
            result = true;
        }

        return result;
    }
}
