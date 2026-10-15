package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PGDenyFunctionTest {
    @Test
    public void test_false() throws Exception {
        assertFalse(WallUtils.isValidatePostgres(//
                "select * from t where fid = 1 union SELECT current_catalog() from t where id = ?"));
    }

    @Test
    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setFunctionCheck(false);
        assertTrue(WallUtils.isValidatePostgres(//
                "SELECT current_catalog() from t where id = ?", config));
    }
}
