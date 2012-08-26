package com.alibaba.druid.support.console;

public class OptionParseException extends Exception {

    public OptionParseException(String msg) {
        super(msg);
    }

    public OptionParseException(String msg, Throwable cause) {
        super(msg,cause);
    }

    public OptionParseException(Throwable cause) {
        super(cause);
    }

}
