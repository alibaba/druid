package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BitwiseInvertTest {
    @Test
    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateMySql(//
                "SELECT * from t where ~2"));
    }

    @Test
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setConditionOpBitwiseAllow(false);
        assertFalse(WallUtils.isValidateMySql(//
                "SELECT * from t where ~2", config));
    }
}
