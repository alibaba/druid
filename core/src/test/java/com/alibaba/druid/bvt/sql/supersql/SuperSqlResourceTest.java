package com.alibaba.druid.bvt.sql.supersql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class SuperSqlResourceTest extends SQLResourceTest {

    public SuperSqlResourceTest() {
        super(DbType.supersql);
    }

    @Test
    public void supersql_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/supersql/" + i + ".txt");
    }
}
