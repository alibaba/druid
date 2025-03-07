package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author Acewuye
 *
 * Notes: Original code of this class based on com.alibaba.druid.bvt.filter.wall.PGWallTest
 */
public class GaussDBWallTest extends TestCase {
    public void test_false() throws Exception {
        Assert.assertTrue(WallUtils.isValidateGaussDB(
                "select wm_concat(article_id) over() from t_nds_web_article"));
    }
}
