package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import static org.junit.*;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class PGDenyFunctionTest extends TestCase {
    public void test_false() throws Exception {
        assertFalse(WallUtils.isValidatePostgres(//
                "select * from t where fid = 1 union SELECT current_catalog() from t where id = ?")); //
    }

    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setFunctionCheck(false);
        assertTrue(WallUtils.isValidatePostgres(//
                "SELECT current_catalog() from t where id = ?", config)); //
    }
}
