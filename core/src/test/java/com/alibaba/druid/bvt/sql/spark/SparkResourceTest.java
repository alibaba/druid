package com.alibaba.druid.bvt.sql.spark;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class SparkResourceTest extends SQLResourceTest {
    public SparkResourceTest() {
        super(DbType.spark);
    }

    @Test
    public void tpcds() throws Exception {
        fileParse("bvt/parser/spark/tpcds/");
    }

    @Test
    public void test() throws Exception {
        fileParse("bvt/parser/spark/spark-tests/inputs");
    }
}
