package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class BooleanXorTest extends TestCase {
    public void test_false() throws Exception {
        assertFalse(WallUtils.isValidateMySql(//
                "SELECT * from t where id = 1 XOR id = 2")); //
    }

    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setConditionOpXorAllow(true);
        assertTrue(WallUtils.isValidateMySql(//
                "SELECT * from t where id = 1 XOR id = 2", config)); //
    }
}
