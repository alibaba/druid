package com.alibaba.druid.wall;

import java.sql.SQLException;

public class WallSQLException extends SQLException {

    private static final long serialVersionUID = 1L;

    public WallSQLException(){
        super();
    }

    public WallSQLException(String reason, Throwable cause){
        super(reason, cause);
    }

    public WallSQLException(String reason){
        super(reason);
    }

    public WallSQLException(Throwable cause){
        super(cause);
    }

}
