package com.alibaba.druid.mapping;

public class DruidMappingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DruidMappingException(){
        super();
    }

    public DruidMappingException(String message, Throwable cause){
        super(message, cause);
    }

    public DruidMappingException(String message){
        super(message);
    }

    public DruidMappingException(Throwable cause){
        super(cause);
    }

}
