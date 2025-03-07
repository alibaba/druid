package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class GaussDBDenyFunctionTest extends TestCase {
    public void test_false() throws Exception {
        Assert.assertFalse(WallUtils.isValidateGaussDB(//
                "select * from t where fid = 1 union SELECT current_catalog() from t where id = ?")); //
    }

    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setFunctionCheck(false);
        Assert.assertTrue(WallUtils.isValidateGaussDB(//
                "SELECT current_catalog() from t where id = ?", config)); //
    }
}
