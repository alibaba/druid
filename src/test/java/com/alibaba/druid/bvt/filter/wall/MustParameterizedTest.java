package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class MustParameterizedTest extends TestCase {

    private String     sql    = "select * from t where id = 3";

    private WallConfig config = new WallConfig();

    protected void setUp() throws Exception {
        config.setMustParameterized(true);
    }

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(sql, config));
    }

    public void testORACLE() throws Exception {

        Assert.assertFalse(WallUtils.isValidateOracle(sql, config));
    }
}
