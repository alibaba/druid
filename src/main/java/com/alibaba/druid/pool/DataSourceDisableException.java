package com.alibaba.druid.pool;

import java.sql.SQLException;

public class DataSourceDisableException extends SQLException {

    private static final long serialVersionUID = 1L;

    public DataSourceDisableException(){
        super();
    }

    public DataSourceDisableException(String reason){
        super(reason);
    }

    public DataSourceDisableException(Throwable cause){
        super(cause);
    }

}
