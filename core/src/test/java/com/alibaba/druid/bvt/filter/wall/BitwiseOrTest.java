package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class BitwiseOrTest extends TestCase {
    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateMySql(//
                "SELECT * from t where (id = 1) | 2")); //
    }

    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setConditionOpBitwiseAllow(false);
        assertFalse(WallUtils.isValidateMySql(//
                "SELECT * from t where (id = 1) | 2", config)); //
    }
}
