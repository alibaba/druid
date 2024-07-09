package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class BigQueryResourceTest extends SQLResourceTest {
    public BigQueryResourceTest() {
        super(DbType.bigquery);
    }

    @Test
    public void bigquery_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/bigquery/" + i + ".txt");
    }
}
