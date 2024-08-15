package com.alibaba.druid.bvt.sql.starrocks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class StarrocksResourceTest extends SQLResourceTest {
    public StarrocksResourceTest() {
        super(DbType.starrocks);
    }

    @Test
    public void starrocks_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/starrocks/" + i + ".txt");
    }
}
