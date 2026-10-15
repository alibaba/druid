package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantArithmeticCheckTest {
    @Test
    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateMySql(//
                "SELECT * from t where 3 - 1"));
    }

    @Test
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setConstArithmeticAllow(false);
        assertFalse(WallUtils.isValidateMySql(//
                "SELECT * from t where  3 - 1", config));
    }
}
