package com.alibaba.druid.bvt.sql.gaussdb;
import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;
public class GaussDbCreateTest extends SQLResourceTest {
    public GaussDbCreateTest() {
        super(DbType.gaussdb);
    }
    @Test
    public void gaussdb_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/gaussdb/" + i + ".txt");
    }
}
