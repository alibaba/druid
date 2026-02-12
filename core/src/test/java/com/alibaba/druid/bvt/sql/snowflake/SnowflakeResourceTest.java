package com.alibaba.druid.bvt.sql.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class SnowflakeResourceTest extends SQLResourceTest {
    public SnowflakeResourceTest() {
        super(DbType.snowflake);
    }

    @Test
    public void snowflake_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/snowflake/" + i + ".txt");
    }
}
