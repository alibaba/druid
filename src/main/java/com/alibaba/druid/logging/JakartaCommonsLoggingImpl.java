package com.alibaba.druid.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class JakartaCommonsLoggingImpl implements com.alibaba.druid.logging.Log {

    private Log log;

    public JakartaCommonsLoggingImpl(Class clazz){
        log = LogFactory.getLog(clazz);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    public void error(String s) {
        log.error(s);
    }

    public void debug(String s) {
        log.debug(s);
    }

    public void warn(String s) {
        log.warn(s);
    }

    @Override
    public void warn(String s, Throwable e) {
        log.warn(s, e);
    }

}
