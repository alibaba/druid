package com.alibaba.druid.pool.vendor;

import java.sql.SQLException;

import com.alibaba.druid.mock.MockConnectionClosedException;
import com.alibaba.druid.pool.ExceptionSorter;

public class MockExceptionSorter implements ExceptionSorter {

    private final static MockExceptionSorter instance = new MockExceptionSorter();

    public final static MockExceptionSorter getInstance() {
        return instance;
    }

    @Override
    public boolean isExceptionFatal(SQLException e) {
        if (e instanceof MockConnectionClosedException) {
            return true;
        }
        return false;
    }

}
