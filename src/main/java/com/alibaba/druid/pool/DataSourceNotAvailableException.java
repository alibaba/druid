package com.alibaba.druid.pool;

import java.sql.SQLException;

public class DataSourceNotAvailableException extends SQLException {

    private static final long serialVersionUID = 1L;

    public DataSourceNotAvailableException(Throwable cause){
        super(cause);
    }

}

