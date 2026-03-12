package com.alibaba.druid.bvt.sql.derby;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

public class DerbySelectTest {
    @Test
    public void test_for_derby() throws Exception {
        String sql = "select * from sys_user offset ? rows fetch next ? rows only";

        SQLUtils.parseSingleStatement(sql, DbType.derby);
    }
}
