package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUtilsAddColumnTest {
    @Test
    public void test_select() throws Exception {
        assertEquals("SELECT id, name"
                + "\nFROM t", SQLUtils.addSelectItem("select id from t", "name", null, null));
    }

    @Test
    public void test_select_1() throws Exception {
        assertEquals("SELECT id, name AS XX"
                + "\nFROM t", SQLUtils.addSelectItem("select id from t", "name", "XX", null));
    }

    @Test
    public void test_select_2() throws Exception {
        assertEquals("SELECT id, name AS \"XX W\""
                + "\nFROM t", SQLUtils.addSelectItem("select id from t", "name", "XX W", null));
    }
}
