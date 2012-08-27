package com.alibaba.druid.pool;

/**
 * 加密出错
 *
 * @author Jonas Yang
 */
public class DecryptException extends Exception {
    public DecryptException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public DecryptException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public DecryptException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public DecryptException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
