package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class HintTest extends TestCase {

    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(false);
        String sql = "select * from person where id = '3'/**/union select 0,1,v from (select 1,2,user/*!() as v*/) a where '1'<>''";
        Assert.assertFalse(WallUtils.isValidateMySql(sql, config)); //
    }

    public void test_true() throws Exception {
        String sql = "select * from person where id = '3'/**/union select 0,1,v from (select 1,2,user/*!() as v*/) a where '1'<>''";
        Assert.assertTrue(WallUtils.isValidateMySql(sql)); //
    }
}
