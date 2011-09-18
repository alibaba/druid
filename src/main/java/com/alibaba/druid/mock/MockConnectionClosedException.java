package com.alibaba.druid.mock;

import java.sql.SQLException;

public class MockConnectionClosedException extends SQLException {

    private static final long serialVersionUID = 1L;

    public MockConnectionClosedException() {
        super();
    }
}
