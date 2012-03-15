package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallUtils;

public class OracleWallTest extends TestCase {

    public void testWall() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select f1, f2 from t union select 1, 2"));
        
        Assert.assertFalse(WallUtils.isValidateOracle("select * from TAB"));
    }
}
