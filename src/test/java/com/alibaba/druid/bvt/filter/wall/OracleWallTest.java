package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

public class OracleWallTest extends TestCase {

    public void testWall() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select f1, f2 from t union select 1, 2"));
        
        Assert.assertFalse(WallUtils.isValidateOracle("select * from TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from ALL_TABLES where (1=1 or (1+1)=2) and (4=8 or 1=1)"));
    }
}
