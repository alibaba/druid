package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 测试单行注释的场景
 * @author admin
 *
 */
public class WallLineCommentTest extends TestCase {
    private String sql = "select f1 from t -- ";
    
    public void testOracle() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle(sql));
    }
    
    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(sql));
    }
}
