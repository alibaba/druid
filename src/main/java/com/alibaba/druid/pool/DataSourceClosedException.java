package com.alibaba.druid.pool;

import java.sql.SQLException;

public class DataSourceClosedException extends SQLException {

    private static final long serialVersionUID = 1L;

    public DataSourceClosedException(){
        super();
    }

    public DataSourceClosedException(String reason){
        super(reason);
    }

    public DataSourceClosedException(Throwable cause){
        super(cause);
    }

}
