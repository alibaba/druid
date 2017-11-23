package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlWallTest146 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "ALTER TABLE project_measures\n" +
                "        DROP COLUMN diff_value_1,\n" +
                "        DROP COLUMN diff_value_2,\n" +
                "        DROP COLUMN diff_value_3,\n" +
                "        ADD COLUMN variation_value_1 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_2 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_3 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_4 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_5 DECIMAL(30, 20) NULL DEFAULT NULL";
        Assert.assertTrue(
                provider.checkValid(sql)
        );
    }
}
