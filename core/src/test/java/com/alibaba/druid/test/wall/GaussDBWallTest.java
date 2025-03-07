package com.alibaba.druid.test.wall;

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.GaussDBWallProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Acewuye on 2025/03/06.
 */
public class GaussDBWallTest {
    @Test
    public void testDoublePrecision() throws Exception {
        WallProvider provider = new GaussDBWallProvider(new WallConfig(GaussDBWallProvider.DEFAULT_CONFIG_DIR));
        String sql = "CREATE TABLE test_pg_wall (col_int INT NOT NULL, col_double_x DOUBLE PRECISION NOT NULL DEFAULT 0, col_varchar VARCHAR(200) NULL)";
        WallCheckResult result = provider.check(sql);
        Assert.assertTrue(result.getViolations().isEmpty());
    }
}
