package com.alibaba.druid.pool;

import java.sql.SQLException;

public class GetConnectionTimeoutException extends SQLException {

    private static final long serialVersionUID = 1L;

    public GetConnectionTimeoutException(){
        super();
    }

    public GetConnectionTimeoutException(String reason){
        super(reason);
    }

    public GetConnectionTimeoutException(Throwable cause){
        super(cause);
    }

}
