package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StrictSyntaxCheckTest {
    @Test
    public void test_syntax() throws Exception {
        assertFalse(WallUtils.isValidateMySql(//
                "SELECT SELECT")); // 部分永真
    }

    @Test
    public void test_syntax_1() throws Exception {
        WallConfig config = new WallConfig();
        config.setStrictSyntaxCheck(false);
        assertTrue(WallUtils.isValidateMySql(//
                "SELECT SELECT", config)); // 部分永真
    }
}
