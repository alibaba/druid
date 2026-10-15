package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.wall.spi.WallVisitorUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallVisitorUtilsLargeOrTest {
    @Test
    public void test_largeOr() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append("ID = 1");
        for (int i = 2; i <= 1000 * 10; ++i) {
            buf.append(" OR ID = " + i);
        }

        assertEquals(null, WallVisitorUtils.getValue(SQLUtils.toSQLExpr(buf.toString())));
    }
}
