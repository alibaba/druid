package com.alibaba.druid.pool;

import java.sql.SQLException;

/**
 * An interface to allow for exception evaluation.
 */
public interface ExceptionSorter {

    /**
     * Returns true or false whether or not the exception is fatal.
     * 
     * @param e the exception
     * @return true or false if the exception is fatal.
     */
    boolean isExceptionFatal(SQLException e);
}
