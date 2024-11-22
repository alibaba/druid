package com.alibaba.druid.bvt.sql.doris;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class DorisResourceTest extends SQLResourceTest {
    public DorisResourceTest() {
        super(DbType.doris);
    }

    @Test
    public void doris_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/doris/" + i + ".txt");
    }
}
