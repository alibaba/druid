package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

public class PGWallTest extends TestCase {

    public void test_false() throws Exception {
        Assert.assertTrue(WallUtils.isValidatePostgres(//
        "select wm_concat(article_id) over() from t_nds_web_article")); //
    }
}
