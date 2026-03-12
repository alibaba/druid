package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PGWallTest {
    @Test
    public void test_false() throws Exception {
        assertTrue(WallUtils.isValidatePostgres(//
                "select wm_concat(article_id) over() from t_nds_web_article"));
    }
}
