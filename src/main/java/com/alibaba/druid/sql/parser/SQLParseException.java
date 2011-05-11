/**
 * Project: fastjson
 * 
 * File Created at 2010-12-2
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.druid.sql.parser;

/**
 * @author shaojin.wensj
 */
public class SQLParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SQLParseException() {
        super();
    }

    public SQLParseException(String message) {
        super(message);
    }

    public SQLParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
