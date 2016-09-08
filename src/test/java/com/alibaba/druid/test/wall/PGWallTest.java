package com.alibaba.druid.test.wall;

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fuzhenn on 2016/5/10.
 */
public class PGWallTest {
    @Test
    public void testDoublePrecision() throws Exception {
        WallProvider provider = new PGWallProvider(new WallConfig(PGWallProvider.DEFAULT_CONFIG_DIR));
        String sql = "CREATE TABLE test_pg_wall (col_int INT NOT NULL, col_double_x DOUBLE PRECISION NOT NULL DEFAULT 0, col_varchar VARCHAR(200) NULL)";
        WallCheckResult result = provider.check(sql);
        Assert.assertTrue(result.getViolations().size() == 0);
    }
}
