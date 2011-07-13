package com.alibaba.druid.pool.vendor;

import java.sql.SQLException;

import com.alibaba.druid.pool.ExceptionSorter;

public class NullExceptionSorter implements ExceptionSorter {

    private final static NullExceptionSorter instance = new NullExceptionSorter();

    public final static NullExceptionSorter getInstance() {
        return instance;
    }

    @Override
    public boolean isExceptionFatal(SQLException e) {
        return false;
    }

}
