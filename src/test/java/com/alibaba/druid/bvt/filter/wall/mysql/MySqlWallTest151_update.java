package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;

public class MySqlWallTest151_update extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setSelectLimit(100);

        String sql = "update twogradeestate set layarea=layarea+97.0, selfarea=selfarea-97.0, sysmoddate='2016-12-21' where tgeid='0012002'";

        WallCheckResult result = provider.check(sql);
        assertEquals(0, result.getViolations().size());
        String wsql = result
                .getStatementList().get(0).toString();

        assertEquals("UPDATE twogradeestate\n" +
                "SET layarea = layarea + 97.0, selfarea = selfarea - 97.0, sysmoddate = '2016-12-21'\n" +
                "WHERE tgeid = '0012002'", wsql);
    }
}
