package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class PGResourceTest extends SQLResourceTest {
    public PGResourceTest() {
        super(DbType.postgresql);
    }

    @Test
    public void test() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/postgresql/" + i + ".txt");
    }
}
