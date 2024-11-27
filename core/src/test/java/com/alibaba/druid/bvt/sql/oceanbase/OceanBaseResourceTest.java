package com.alibaba.druid.bvt.sql.oceanbase;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class OceanBaseResourceTest extends SQLResourceTest {
    public OceanBaseResourceTest() {
        super(DbType.oceanbase);
    }

    @Test
    public void oceanbase_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/oceanbase/" + i + ".txt");
    }
}
