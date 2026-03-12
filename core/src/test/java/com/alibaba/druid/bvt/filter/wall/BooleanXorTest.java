package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanXorTest {
    @Test
    public void test_false() throws Exception {
        assertFalse(WallUtils.isValidateMySql(//
                "SELECT * from t where id = 1 XOR id = 2"));
    }

    @Test
    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setConditionOpXorAllow(true);
        assertTrue(WallUtils.isValidateMySql(//
                "SELECT * from t where id = 1 XOR id = 2", config));
    }
}
