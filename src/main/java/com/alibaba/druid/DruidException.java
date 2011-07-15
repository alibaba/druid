package com.alibaba.druid;

public class DruidException extends Exception {

    private static final long serialVersionUID = 1L;

    public DruidException(){
        super();
    }

    public DruidException(String message, Throwable cause){
        super(message, cause);
    }

    public DruidException(String message){
        super(message);
    }

    public DruidException(Throwable cause){
        super(cause);
    }

}
