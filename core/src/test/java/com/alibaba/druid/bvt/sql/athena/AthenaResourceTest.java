package com.alibaba.druid.bvt.sql.athena;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class AthenaResourceTest extends SQLResourceTest {

    public AthenaResourceTest() {
        super(DbType.athena);
    }

    @Test
    public void athena_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/athena/" + i + ".txt");
    }
}
