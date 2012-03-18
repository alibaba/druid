package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景，检测可疑的Having条件
 * @author wenshao
 *
 */
public class WallReadOnlyTest extends TestCase {
    private WallConfig config = new WallConfig();
    
    protected void setUp() throws Exception {
        config.getReadOnlyTables().add("members");
    }
    private String sql = "SELECT F1, F2 members";
    private String insert_sql = "INSERT INTO members (FID, FNAME) VALUES (?, ?)";
    private String update_sql = "UPDATE members SET FNAME = ? WHERe FID = ?";
    private String delete_sql = "DELETE members WHERE FID = ?";

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config));
        Assert.assertFalse(WallUtils.isValidateMySql(insert_sql, config));
        Assert.assertFalse(WallUtils.isValidateMySql(update_sql, config));
        Assert.assertFalse(WallUtils.isValidateMySql(delete_sql, config));
    }
    
    public void testORACLE() throws Exception {
        Assert.assertTrue(WallUtils.isValidateOracle(sql, config));
        Assert.assertFalse(WallUtils.isValidateOracle(insert_sql, config));
        Assert.assertFalse(WallUtils.isValidateOracle(update_sql, config));
        Assert.assertFalse(WallUtils.isValidateOracle(delete_sql, config));
    }
}
