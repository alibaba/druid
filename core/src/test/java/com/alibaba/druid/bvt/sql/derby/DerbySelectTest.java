package com.alibaba.druid.bvt.sql.derby;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class DerbySelectTest extends TestCase {
    public void test_for_derby() throws Exception {
        String sql = "select * from sys_user offset ? rows fetch next ? rows only";

        SQLUtils.parseSingleStatement(sql, DbType.derby);
    }
}
