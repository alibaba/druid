package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import static org.junit.*;

import com.alibaba.druid.wall.WallUtils;

public class PGWallTest extends TestCase {
    public void test_false() throws Exception {
        assertTrue(WallUtils.isValidatePostgres(//
                "select wm_concat(article_id) over() from t_nds_web_article")); //
    }
}
