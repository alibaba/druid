package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLParserUtilsTest {
    @Test
    public void test_0() throws Exception {
        assertEquals(
                SQLType.SET_PROJECT,
                SQLParserUtils.getSQLTypeV2("setproject odps.sql.allow.fullscan=true;", DbType.odps));
    }
}
