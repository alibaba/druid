package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class MustParameterizedTest3 extends TestCase {

    private WallConfig config = new WallConfig();

    protected void setUp() throws Exception {
        config.setMustParameterized(true);
    }

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("select * from t where id  = (3 + 5 - 2 - 1)", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select * from t where id  != id + 3", config));
        Assert.assertFalse(WallUtils.isValidateMySql("delete from t where id  != id + 3", config));
        Assert.assertFalse(WallUtils.isValidateMySql("delete from t where id = 'aa' + 'bbb'", config));
        Assert.assertTrue(WallUtils.isValidateMySql("select * from t where id  = ? ORDER BY 1", config));
        Assert.assertTrue(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ?", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = 7", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ? union select * from t", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ? union select * from t fid = fid", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ? union select * from t fid > 5", config));
    }

}
