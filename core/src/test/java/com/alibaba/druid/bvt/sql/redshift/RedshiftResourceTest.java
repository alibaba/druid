package com.alibaba.druid.bvt.sql.redshift;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class RedshiftResourceTest extends SQLResourceTest {
        public RedshiftResourceTest() {
            super(DbType.redshift);
        }

        @Test
        public void redshift_parse() throws Exception {
            fileTest(0, 999, i -> "bvt/parser/redshift/" + i + ".txt");
        }
}

