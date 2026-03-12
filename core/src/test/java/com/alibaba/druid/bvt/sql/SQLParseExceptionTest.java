package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.parser.SQLParseException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class SQLParseExceptionTest {
    @Test
    public void test_new() throws Exception {
        new SQLParseException();
    }
}
