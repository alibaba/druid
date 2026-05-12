package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntersectTest {
    @Test
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setMinusAllow(false);
        assertFalse(WallUtils.isValidateOracle(//
                "SELECT * FROM A MINUS SELECT * FROM B", config));
    }

    @Test
    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateOracle(//
                "SELECT * FROM A MINUS SELECT * FROM B"));
    }
}
