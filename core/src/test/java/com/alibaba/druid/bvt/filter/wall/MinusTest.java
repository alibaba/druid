package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MinusTest {
    @Test
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setIntersectAllow(false);
        assertFalse(WallUtils.isValidateOracle(//
                "SELECT * FROM A Intersect SELECT * FROM B", config));
    }

    @Test
    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateOracle(//
                "SELECT * FROM A Intersect SELECT * FROM B"));
    }
}
