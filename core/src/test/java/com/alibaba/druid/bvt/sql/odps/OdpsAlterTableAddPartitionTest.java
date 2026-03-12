package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterTableAddPartitionTest {
    @Test
    public void test_if() throws Exception {
        String sql = "alter table sale_detail add if not exists partition (sale_date='201312', region='hangzhou');";
        assertEquals("ALTER TABLE sale_detail"
                + "\n\tADD IF NOT EXISTS PARTITION (sale_date = '201312', region = 'hangzhou');", SQLUtils.formatOdps(sql));
    }
}
