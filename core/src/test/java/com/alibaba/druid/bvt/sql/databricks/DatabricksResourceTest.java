package com.alibaba.druid.bvt.sql.databricks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class DatabricksResourceTest extends SQLResourceTest {

    public DatabricksResourceTest() {
        super(DbType.databricks);
    }

    @Test
    public void databricks_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/databricks/" + i + ".txt");
    }
}
