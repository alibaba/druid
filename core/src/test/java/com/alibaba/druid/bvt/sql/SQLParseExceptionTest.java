package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.parser.SQLParseException;
import junit.framework.TestCase;

@SuppressWarnings("deprecation")
public class SQLParseExceptionTest extends TestCase {
    public void test_new() throws Exception {
        new SQLParseException();
    }
}
