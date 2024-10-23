package com.alibaba.druid.bvt.sql.teradata;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class TDResourceTest extends SQLResourceTest {
    public TDResourceTest() {
        super(DbType.teradata);
    }

    @Test
    public void teradata_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/teradata/" + i + ".txt");
    }
}
