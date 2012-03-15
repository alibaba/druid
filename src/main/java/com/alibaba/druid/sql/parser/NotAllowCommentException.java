package com.alibaba.druid.sql.parser;

public class NotAllowCommentException extends ParserException {

    private static final long serialVersionUID = 1L;

    public NotAllowCommentException(){
        super();
    }

    public NotAllowCommentException(String message, Throwable e){
        super(message, e);
    }

    public NotAllowCommentException(String message){
        super(message);
    }

}
