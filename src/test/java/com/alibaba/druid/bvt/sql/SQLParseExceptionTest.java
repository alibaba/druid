package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import com.alibaba.druid.sql.parser.SQLParseException;

@SuppressWarnings("deprecation")
public class SQLParseExceptionTest extends TestCase {

    public void test_new() throws Exception {
        new SQLParseException();
    }
}
