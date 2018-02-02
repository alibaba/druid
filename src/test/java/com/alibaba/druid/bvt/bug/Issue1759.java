package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import junit.framework.TestCase;

/**
 * Created by wenshao on 12/07/2017.
 */
public class Issue1759 extends TestCase {
    public void test_0() throws Exception {
        String sql = "COMMENT ON COLUMN \"TB_CRM_MATERIAL\".\"INVALID_TIME\" IS '生效时间'";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLStatement statement = parser.parseStatement();// 分号之后多语句忽略
        OracleWallProvider provider = new OracleWallProvider();

        WallCheckResult result1 = provider.check(sql);

        assertTrue(result1.getViolations().size() == 0);
    }
}
