package com.alibaba.druid.wall;


public class WallRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WallRuntimeException(){
        super();
    }

    public WallRuntimeException(String reason, Throwable cause){
        super(reason, cause);
    }

    public WallRuntimeException(String reason){
        super(reason);
    }

    public WallRuntimeException(Throwable cause){
        super(cause);
    }

}
