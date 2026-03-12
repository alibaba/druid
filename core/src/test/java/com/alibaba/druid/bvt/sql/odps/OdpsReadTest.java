package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsReadTest {
    @Test
    public void test_read() throws Exception {
        String sql = "read sale_detail";
        assertEquals("READ sale_detail", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_read_column() throws Exception {
        String sql = "read sale_detail(f0, f1, f2) 10";
        assertEquals("READ sale_detail (f0, f1, f2) 10", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_read_limit() throws Exception {
        String sql = "read sale_detail 10";
        assertEquals("READ sale_detail 10", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_read_partition() throws Exception {
        String sql = "read sale_detail partition(ds='20150701')";
        assertEquals("READ sale_detail PARTITION (ds = '20150701')", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_read_partition_limit() throws Exception {
        String sql = "read sale_detail partition(ds='20150701') 10";
        assertEquals("READ sale_detail PARTITION (ds = '20150701') 10", SQLUtils.formatOdps(sql));
    }
}
