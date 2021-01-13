package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;

public class MySqlWallTest152 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setSelectLimit(100);

        String sql = "SELECT version FROM schema_migrations";

//        assertTrue(
//                provider.checkValid(sql)
//        );

        WallCheckResult result = provider.check(sql);
        assertEquals(0, result.getViolations().size());
        String wsql = result
                .getStatementList().get(0).toString();

        assertEquals("SELECT version\n" +
                "FROM schema_migrations\n" +
                "LIMIT 100", wsql);
    }
}
